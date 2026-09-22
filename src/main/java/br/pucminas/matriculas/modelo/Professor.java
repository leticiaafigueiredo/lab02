package br.pucminas.matriculas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "professores")
public class Professor extends Usuario {

    @Column
    private String departamento;

    @Column
    private String titulacao;

    public Professor() {
        super();
        setPerfil(Perfil.PROFESSOR);
    }

    public Professor(String login, String senha, String nome, String departamento) {
        super(login, senha, nome, Perfil.PROFESSOR);
        this.departamento = departamento;
    }

    public Professor(String id, String login, String senha, String nome, String departamento) {
        super(id, login, senha, nome, Perfil.PROFESSOR);
        this.departamento = departamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTitulacao() {
        return titulacao;
    }

    public void setTitulacao(String titulacao) {
        this.titulacao = titulacao;
    }
}
