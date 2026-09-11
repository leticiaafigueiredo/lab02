package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Usuario;
import br.pucminas.matriculas.persistencia.Repositorio;

public class AutenticacaoServico {
    private final Repositorio repositorio;

    public AutenticacaoServico(Repositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Usuario autenticar(String login, String senha) {
        Usuario usuario = repositorio.buscarUsuarioPorLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Login não encontrado."));
        if (!usuario.autenticar(senha)) {
            throw new IllegalArgumentException("Senha inválida.");
        }
        return usuario;
    }
}
