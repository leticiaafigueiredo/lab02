package br.pucminas.matriculas.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matriculas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"aluno_id", "oferta_id"})
})
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id", nullable = false)
    @JsonIgnoreProperties({"matriculas", "senha"})
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "oferta_id", nullable = false)
    @JsonIgnoreProperties({"matriculas"})
    private OfertaDisciplina oferta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMatricula tipo;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Matricula() {
        this.dataHora = LocalDateTime.now();
    }

    public Matricula(Aluno aluno, OfertaDisciplina oferta, TipoMatricula tipo) {
        this.aluno = aluno;
        this.oferta = oferta;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    public Matricula(String id, Aluno aluno, OfertaDisciplina oferta, TipoMatricula tipo) {
        this.id = id;
        this.aluno = aluno;
        this.oferta = oferta;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public OfertaDisciplina getOferta() {
        return oferta;
    }

    public void setOferta(OfertaDisciplina oferta) {
        this.oferta = oferta;
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public void setTipo(TipoMatricula tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
