package br.pucminas.matriculas.dto;

import br.pucminas.matriculas.modelo.Perfil;

public record LoginResponseDTO(
    String token,
    String id,
    String nome,
    String login,
    Perfil perfil,
    String ra,
    String departamento
) {}
