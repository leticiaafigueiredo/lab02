package br.pucminas.matriculas.dto;

import br.pucminas.matriculas.modelo.StatusOferta;
import java.util.List;

public record TurmaProfessorDTO(
    String ofertaId,
    String codigoDisciplina,
    String nomeDisciplina,
    String cursoNome,
    String semestre,
    StatusOferta status,
    int totalInscritos,
    List<AlunoInscritoDTO> alunos
) {
    public record AlunoInscritoDTO(
        String matriculaId,
        String alunoId,
        String nome,
        String login,
        String ra,
        String tipo,
        String dataHora
    ) {}
}
