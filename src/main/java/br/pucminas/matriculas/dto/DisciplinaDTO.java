package br.pucminas.matriculas.dto;

import jakarta.validation.constraints.NotBlank;

public class DisciplinaDTO {

    private String id;

    @NotBlank(message = "O código da disciplina é obrigatório")
    private String codigo;

    @NotBlank(message = "O nome da disciplina é obrigatório")
    private String nome;

    @NotBlank(message = "O ID do curso é obrigatório")
    private String cursoId;
    private String cursoNome;

    private String professorId;
    private String professorNome;

    public DisciplinaDTO() {
    }

    public DisciplinaDTO(String id, String codigo, String nome, String cursoId, String cursoNome, String professorId, String professorNome) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.cursoId = cursoId;
        this.cursoNome = cursoNome;
        this.professorId = professorId;
        this.professorNome = professorNome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCursoId() {
        return cursoId;
    }

    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getProfessorNome() {
        return professorNome;
    }

    public void setProfessorNome(String professorNome) {
        this.professorNome = professorNome;
    }
}
