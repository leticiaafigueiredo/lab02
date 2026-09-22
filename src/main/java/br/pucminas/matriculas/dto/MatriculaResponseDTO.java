package br.pucminas.matriculas.dto;

import br.pucminas.matriculas.modelo.TipoMatricula;
import java.time.LocalDateTime;

public record MatriculaResponseDTO(
    String id,
    String ofertaId,
    String codigoDisciplina,
    String nomeDisciplina,
    String professorNome,
    String cursoNome,
    String semestre,
    TipoMatricula tipo,
    LocalDateTime dataHora
) {}
