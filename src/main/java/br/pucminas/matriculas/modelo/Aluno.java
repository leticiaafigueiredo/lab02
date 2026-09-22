package br.pucminas.matriculas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "alunos")
public class Aluno extends Usuario {

    @Column(unique = true)
    private String ra;

    public Aluno() {
        super();
        setPerfil(Perfil.ALUNO);
    }

    public Aluno(String login, String senha, String nome, String ra) {
        super(login, senha, nome, Perfil.ALUNO);
        this.ra = ra;
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
