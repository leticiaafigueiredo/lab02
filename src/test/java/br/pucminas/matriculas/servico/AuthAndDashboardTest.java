package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.dto.AlunoDashboardDTO;
import br.pucminas.matriculas.dto.LoginRequestDTO;
import br.pucminas.matriculas.dto.LoginResponseDTO;
import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthAndDashboardTest {

    @Autowired
    private AutenticacaoService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Test
    void testAuthAndDashboard() {
        LoginResponseDTO res = authService.autenticar(new LoginRequestDTO("aluno1", "123"));
        assertNotNull(res.token());
        
        String extractedLogin = jwtService.extrairLogin(res.token());
        assertEquals("aluno1", extractedLogin);

        Aluno aluno = alunoRepository.findByLogin(extractedLogin).orElse(null);
        assertNotNull(aluno, "Aluno deve ser encontrado por login aluno1");

        AlunoDashboardDTO dash = matriculaService.obterDashboardAluno(extractedLogin);
        assertNotNull(dash);
        assertEquals("João Silva", dash.alunoNome());
    }
}
