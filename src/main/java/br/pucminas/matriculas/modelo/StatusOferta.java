package br.pucminas.matriculas.modelo;

public enum StatusOferta {
    /** Aceita matrículas no período aberto. */
    ABERTA,
    /** Atingiu 60 alunos; inscrições encerradas. */
    VAGAS_ENCERRADAS,
    /** ≥ 3 alunos ao fim do período: ocorre no semestre seguinte. */
    ATIVA,
    /** < 3 alunos ao fim do período: não ocorre. */
    CANCELADA
}
