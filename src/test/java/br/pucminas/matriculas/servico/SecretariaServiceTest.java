package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.*;
import br.pucminas.matriculas.modelo.*;
import br.pucminas.matriculas.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SecretariaServiceTest {

    @Autowired
    private SecretariaService secretariaService;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Test
    @DisplayName("Deve cadastrar e listar cursos com sucesso")
    void deveCadastrarEListarCursos() {
        CursoDTO novo = new CursoDTO(null, "Sistemas de Informação", 200);
        CursoDTO salvo = secretariaService.cadastrarCurso(novo);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Sistemas de Informação");
        assertThat(salvo.getCreditos()).isEqualTo(200);

        List<CursoDTO> cursos = secretariaService.listarCursos();
        assertThat(cursos).extracting(CursoDTO::getNome).contains("Sistemas de Informação");
    }

    @Test
    @DisplayName("Deve cadastrar e listar professores com sucesso")
    void deveCadastrarEListarProfessores() {
        ProfessorDTO dto = new ProfessorDTO(null, "prof_test", "Prof. Teste", "Computação", "Doutor");
        dto.setSenha("123");
        ProfessorDTO salvo = secretariaService.cadastrarProfessor(dto);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getLogin()).isEqualTo("prof_test");

        List<ProfessorDTO> lista = secretariaService.listarProfessores();
        assertThat(lista).extracting(ProfessorDTO::getLogin).contains("prof_test");
    }

    @Test
    @DisplayName("Deve cadastrar e listar alunos com sucesso")
    void deveCadastrarEListarAlunos() {
        AlunoDTO dto = new AlunoDTO(null, "aluno_test", "Aluno Teste", "9999");
        dto.setSenha("123");
        AlunoDTO salvo = secretariaService.cadastrarAluno(dto);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getRa()).isEqualTo("9999");

        List<AlunoDTO> lista = secretariaService.listarAlunos();
        assertThat(lista).extracting(AlunoDTO::getRa).contains("9999");
    }

    @Test
    @DisplayName("Deve cadastrar disciplina e gerar oferta semestral com sucesso")
    void deveCadastrarDisciplinaEGerarOferta() {
        Curso curso = cursoRepository.save(new Curso("Engenharia Biomédica", 240));
        Professor prof = professorRepository.save(new Professor("prof_temp", "123", "Prof Temp", "ES"));

        DisciplinaDTO discDTO = new DisciplinaDTO(null, "TEMP101", "Tópicos Especiais", curso.getId(), null, prof.getId(), null);
        DisciplinaDTO discSalva = secretariaService.cadastrarDisciplina(discDTO);

        assertThat(discSalva.getId()).isNotNull();

        // Gerar Oferta
        GerarOfertaDTO ofertaDTO = new GerarOfertaDTO("2026.2", discSalva.getId());
        OfertaDisponivelDTO ofertaSalva = secretariaService.gerarOfertaSemestral(ofertaDTO);

        assertThat(ofertaSalva.id()).isNotNull();
        assertThat(ofertaSalva.codigoDisciplina()).isEqualTo("TEMP101");
        assertThat(ofertaSalva.semestre()).isEqualTo("2026.2");
    }

    @Test
    @DisplayName("Deve impedir gerar oferta duplicada no mesmo semestre")
    void deveImpedirOfertaDuplicada() {
        Curso curso = cursoRepository.save(new Curso("Engenharia Mecatrônica", 240));
        Disciplina disc = disciplinaRepository.save(new Disciplina("DUP101", "Disciplina Duplicada", curso));

        GerarOfertaDTO ofertaDTO = new GerarOfertaDTO("2026.2", disc.getId());
        secretariaService.gerarOfertaSemestral(ofertaDTO);

        assertThatThrownBy(() -> secretariaService.gerarOfertaSemestral(ofertaDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe oferta gerada");
    }
}
