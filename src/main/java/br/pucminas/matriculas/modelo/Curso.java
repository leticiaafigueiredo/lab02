package br.pucminas.matriculas.modelo;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String id;
    private String nome;
    private int creditos;
    private final List<String> disciplinaIds = new ArrayList<>();

    public Curso() {
    }

    public Curso(String id, String nome, int creditos) {
        this.id = id;
        this.nome = nome;
        this.creditos = creditos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<String> getDisciplinaIds() {
        return disciplinaIds;
    }

    public void associarDisciplina(String disciplinaId) {
        if (!disciplinaIds.contains(disciplinaId)) {
            disciplinaIds.add(disciplinaId);
        }
    }

    @Override
    public String toString() {
        return id + " - " + nome + " (" + creditos + " créditos, " + disciplinaIds.size() + " disciplinas)";
    }
}
