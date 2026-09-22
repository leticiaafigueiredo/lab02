package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.dto.AlunoDashboardDTO;
import br.pucminas.matriculas.dto.MatriculaRequestDTO;
import br.pucminas.matriculas.dto.MatriculaResponseDTO;
import br.pucminas.matriculas.servico.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> matricular(
            @Valid @RequestBody MatriculaRequestDTO request,
            Authentication authentication) {
        String login = authentication.getName();
        MatriculaResponseDTO response = matriculaService.matricular(login, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable String id,
            Authentication authentication) {
        String login = authentication.getName();
        matriculaService.cancelar(login, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<MatriculaResponseDTO>> listarMinhas(Authentication authentication) {
        String login = authentication.getName();
        List<MatriculaResponseDTO> matriculas = matriculaService.listarMinhasMatriculas(login);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AlunoDashboardDTO> dashboard(Authentication authentication) {
        String login = authentication.getName();
        AlunoDashboardDTO dashboard = matriculaService.obterDashboardAluno(login);
        return ResponseEntity.ok(dashboard);
    }
}
