package br.pucminas.matriculas.dto;

import br.pucminas.matriculas.modelo.StatusOferta;
import br.pucminas.matriculas.modelo.TipoMatricula;

public record OfertaDisponivelDTO(
    String id,
    String semestre,
    String codigoDisciplina,
    String nomeDisciplina,
    String cursoNome,
    String professorNome,
    StatusOferta status,
    int vagasOcupadas,
    int vagasRestantes,
    int maxVagas,
    boolean jaMatriculado,
    String matriculaId,
    TipoMatricula tipoMatricula
) {}
