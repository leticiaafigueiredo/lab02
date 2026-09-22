package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.dto.PeriodoDTO;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.repository.PeriodoMatriculasRepository;
import br.pucminas.matriculas.servico.SecretariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/periodo")
public class PeriodoController {

    private final PeriodoMatriculasRepository periodoRepository;
    private final SecretariaService secretariaService;

    public PeriodoController(PeriodoMatriculasRepository periodoRepository, SecretariaService secretariaService) {
        this.periodoRepository = periodoRepository;
        this.secretariaService = secretariaService;
    }

    @GetMapping("/atual")
    public ResponseEntity<PeriodoDTO> obterAtual() {
        PeriodoMatriculas p = periodoRepository.findFirstByOrderBySemestreDesc()
                .orElse(new PeriodoMatriculas("2026.2", true));
        return ResponseEntity.ok(new PeriodoDTO(p.getId(), p.getSemestre(), p.isAberto()));
    }

    @PostMapping("/toggle")
    public ResponseEntity<PeriodoDTO> togglePeriodo(@RequestParam(defaultValue = "2026.2") String semestre) {
        PeriodoMatriculas p = secretariaService.alternarPeriodo(semestre);
        return ResponseEntity.ok(new PeriodoDTO(p.getId(), p.getSemestre(), p.isAberto()));
    }
}
