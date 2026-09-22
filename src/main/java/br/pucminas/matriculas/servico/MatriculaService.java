package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.AlunoDashboardDTO;
import br.pucminas.matriculas.dto.MatriculaRequestDTO;
import br.pucminas.matriculas.dto.MatriculaResponseDTO;
import br.pucminas.matriculas.dto.OfertaDisponivelDTO;
import br.pucminas.matriculas.modelo.*;
import br.pucminas.matriculas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final PeriodoMatriculasRepository periodoRepository;
    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CobrancaService cobrancaService;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            OfertaDisciplinaRepository ofertaRepository,
            PeriodoMatriculasRepository periodoRepository,
            AlunoRepository alunoRepository,
            UsuarioRepository usuarioRepository,
            CobrancaService cobrancaService) {
        this.matriculaRepository = matriculaRepository;
        this.ofertaRepository = ofertaRepository;
        this.periodoRepository = periodoRepository;
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cobrancaService = cobrancaService;
    }

    @Transactional
    public MatriculaResponseDTO matricular(String alunoLogin, MatriculaRequestDTO request) {
        PeriodoMatriculas periodo = obterPeriodoAberto();
        Aluno aluno = resolverAluno(alunoLogin);

        OfertaDisciplina oferta = ofertaRepository.findById(request.ofertaId())
                .orElseThrow(() -> new IllegalArgumentException("Oferta de disciplina não encontrada."));

        if (!oferta.getSemestre().equals(periodo.getSemestre())) {
            throw new IllegalArgumentException("A oferta selecionada (" + oferta.getSemestre() + 
                    ") não pertence ao semestre aberto vigente (" + periodo.getSemestre() + ").");
        }

        long totalInscritos = matriculaRepository.countByOfertaId(oferta.getId());
        if (totalInscritos >= Disciplina.MAXIMO_ALUNOS || oferta.getStatus() == StatusOferta.VAGAS_ENCERRADAS) {
            oferta.setStatus(StatusOferta.VAGAS_ENCERRADAS);
            ofertaRepository.save(oferta);
            throw new IllegalStateException("As inscrições desta disciplina estão encerradas. Limite de " +
                    Disciplina.MAXIMO_ALUNOS + " alunos atingido.");
        }

        if (matriculaRepository.existsByAlunoIdAndOfertaId(aluno.getId(), oferta.getId())) {
            throw new IllegalStateException("O aluno já está matriculado nesta disciplina.");
        }

        long totalObrigatorias = matriculaRepository.countByAlunoIdAndSemestreAndTipo(
                aluno.getId(), periodo.getSemestre(), TipoMatricula.OBRIGATORIA);
        long totalOptativas = matriculaRepository.countByAlunoIdAndSemestreAndTipo(
                aluno.getId(), periodo.getSemestre(), TipoMatricula.OPTATIVA);

        if (request.tipo() == TipoMatricula.OBRIGATORIA && totalObrigatorias >= RegrasMatricula.MAX_OBRIGATORIAS) {
            throw new IllegalStateException("Limite de " + RegrasMatricula.MAX_OBRIGATORIAS + 
                    " disciplinas obrigatórias (1ª opção) atingido para este semestre.");
        }

        if (request.tipo() == TipoMatricula.OPTATIVA && totalOptativas >= RegrasMatricula.MAX_OPTATIVAS) {
            throw new IllegalStateException("Limite de " + RegrasMatricula.MAX_OPTATIVAS + 
                    " disciplinas optativas (alternativas) atingido para este semestre.");
        }

        Matricula matricula = new Matricula(aluno, oferta, request.tipo());
        matricula = matriculaRepository.save(matricula);

        if (totalInscritos + 1 >= Disciplina.MAXIMO_ALUNOS) {
            oferta.setStatus(StatusOferta.VAGAS_ENCERRADAS);
            ofertaRepository.save(oferta);
        }

        cobrancaService.notificarInscricao(
                aluno,
                oferta.getDisciplina().getNome(),
                periodo.getSemestre(),
                request.tipo()
        );

        return toDTO(matricula);
    }

    @Transactional
    public void cancelar(String alunoLogin, String matriculaId) {
        PeriodoMatriculas periodo = obterPeriodoAberto();
        Aluno aluno = resolverAluno(alunoLogin);

        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));

        if (!matricula.getAluno().getId().equals(aluno.getId())) {
            throw new IllegalStateException("Você não tem permissão para cancelar uma matrícula de outro aluno.");
        }

        OfertaDisciplina oferta = matricula.getOferta();
        if (!oferta.getSemestre().equals(periodo.getSemestre())) {
            throw new IllegalStateException("Só é permitido cancelar matrículas do semestre vigente.");
        }

        String nomeDisciplina = oferta.getDisciplina().getNome();
        matriculaRepository.delete(matricula);

        long inscritosRestantes = matriculaRepository.countByOfertaId(oferta.getId());
        if (oferta.getStatus() == StatusOferta.VAGAS_ENCERRADAS && inscritosRestantes < Disciplina.MAXIMO_ALUNOS) {
            oferta.setStatus(StatusOferta.ABERTA);
            ofertaRepository.save(oferta);
        }

        cobrancaService.notificarCancelamento(aluno, nomeDisciplina, periodo.getSemestre());
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listarMinhasMatriculas(String alunoLogin) {
        Aluno aluno = resolverAluno(alunoLogin);

        PeriodoMatriculas periodo = periodoRepository.findFirstByOrderBySemestreDesc()
                .orElse(new PeriodoMatriculas("2026.2", true));

        List<Matricula> matriculas = matriculaRepository.findByAlunoIdAndSemestre(aluno.getId(), periodo.getSemestre());
        return matriculas.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<OfertaDisponivelDTO> listarOfertasDisponiveis(String alunoLogin) {
        PeriodoMatriculas periodo = periodoRepository.findFirstByOrderBySemestreDesc()
                .orElse(new PeriodoMatriculas("2026.2", true));

        Aluno aluno = null;
        if (alunoLogin != null) {
            try {
                aluno = resolverAluno(alunoLogin);
            } catch (Exception ignored) {}
        }

        List<OfertaDisciplina> ofertas = ofertaRepository.findBySemestre(periodo.getSemestre());
        final Aluno finalAluno = aluno;

        return ofertas.stream().map(oferta -> {
            long inscritos = matriculaRepository.countByOfertaId(oferta.getId());
            Optional<Matricula> matOpt = finalAluno != null ?
                    matriculaRepository.findByAlunoIdAndOfertaId(finalAluno.getId(), oferta.getId()) :
                    Optional.empty();

            boolean jaMatriculado = matOpt.isPresent();
            String matId = matOpt.map(Matricula::getId).orElse(null);
            TipoMatricula tipoMat = matOpt.map(Matricula::getTipo).orElse(null);

            Disciplina d = oferta.getDisciplina();
            String profNome = d.getProfessor() != null ? d.getProfessor().getNome() : "A definir";
            String cursoNome = d.getCurso() != null ? d.getCurso().getNome() : "Engenharia de Software";

            return new OfertaDisponivelDTO(
                    oferta.getId(),
                    oferta.getSemestre(),
                    d.getCodigo(),
                    d.getNome(),
                    cursoNome,
                    profNome,
                    oferta.getStatus(),
                    (int) inscritos,
                    Math.max(0, Disciplina.MAXIMO_ALUNOS - (int) inscritos),
                    Disciplina.MAXIMO_ALUNOS,
                    jaMatriculado,
                    matId,
                    tipoMat
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public AlunoDashboardDTO obterDashboardAluno(String alunoLogin) {
        Aluno aluno = resolverAluno(alunoLogin);

        PeriodoMatriculas periodo = periodoRepository.findFirstByOrderBySemestreDesc()
                .orElse(new PeriodoMatriculas("2026.2", true));

        List<Matricula> matriculas = matriculaRepository.findByAlunoIdAndSemestre(aluno.getId(), periodo.getSemestre());
        long obrigatorias = matriculas.stream().filter(m -> m.getTipo() == TipoMatricula.OBRIGATORIA).count();
        long optativas = matriculas.stream().filter(m -> m.getTipo() == TipoMatricula.OPTATIVA).count();

        List<MatriculaResponseDTO> matriculasDTO = matriculas.stream().map(this::toDTO).toList();

        return new AlunoDashboardDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getRa(),
                periodo.getSemestre(),
                periodo.isAberto(),
                (int) obrigatorias,
                RegrasMatricula.MAX_OBRIGATORIAS,
                (int) optativas,
                RegrasMatricula.MAX_OPTATIVAS,
                matriculasDTO
        );
    }

    private Aluno resolverAluno(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login do usuário não informado.");
        }
        return alunoRepository.findByLogin(login.trim())
                .orElseGet(() -> {
                    Usuario u = usuarioRepository.findByLogin(login.trim())
                            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado para o login: " + login));
                    if (u instanceof Aluno a) {
                        return a;
                    }
                    throw new IllegalStateException("O usuário '" + login + "' não possui perfil de Aluno.");
                });
    }

    private PeriodoMatriculas obterPeriodoAberto() {
        PeriodoMatriculas periodo = periodoRepository.findFirstByOrderBySemestreDesc()
                .orElseThrow(() -> new IllegalStateException("Nenhum período de matrícula cadastrado no sistema."));

        if (!periodo.isAberto()) {
            throw new IllegalStateException("O período de matrículas (" + periodo.getSemestre() + ") está encerrado.");
        }

        return periodo;
    }

    private MatriculaResponseDTO toDTO(Matricula m) {
        Disciplina d = m.getOferta().getDisciplina();
        String profNome = d.getProfessor() != null ? d.getProfessor().getNome() : "A definir";
        String cursoNome = d.getCurso() != null ? d.getCurso().getNome() : "Engenharia de Software";

        return new MatriculaResponseDTO(
                m.getId(),
                m.getOferta().getId(),
                d.getCodigo(),
                d.getNome(),
                profNome,
                cursoNome,
                m.getOferta().getSemestre(),
                m.getTipo(),
                m.getDataHora()
        );
    }
}
