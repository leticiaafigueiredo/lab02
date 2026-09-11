package br.pucminas.matriculas.modelo;

public class PeriodoMatriculas {
    private String semestre;
    private boolean aberto;

    public PeriodoMatriculas() {
    }

    public PeriodoMatriculas(String semestre, boolean aberto) {
        this.semestre = semestre;
        this.aberto = aberto;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }
}
