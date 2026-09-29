package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.*;
import br.pucminas.matriculas.modelo.*;
import br.pucminas.matriculas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SecretariaService {

    private final PeriodoMatriculasRepository periodoRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final MatriculaRepository matriculaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;

    public SecretariaService(
            PeriodoMatriculasRepository periodoRepository,
            OfertaDisciplinaRepository ofertaRepository,
            MatriculaRepository matriculaRepository,
            DisciplinaRepository disciplinaRepository,
            CursoRepository cursoRepository,
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            UsuarioRepository usuarioRepository) {
        this.periodoRepository = periodoRepository;
        this.ofertaRepository = ofertaRepository;
        this.matriculaRepository = matriculaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PeriodoMatriculas alternarPeriodo(String semestre) {
        PeriodoMatriculas periodo = periodoRepository.findBySemestre(semestre)
                .orElseGet(() -> new PeriodoMatriculas(semestre, true));

        periodo.setAberto(!periodo.isAberto());
        return periodoRepository.save(periodo);
    }

    @Transactional
    public void encerrarPeriodoComQuorum(String semestre) {
        PeriodoMatriculas periodo = periodoRepository.findBySemestre(semestre)
                .orElseThrow(() -> new IllegalArgumentException("Período não encontrado: " + semestre));

        periodo.setAberto(false);
        periodoRepository.save(periodo);

        // Aplica regra de quórum (mínimo de 3 alunos inscritos)
        List<OfertaDisciplina> ofertas = ofertaRepository.findBySemestre(semestre);
        for (OfertaDisciplina oferta : ofertas) {
            long totalInscritos = matriculaRepository.countByOfertaId(oferta.getId());
            if (totalInscritos >= Disciplina.MINIMO_ALUNOS) {
                oferta.setStatus(StatusOferta.ATIVA);
            } else {
                oferta.setStatus(StatusOferta.CANCELADA);
            }
            ofertaRepository.save(oferta);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterResumo() {
        PeriodoMatriculas periodo = periodoRepository.findFirstByOrderBySemestreDesc()
                .orElse(new PeriodoMatriculas("2026.2", true));

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("periodo", periodo);
        resumo.put("totalOfertas", ofertaRepository.count());
        resumo.put("totalDisciplinas", disciplinaRepository.count());
        resumo.put("totalMatriculas", matriculaRepository.count());
        resumo.put("totalUsuarios", usuarioRepository.count());
        resumo.put("totalCursos", cursoRepository.count());
        resumo.put("totalProfessores", professorRepository.count());
        resumo.put("totalAlunos", alunoRepository.count());
        return resumo;
    }

    // --- Gestão de Cursos ---
    @Transactional(readOnly = true)
    public List<CursoDTO> listarCursos() {
        return cursoRepository.findAll().stream()
                .map(c -> new CursoDTO(c.getId(), c.getNome(), c.getCreditos()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CursoDTO cadastrarCurso(CursoDTO dto) {
        Curso curso = new Curso(dto.getNome(), dto.getCreditos());
        curso = cursoRepository.save(curso);
        return new CursoDTO(curso.getId(), curso.getNome(), curso.getCreditos());
    }

    // --- Gestão de Professores ---
    @Transactional(readOnly = true)
    public List<ProfessorDTO> listarProfessores() {
        return professorRepository.findAll().stream()
                .map(p -> new ProfessorDTO(p.getId(), p.getLogin(), p.getNome(), p.getDepartamento(), p.getTitulacao()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfessorDTO cadastrarProfessor(ProfessorDTO dto) {
        if (usuarioRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Login já existente: " + dto.getLogin());
        }
        Professor prof = new Professor(dto.getLogin(), dto.getSenha(), dto.getNome(), dto.getDepartamento());
        prof.setTitulacao(dto.getTitulacao());
        prof = professorRepository.save(prof);
        return new ProfessorDTO(prof.getId(), prof.getLogin(), prof.getNome(), prof.getDepartamento(), prof.getTitulacao());
    }

    // --- Gestão de Alunos ---
    @Transactional(readOnly = true)
    public List<AlunoDTO> listarAlunos() {
        return alunoRepository.findAll().stream()
                .map(a -> new AlunoDTO(a.getId(), a.getLogin(), a.getNome(), a.getRa()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AlunoDTO cadastrarAluno(AlunoDTO dto) {
        if (usuarioRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Login já existente: " + dto.getLogin());
        }
        Aluno aluno = new Aluno(dto.getLogin(), dto.getSenha(), dto.getNome(), dto.getRa());
        aluno = alunoRepository.save(aluno);
        return new AlunoDTO(aluno.getId(), aluno.getLogin(), aluno.getNome(), aluno.getRa());
    }

    // --- Gestão de Disciplinas ---
    @Transactional(readOnly = true)
    public List<DisciplinaDTO> listarDisciplinas() {
        return disciplinaRepository.findAll().stream()
                .map(d -> new DisciplinaDTO(
                        d.getId(),
                        d.getCodigo(),
                        d.getNome(),
                        d.getCurso() != null ? d.getCurso().getId() : null,
                        d.getCurso() != null ? d.getCurso().getNome() : null,
                        d.getProfessor() != null ? d.getProfessor().getId() : null,
                        d.getProfessor() != null ? d.getProfessor().getNome() : null
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public DisciplinaDTO cadastrarDisciplina(DisciplinaDTO dto) {
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado com ID: " + dto.getCursoId()));

        Professor prof = null;
        if (dto.getProfessorId() != null && !dto.getProfessorId().isBlank()) {
            prof = professorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado com ID: " + dto.getProfessorId()));
        }

        Disciplina disciplina = new Disciplina(dto.getCodigo(), dto.getNome(), curso, prof);
        disciplina = disciplinaRepository.save(disciplina);

        return new DisciplinaDTO(
                disciplina.getId(),
                disciplina.getCodigo(),
                disciplina.getNome(),
                curso.getId(),
                curso.getNome(),
                prof != null ? prof.getId() : null,
                prof != null ? prof.getNome() : null
        );
    }

    // --- Geração de Currículo / Ofertas Semestrais ---
    @Transactional
    public OfertaDisponivelDTO gerarOfertaSemestral(GerarOfertaDTO dto) {
        Disciplina disciplina = disciplinaRepository.findById(dto.getDisciplinaId())
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada com ID: " + dto.getDisciplinaId()));

        // Verificar se já existe oferta desta disciplina no semestre
        List<OfertaDisciplina> existentes = ofertaRepository.findBySemestre(dto.getSemestre());
        boolean jaExiste = existentes.stream().anyMatch(o -> o.getDisciplina().getId().equals(disciplina.getId()));
        if (jaExiste) {
            throw new IllegalArgumentException("Já existe oferta gerada para a disciplina " + disciplina.getNome() + " no semestre " + dto.getSemestre());
        }

        OfertaDisciplina oferta = new OfertaDisciplina(dto.getSemestre(), disciplina);
        oferta.setStatus(StatusOferta.ABERTA);
        oferta = ofertaRepository.save(oferta);

        return new OfertaDisponivelDTO(
                oferta.getId(),
                oferta.getSemestre(),
                disciplina.getCodigo(),
                disciplina.getNome(),
                disciplina.getCurso() != null ? disciplina.getCurso().getNome() : "",
                disciplina.getProfessor() != null ? disciplina.getProfessor().getNome() : "A definir",
                oferta.getStatus(),
                0,
                Disciplina.MAXIMO_ALUNOS,
                Disciplina.MAXIMO_ALUNOS,
                false,
                null,
                null
        );
    }
}
