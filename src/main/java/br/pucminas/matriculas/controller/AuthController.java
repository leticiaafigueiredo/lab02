package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.dto.LoginRequestDTO;
import br.pucminas.matriculas.dto.LoginResponseDTO;
import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.Usuario;
import br.pucminas.matriculas.servico.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticacaoService authService;

    public AuthController(AutenticacaoService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.autenticar(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String login = authentication.getName();
        Usuario usuario = authService.obterUsuarioPorLogin(login);
        String ra = (usuario instanceof Aluno a) ? a.getRa() : null;
        String dep = (usuario instanceof Professor p) ? p.getDepartamento() : null;

        LoginResponseDTO response = new LoginResponseDTO(
                null,
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfil(),
                ra,
                dep
        );
        return ResponseEntity.ok(response);
    }
}
