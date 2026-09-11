package br.pucminas.matriculas.modelo;

public class Matricula {
    private String id;
    private String alunoId;
    private String ofertaId;
    private TipoMatricula tipo;

    public Matricula() {
    }

    public Matricula(String id, String alunoId, String ofertaId, TipoMatricula tipo) {
        this.id = id;
        this.alunoId = alunoId;
        this.ofertaId = ofertaId;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(String alunoId) {
        this.alunoId = alunoId;
    }

    public String getOfertaId() {
        return ofertaId;
    }

    public void setOfertaId(String ofertaId) {
        this.ofertaId = ofertaId;
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public void setTipo(TipoMatricula tipo) {
        this.tipo = tipo;
    }
}
