package br.pucminas.matriculas.modelo;

public class OfertaDisciplina {
    private String id;
    private String semestre;
    private String disciplinaId;
    private StatusOferta status = StatusOferta.ABERTA;

    public OfertaDisciplina() {
    }

    public OfertaDisciplina(String id, String semestre, String disciplinaId) {
        this.id = id;
        this.semestre = semestre;
        this.disciplinaId = disciplinaId;
    }

    public boolean aceitaMatricula() {
        return status == StatusOferta.ABERTA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(String disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public StatusOferta getStatus() {
        return status;
    }

    public void setStatus(StatusOferta status) {
        this.status = status;
    }
}
