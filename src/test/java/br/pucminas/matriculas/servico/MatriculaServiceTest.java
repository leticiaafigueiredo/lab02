package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.MatriculaRequestDTO;
import br.pucminas.matriculas.dto.MatriculaResponseDTO;
import br.pucminas.matriculas.modelo.*;
import br.pucminas.matriculas.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MatriculaServiceTest {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private SecretariaService secretariaService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaRepository;

    @Autowired
    private PeriodoMatriculasRepository periodoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    private Aluno alunoTest;
    private OfertaDisciplina oferta1;
    private OfertaDisciplina oferta2;
    private OfertaDisciplina oferta3;
    private OfertaDisciplina oferta4;
    private OfertaDisciplina oferta5;
    private OfertaDisciplina oferta6;

    @BeforeEach
    void setup() {
        matriculaRepository.deleteAll();

        // Garantir período 2026.2 aberto
        PeriodoMatriculas periodo = periodoRepository.findBySemestre("2026.2")
                .orElseGet(() -> new PeriodoMatriculas("2026.2", true));
        periodo.setAberto(true);
        periodoRepository.save(periodo);

        alunoTest = alunoRepository.findByLogin("aluno1").orElseGet(() -> {
            Aluno a = new Aluno("aluno1", "123", "João Silva", "1001");
            return alunoRepository.save(a);
        });

        var ofertas = ofertaRepository.findBySemestre("2026.2");
        oferta1 = ofertas.get(0);
        oferta2 = ofertas.get(1);
        oferta3 = ofertas.get(2);
        oferta4 = ofertas.get(3);
        oferta5 = ofertas.get(4);
        oferta6 = ofertas.size() > 5 ? ofertas.get(5) : ofertas.get(0);
    }

    @Test
    @DisplayName("RF01 - Deve matricular aluno com sucesso em disciplina obrigatória")
    void deveMatricularComSucessoObrigatoria() {
        MatriculaRequestDTO req = new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA);
        MatriculaResponseDTO res = matriculaService.matricular(alunoTest.getLogin(), req);

        assertNotNull(res.id());
        assertEquals(oferta1.getId(), res.ofertaId());
        assertEquals(TipoMatricula.OBRIGATORIA, res.tipo());
        assertEquals("2026.2", res.semestre());
        assertTrue(matriculaRepository.existsByAlunoIdAndOfertaId(alunoTest.getId(), oferta1.getId()));
    }

    @Test
    @DisplayName("RF01 - Deve matricular aluno com sucesso em disciplina optativa")
    void deveMatricularComSucessoOptativa() {
        MatriculaRequestDTO req = new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OPTATIVA);
        MatriculaResponseDTO res = matriculaService.matricular(alunoTest.getLogin(), req);

        assertNotNull(res.id());
        assertEquals(TipoMatricula.OPTATIVA, res.tipo());
    }

    @Test
    @DisplayName("RF01 - Deve bloquear matrícula duplicada na mesma oferta")
    void deveBloquearMatriculaDuplicada() {
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OPTATIVA))
        );
        assertTrue(ex.getMessage().contains("já está matriculado"));
    }

    @Test
    @DisplayName("RF01 - Regra 1: Deve bloquear quando ultrapassar o limite de 4 disciplinas obrigatórias")
    void deveBloquearAoUltrapassarLimiteObrigatorias() {
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA));
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta2.getId(), TipoMatricula.OBRIGATORIA));
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta3.getId(), TipoMatricula.OBRIGATORIA));
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta4.getId(), TipoMatricula.OBRIGATORIA));

        // A 5ª obrigatória deve ser recusada
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta5.getId(), TipoMatricula.OBRIGATORIA))
        );
        assertTrue(ex.getMessage().contains("Limite de 4 disciplinas obrigatórias"));
    }

    @Test
    @DisplayName("RF01 - Regra 1: Deve bloquear quando ultrapassar o limite de 2 disciplinas optativas")
    void deveBloquearAoUltrapassarLimiteOptativas() {
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OPTATIVA));
        matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta2.getId(), TipoMatricula.OPTATIVA));

        // A 3ª optativa deve ser recusada
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta3.getId(), TipoMatricula.OPTATIVA))
        );
        assertTrue(ex.getMessage().contains("Limite de 2 disciplinas optativas"));
    }

    @Test
    @DisplayName("RF01 - Regra 4: Deve bloquear matrícula quando período estiver fechado")
    void deveBloquearMatriculaComPeriodoFechado() {
        PeriodoMatriculas p = periodoRepository.findBySemestre("2026.2").get();
        p.setAberto(false);
        periodoRepository.save(p);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA))
        );
        assertTrue(ex.getMessage().contains("encerrado") || ex.getMessage().contains("fechado"));
    }

    @Test
    @DisplayName("RF01 - Deve permitir cancelamento com período aberto e bloquear com período fechado")
    void devePermitirCancelamentoComPeriodoAberto() {
        MatriculaResponseDTO res = matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA));
        assertEquals(1, matriculaRepository.findByAlunoId(alunoTest.getId()).size());

        matriculaService.cancelar(alunoTest.getLogin(), res.id());
        assertEquals(0, matriculaRepository.findByAlunoId(alunoTest.getId()).size());

        // Recria a matrícula
        MatriculaResponseDTO res2 = matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA));

        // Fecha o período
        PeriodoMatriculas p = periodoRepository.findBySemestre("2026.2").get();
        p.setAberto(false);
        periodoRepository.save(p);

        // Cancelamento deve falhar com período fechado
        assertThrows(IllegalStateException.class, () ->
                matriculaService.cancelar(alunoTest.getLogin(), res2.id())
        );
    }

    @Test
    @DisplayName("RF01 - Regra 2: Deve bloquear matrícula quando oferta atingir 60 inscritos")
    void deveBloquearQuandoOfertaCheia() {
        // Criar 60 alunos fictícios e matricular
        for (int i = 1; i <= 60; i++) {
            Aluno fake = new Aluno("fake_aluno_" + i, "123", "Fake " + i, "RA" + i);
            fake = alunoRepository.save(fake);
            Matricula m = new Matricula(fake, oferta1, TipoMatricula.OBRIGATORIA);
            matriculaRepository.save(m);
        }

        // Tentativa de matrícula do alunoTest na oferta cheia (60/60)
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                matriculaService.matricular(alunoTest.getLogin(), new MatriculaRequestDTO(oferta1.getId(), TipoMatricula.OBRIGATORIA))
        );
        assertTrue(ex.getMessage().contains("encerradas") || ex.getMessage().contains("Limite de 60"));
    }

    @Test
    @DisplayName("Regra 3 (PO): Regra de quórum ao encerrar período (>= 3 ativa, < 3 cancelada)")
    void deveAplicarRegraDeQuorumAoEncerrarPeriodo() {
        Aluno a1 = alunoRepository.findByLogin("aluno1").get();
        Aluno a2 = alunoRepository.findByLogin("aluno2").get();
        Aluno a3 = alunoRepository.findByLogin("aluno3").get();

        // Oferta 1 recebe 3 alunos
        matriculaRepository.save(new Matricula(a1, oferta1, TipoMatricula.OBRIGATORIA));
        matriculaRepository.save(new Matricula(a2, oferta1, TipoMatricula.OBRIGATORIA));
        matriculaRepository.save(new Matricula(a3, oferta1, TipoMatricula.OBRIGATORIA));

        // Oferta 2 recebe apenas 1 aluno
        matriculaRepository.save(new Matricula(a1, oferta2, TipoMatricula.OBRIGATORIA));

        // Encerrar período via secretaria
        secretariaService.encerrarPeriodoComQuorum("2026.2");

        OfertaDisciplina o1Atualizada = ofertaRepository.findById(oferta1.getId()).get();
        OfertaDisciplina o2Atualizada = ofertaRepository.findById(oferta2.getId()).get();

        assertEquals(StatusOferta.ATIVA, o1Atualizada.getStatus());
        assertEquals(StatusOferta.CANCELADA, o2Atualizada.getStatus());
    }
}
