package br.pucminas.matriculas.modelo;

public class Usuario {
    private String id;
    private String login;
    private String senha;
    private String nome;
    private Perfil perfil;

    public Usuario() {
    }

    public Usuario(String id, String login, String senha, String nome, Perfil perfil) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.perfil = perfil;
    }

    public boolean autenticar(String senhaInformada) {
        return senha != null && senha.equals(senhaInformada);
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

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return nome + " (" + login + ") [" + perfil + "]";
    }
}
