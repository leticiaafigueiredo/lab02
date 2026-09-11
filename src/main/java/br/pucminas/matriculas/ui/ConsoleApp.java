package br.pucminas.matriculas.ui;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.Usuario;
import br.pucminas.matriculas.persistencia.DadosIniciais;
import br.pucminas.matriculas.persistencia.Repositorio;
import br.pucminas.matriculas.persistencia.RepositorioArquivo;
import br.pucminas.matriculas.servico.AutenticacaoServico;
import br.pucminas.matriculas.servico.CobrancaServico;
import br.pucminas.matriculas.servico.MatriculaServico;
import br.pucminas.matriculas.servico.ProfessorServico;
import br.pucminas.matriculas.servico.SecretariaServico;

import java.nio.file.Path;
import java.util.Scanner;

public class ConsoleApp {
    private final Path pastaDados;
    private final Terminal terminal;
    private final Repositorio repositorio;
    private final AutenticacaoServico autenticacaoServico;
    private final Menus menus;

    public ConsoleApp(Path pastaDados) {
        this.pastaDados = pastaDados;
        this.terminal = new Terminal(new Scanner(System.in));
        this.repositorio = new RepositorioArquivo(pastaDados);
        this.repositorio.carregar();
        DadosIniciais.garantir(repositorio);
        CobrancaServico cobrancaServico = new CobrancaServico(repositorio, pastaDados.resolve("cobrancas.log"));
        this.autenticacaoServico = new AutenticacaoServico(repositorio);
        this.menus = new Menus(terminal, repositorio,
                new SecretariaServico(repositorio, cobrancaServico),
                new MatriculaServico(repositorio, cobrancaServico),
                new ProfessorServico(repositorio));
    }

    public void executar() {
        terminal.titulo("Sistema de Matrículas — Universidade");
        System.out.println("Persistência em arquivos na pasta: " + pastaDados.toAbsolutePath());
        System.out.println("Usuários iniciais: secretaria/123 | prof/123 | aluno1/123 | aluno2/123 | aluno3/123");
        while (true) {
            terminal.titulo("Login");
            System.out.println("0) Encerrar sistema");
            String login = terminal.ler("Login: ");
            if ("0".equals(login)) {
                System.out.println("Até logo.");
                return;
            }
            String senha = terminal.ler("Senha: ");
            try {
                Usuario usuario = autenticacaoServico.autenticar(login, senha);
                System.out.println("Bem-vindo(a), " + usuario.getNome() + ".");
                switch (usuario.getPerfil()) {
                    case SECRETARIA -> menus.secretaria();
                    case ALUNO -> menus.aluno((Aluno) usuario);
                    case PROFESSOR -> menus.professor((Professor) usuario);
                }
            } catch (Exception e) {
                terminal.erro(e);
            }
        }
    }
}
