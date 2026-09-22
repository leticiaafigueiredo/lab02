package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.servico.SecretariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/secretaria")
public class SecretariaController {

    private final SecretariaService secretariaService;

    public SecretariaController(SecretariaService secretariaService) {
        this.secretariaService = secretariaService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> obterResumo() {
        return ResponseEntity.ok(secretariaService.obterResumo());
    }

    @PostMapping("/encerrar-periodo")
    public ResponseEntity<Void> encerrarPeriodo(@RequestParam(defaultValue = "2026.2") String semestre) {
        secretariaService.encerrarPeriodoComQuorum(semestre);
        return ResponseEntity.ok().build();
    }
}
