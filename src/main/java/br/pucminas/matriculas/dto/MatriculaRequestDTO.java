package br.pucminas.matriculas.dto;

import br.pucminas.matriculas.modelo.TipoMatricula;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MatriculaRequestDTO(
    @NotBlank(message = "O ID da oferta é obrigatório.")
    String ofertaId,
    @NotNull(message = "O tipo de matrícula (OBRIGATORIA ou OPTATIVA) é obrigatório.")
    TipoMatricula tipo
) {}
