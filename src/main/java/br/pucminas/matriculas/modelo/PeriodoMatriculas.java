package br.pucminas.matriculas.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "periodos_matriculas")
public class PeriodoMatriculas {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String semestre;

    @Column(nullable = false)
    private boolean aberto;

    public PeriodoMatriculas() {
    }

    public PeriodoMatriculas(String semestre, boolean aberto) {
        this.semestre = semestre;
        this.aberto = aberto;
    }

    public PeriodoMatriculas(String id, String semestre, boolean aberto) {
        this.id = id;
        this.semestre = semestre;
        this.aberto = aberto;
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

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }
}
