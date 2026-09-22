package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.dto.OfertaDisponivelDTO;
import br.pucminas.matriculas.servico.MatriculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

    private final MatriculaService matriculaService;

    public OfertaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<OfertaDisponivelDTO>> listarDisponiveis(Authentication authentication) {
        String login = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
        List<OfertaDisponivelDTO> ofertas = matriculaService.listarOfertasDisponiveis(login);
        return ResponseEntity.ok(ofertas);
    }
}
