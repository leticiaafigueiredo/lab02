package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.StatusOferta;
import br.pucminas.matriculas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecretariaService {

    private final PeriodoMatriculasRepository periodoRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final MatriculaRepository matriculaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final UsuarioRepository usuarioRepository;

    public SecretariaService(
            PeriodoMatriculasRepository periodoRepository,
            OfertaDisciplinaRepository ofertaRepository,
            MatriculaRepository matriculaRepository,
            DisciplinaRepository disciplinaRepository,
            UsuarioRepository usuarioRepository) {
        this.periodoRepository = periodoRepository;
        this.ofertaRepository = ofertaRepository;
        this.matriculaRepository = matriculaRepository;
        this.disciplinaRepository = disciplinaRepository;
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
        return resumo;
    }
}
