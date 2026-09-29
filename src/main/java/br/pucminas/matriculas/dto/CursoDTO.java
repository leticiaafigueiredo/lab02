package br.pucminas.matriculas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CursoDTO {

    private String id;

    @NotBlank(message = "O nome do curso é obrigatório")
    private String nome;

    @Min(value = 1, message = "O curso deve ter pelo menos 1 crédito")
    private int creditos;

    public CursoDTO() {
    }

    public CursoDTO(String id, String nome, int creditos) {
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
}
