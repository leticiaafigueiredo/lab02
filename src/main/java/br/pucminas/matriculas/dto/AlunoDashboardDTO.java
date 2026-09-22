package br.pucminas.matriculas.dto;

import java.util.List;

public record AlunoDashboardDTO(
    String alunoId,
    String alunoNome,
    String ra,
    String semestre,
    boolean periodoAberto,
    int totalObrigatorias,
    int maxObrigatorias,
    int totalOptativas,
    int maxOptativas,
    List<MatriculaResponseDTO> matriculas
) {}
