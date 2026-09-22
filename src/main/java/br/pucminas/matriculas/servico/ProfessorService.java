package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.TurmaProfessorDTO;
import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.repository.MatriculaRepository;
import br.pucminas.matriculas.repository.OfertaDisciplinaRepository;
import br.pucminas.matriculas.repository.PeriodoMatriculasRepository;
import br.pucminas.matriculas.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final MatriculaRepository matriculaRepository;
    private final PeriodoMatriculasRepository periodoRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProfessorService(
            ProfessorRepository professorRepository,
            OfertaDisciplinaRepository ofertaRepository,
            MatriculaRepository matriculaRepository,
            PeriodoMatriculasRepository periodoRepository) {
        this.professorRepository = professorRepository;
        this.ofertaRepository = ofertaRepository;
        this.matriculaRepository = matriculaRepository;
        this.periodoRepository = periodoRepository;
    }

    @Transactional(readOnly = true)
    public List<TurmaProfessorDTO> listarTurmasDoProfessor(String login) {
        Professor professor = professorRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado para o login: " + login));

        String semestre = periodoRepository.findFirstByOrderBySemestreDesc()
                .map(p -> p.getSemestre())
                .orElse("2026.2");

        List<OfertaDisciplina> ofertas = ofertaRepository.findByProfessorIdAndSemestre(professor.getId(), semestre);

        return ofertas.stream().map(oferta -> {
            List<Matricula> matriculas = matriculaRepository.findByOfertaId(oferta.getId());
            List<TurmaProfessorDTO.AlunoInscritoDTO> alunosDTO = matriculas.stream().map(m ->
                    new TurmaProfessorDTO.AlunoInscritoDTO(
                            m.getId(),
                            m.getAluno().getId(),
                            m.getAluno().getNome(),
                            m.getAluno().getLogin(),
                            m.getAluno().getRa(),
                            m.getTipo().name(),
                            m.getDataHora() != null ? m.getDataHora().format(FORMATTER) : ""
                    )
            ).toList();

            return new TurmaProfessorDTO(
                    oferta.getId(),
                    oferta.getDisciplina().getCodigo(),
                    oferta.getDisciplina().getNome(),
                    oferta.getDisciplina().getCurso() != null ? oferta.getDisciplina().getCurso().getNome() : "Geral",
                    oferta.getSemestre(),
                    oferta.getStatus(),
                    alunosDTO.size(),
                    alunosDTO
            );
        }).toList();
    }
}
