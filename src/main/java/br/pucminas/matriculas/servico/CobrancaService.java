package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.TipoMatricula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CobrancaService {

    private static final Logger log = LoggerFactory.getLogger(CobrancaService.class);
    private static final String ARQUIVO_LOG = "data/cobrancas.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void notificarInscricao(Aluno aluno, String nomeDisciplina, String semestre, TipoMatricula tipo) {
        String dataHora = LocalDateTime.now().format(FORMATTER);
        String mensagem = String.format(
            "[%s] COBRANCA_NOTIFICACAO_INSCRICAO -> Aluno: %s (RA: %s, Login: %s) | Disciplina: %s | Semestre: %s | Tipo: %s",
            dataHora, aluno.getNome(), aluno.getRa(), aluno.getLogin(), nomeDisciplina, semestre, tipo
        );

        log.info("{}", mensagem);
        gravarLogArquivo(mensagem);
    }

    public void notificarCancelamento(Aluno aluno, String nomeDisciplina, String semestre) {
        String dataHora = LocalDateTime.now().format(FORMATTER);
        String mensagem = String.format(
            "[%s] COBRANCA_NOTIFICACAO_CANCELAMENTO -> Aluno: %s (RA: %s, Login: %s) | Disciplina: %s | Semestre: %s",
            dataHora, aluno.getNome(), aluno.getRa(), aluno.getLogin(), nomeDisciplina, semestre
        );

        log.info("{}", mensagem);
        gravarLogArquivo(mensagem);
    }

    private synchronized void gravarLogArquivo(String mensagem) {
        try {
            File file = new File(ARQUIVO_LOG);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(mensagem);
            }
        } catch (IOException e) {
            log.error("Erro ao gravar notificação em {}: {}", ARQUIVO_LOG, e.getMessage());
        }
    }
}
