package br.pucminas.matriculas.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfessorDTO {

    private String id;

    @NotBlank(message = "O login é obrigatório")
    private String login;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String departamento;
    private String titulacao;

    public ProfessorDTO() {
    }

    public ProfessorDTO(String id, String login, String nome, String departamento, String titulacao) {
        this.id = id;
        this.login = login;
        this.nome = nome;
        this.departamento = departamento;
        this.titulacao = titulacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
