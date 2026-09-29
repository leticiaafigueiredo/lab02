package br.pucminas.matriculas.dto;

import jakarta.validation.constraints.NotBlank;

public class GerarOfertaDTO {

    @NotBlank(message = "O semestre é obrigatório")
    private String semestre;

    @NotBlank(message = "O ID da disciplina é obrigatório")
    private String disciplinaId;

    public GerarOfertaDTO() {
    }

    public GerarOfertaDTO(String semestre, String disciplinaId) {
        this.semestre = semestre;
        this.disciplinaId = disciplinaId;
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
}
