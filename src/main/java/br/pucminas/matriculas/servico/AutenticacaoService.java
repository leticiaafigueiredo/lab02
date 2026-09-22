package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.LoginRequestDTO;
import br.pucminas.matriculas.dto.LoginResponseDTO;
import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.Usuario;
import br.pucminas.matriculas.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AutenticacaoService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas. Usuário não encontrado."));

        if (!usuario.autenticar(request.senha())) {
            throw new BadCredentialsException("Credenciais inválidas. Senha incorreta.");
        }

        String token = jwtService.gerarToken(usuario);

        String ra = null;
        String departamento = null;
        if (usuario instanceof Aluno aluno) {
            ra = aluno.getRa();
        } else if (usuario instanceof Professor prof) {
            departamento = prof.getDepartamento();
        }

        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfil(),
                ra,
                departamento
        );
    }

    public Usuario obterUsuarioPorLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }
}
