package br.pucminas.matriculas.dto;

import jakarta.validation.constraints.NotBlank;

public class AlunoDTO {

    private String id;

    @NotBlank(message = "O login é obrigatório")
    private String login;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O RA é obrigatório")
    private String ra;

    public AlunoDTO() {
    }

    public AlunoDTO(String id, String login, String nome, String ra) {
        this.id = id;
        this.login = login;
        this.nome = nome;
        this.ra = ra;
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

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }
}
