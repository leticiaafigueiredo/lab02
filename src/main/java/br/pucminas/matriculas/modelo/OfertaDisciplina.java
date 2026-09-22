package br.pucminas.matriculas.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ofertas_disciplinas")
public class OfertaDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String semestre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOferta status = StatusOferta.ABERTA;

    @JsonIgnore
    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Matricula> matriculas = new ArrayList<>();

    public OfertaDisciplina() {
    }

    public OfertaDisciplina(String semestre, Disciplina disciplina) {
        this.semestre = semestre;
        this.disciplina = disciplina;
        this.status = StatusOferta.ABERTA;
    }

    public OfertaDisciplina(String id, String semestre, Disciplina disciplina) {
        this.id = id;
        this.semestre = semestre;
        this.disciplina = disciplina;
        this.status = StatusOferta.ABERTA;
    }

    public boolean aceitaMatricula() {
        return status == StatusOferta.ABERTA && (matriculas == null || matriculas.size() < Disciplina.MAXIMO_ALUNOS);
    }

    public int getVagasOcupadas() {
        return matriculas != null ? matriculas.size() : 0;
    }

    public int getVagasRestantes() {
        return Math.max(0, Disciplina.MAXIMO_ALUNOS - getVagasOcupadas());
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

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public StatusOferta getStatus() {
        return status;
    }

    public void setStatus(StatusOferta status) {
        this.status = status;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }
}
