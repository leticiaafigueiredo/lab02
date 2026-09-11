package br.pucminas.matriculas.modelo;

public class Professor extends Usuario {
    public Professor() {
        setPerfil(Perfil.PROFESSOR);
    }

    public Professor(String id, String login, String senha, String nome) {
        super(id, login, senha, nome, Perfil.PROFESSOR);
    }
}
