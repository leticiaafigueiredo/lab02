package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.dto.TurmaProfessorDTO;
import br.pucminas.matriculas.servico.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/turmas")
    public ResponseEntity<List<TurmaProfessorDTO>> listarTurmas(Authentication authentication) {
        String login = authentication.getName();
        List<TurmaProfessorDTO> turmas = professorService.listarTurmasDoProfessor(login);
        return ResponseEntity.ok(turmas);
    }
}
