package br.pucminas.matriculas.modelo;

public class Disciplina {
    public static final int MINIMO_ALUNOS = 3;
    public static final int MAXIMO_ALUNOS = 60;

    private String id;
    private String codigo;
    private String nome;
    private String cursoId;
    private String professorId;

    public Disciplina() {
    }

    public Disciplina(String id, String codigo, String nome, String cursoId) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.cursoId = cursoId;
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

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
