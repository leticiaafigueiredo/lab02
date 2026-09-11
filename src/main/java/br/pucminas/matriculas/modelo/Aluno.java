package br.pucminas.matriculas.modelo;

public class Aluno extends Usuario {
    private String ra;

    public Aluno() {
        setPerfil(Perfil.ALUNO);
    }

    public Aluno(String id, String login, String senha, String nome, String ra) {
        super(id, login, senha, nome, Perfil.ALUNO);
        this.ra = ra;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }
}
