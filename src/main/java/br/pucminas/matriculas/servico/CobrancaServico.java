package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.persistencia.Repositorio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simula o sistema externo de cobranças gravando notificações em arquivo.
 */
public class CobrancaServico {
    private final Path log;
    private final Repositorio repositorio;

    public CobrancaServico(Repositorio repositorio, Path log) {
        this.repositorio = repositorio;
        this.log = log;
    }

    public void notificarInscricaoSemestre(Aluno aluno, String semestre) {
        String disciplinas = disciplinasDoAluno(aluno.getId(), semestre);
        registrar("INSCRICAO", aluno, semestre, disciplinas);
    }

    public void notificarAjusteFimPeriodo(Aluno aluno, String semestre) {
        String disciplinas = disciplinasDoAlunoAtivas(aluno.getId(), semestre);
        registrar("AJUSTE_FIM_PERIODO", aluno, semestre, disciplinas);
    }

    private String disciplinasDoAluno(String alunoId, String semestre) {
        return repositorio.listarMatriculasDoAluno(alunoId).stream()
                .filter(m -> ofertaDoSemestre(m, semestre) != null)
                .map(this::nomeDisciplina)
                .collect(Collectors.joining(", "));
    }

    private String disciplinasDoAlunoAtivas(String alunoId, String semestre) {
        return repositorio.listarMatriculasDoAluno(alunoId).stream()
                .map(m -> ofertaDoSemestre(m, semestre))
                .filter(o -> o != null && o.getStatus() == br.pucminas.matriculas.modelo.StatusOferta.ATIVA)
                .map(o -> repositorio.buscarDisciplina(o.getDisciplinaId()).map(Disciplina::toString).orElse(o.getDisciplinaId()))
                .collect(Collectors.joining(", "));
    }

    private OfertaDisciplina ofertaDoSemestre(Matricula m, String semestre) {
        return repositorio.buscarOferta(m.getOfertaId())
                .filter(o -> o.getSemestre().equals(semestre))
                .orElse(null);
    }

    private String nomeDisciplina(Matricula m) {
        return repositorio.buscarOferta(m.getOfertaId())
                .flatMap(o -> repositorio.buscarDisciplina(o.getDisciplinaId()))
                .map(Disciplina::toString)
                .orElse(m.getOfertaId());
    }

    private void registrar(String evento, Aluno aluno, String semestre, String disciplinas) {
        String linha = LocalDateTime.now() + " | " + evento + " | aluno=" + aluno.getLogin()
                + " | semestre=" + semestre + " | disciplinas=[" + disciplinas + "]";
        try {
            Files.createDirectories(log.getParent());
            Files.write(log, List.of(linha), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível notificar o sistema de cobranças.", e);
        }
    }
}
