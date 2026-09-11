package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.RegrasMatricula;
import br.pucminas.matriculas.modelo.StatusOferta;
import br.pucminas.matriculas.modelo.TipoMatricula;
import br.pucminas.matriculas.persistencia.Repositorio;

import java.util.List;

public class MatriculaServico {
    private final Repositorio repositorio;
    private final CobrancaServico cobrancaServico;

    public MatriculaServico(Repositorio repositorio, CobrancaServico cobrancaServico) {
        this.repositorio = repositorio;
        this.cobrancaServico = cobrancaServico;
    }

    public Matricula matricular(Aluno aluno, String ofertaId, TipoMatricula tipo) {
        PeriodoMatriculas periodo = exigirPeriodoAberto();
        OfertaDisciplina oferta = repositorio.buscarOferta(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
        if (!oferta.getSemestre().equals(periodo.getSemestre())) {
            throw new IllegalArgumentException("A oferta não pertence ao semestre do período vigente.");
        }
        if (!oferta.aceitaMatricula()) {
            throw new IllegalStateException("As inscrições desta disciplina estão encerradas (limite de "
                    + Disciplina.MAXIMO_ALUNOS + " alunos).");
        }
        boolean jaMatriculado = repositorio.listarMatriculasDoAluno(aluno.getId()).stream()
                .anyMatch(m -> m.getOfertaId().equals(ofertaId));
        if (jaMatriculado) {
            throw new IllegalStateException("Você já está matriculado nesta disciplina.");
        }
        List<Matricula> doSemestre = matriculasDoSemestre(aluno.getId(), periodo.getSemestre());
        long obrigatorias = doSemestre.stream().filter(m -> m.getTipo() == TipoMatricula.OBRIGATORIA).count();
        long optativas = doSemestre.stream().filter(m -> m.getTipo() == TipoMatricula.OPTATIVA).count();
        if (tipo == TipoMatricula.OBRIGATORIA && obrigatorias >= RegrasMatricula.MAX_OBRIGATORIAS) {
            throw new IllegalStateException("Limite de " + RegrasMatricula.MAX_OBRIGATORIAS
                    + " disciplinas obrigatórias (1ª opção) atingido.");
        }
        if (tipo == TipoMatricula.OPTATIVA && optativas >= RegrasMatricula.MAX_OPTATIVAS) {
            throw new IllegalStateException("Limite de " + RegrasMatricula.MAX_OPTATIVAS
                    + " disciplinas optativas (alternativas) atingido.");
        }
        Matricula matricula = new Matricula(repositorio.proximoId("M"), aluno.getId(), ofertaId, tipo);
        repositorio.adicionarMatricula(matricula);
        int inscritos = repositorio.listarMatriculasDaOferta(ofertaId).size();
        if (inscritos >= Disciplina.MAXIMO_ALUNOS) {
            oferta.setStatus(StatusOferta.VAGAS_ENCERRADAS);
        }
        repositorio.salvar();
        cobrancaServico.notificarInscricaoSemestre(aluno, periodo.getSemestre());
        return matricula;
    }

    public void cancelar(Aluno aluno, String matriculaId) {
        PeriodoMatriculas periodo = exigirPeriodoAberto();
        Matricula matricula = repositorio.listarMatriculasDoAluno(aluno.getId()).stream()
                .filter(m -> m.getId().equals(matriculaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
        OfertaDisciplina oferta = repositorio.buscarOferta(matricula.getOfertaId())
                .orElseThrow(() -> new IllegalStateException("Oferta da matrícula não encontrada."));
        if (!oferta.getSemestre().equals(periodo.getSemestre())) {
            throw new IllegalStateException("Só é possível cancelar matrículas do período vigente.");
        }
        repositorio.removerMatricula(matriculaId);
        if (oferta.getStatus() == StatusOferta.VAGAS_ENCERRADAS
                && repositorio.listarMatriculasDaOferta(oferta.getId()).size() < Disciplina.MAXIMO_ALUNOS) {
            oferta.setStatus(StatusOferta.ABERTA);
        }
        repositorio.salvar();
        cobrancaServico.notificarInscricaoSemestre(aluno, periodo.getSemestre());
    }

    public List<Matricula> listarDoAlunoNoSemestreCorrente(Aluno aluno) {
        String semestre = repositorio.getPeriodo().map(PeriodoMatriculas::getSemestre).orElse(null);
        if (semestre == null) {
            return List.of();
        }
        return matriculasDoSemestre(aluno.getId(), semestre);
    }

    private List<Matricula> matriculasDoSemestre(String alunoId, String semestre) {
        return repositorio.listarMatriculasDoAluno(alunoId).stream()
                .filter(m -> repositorio.buscarOferta(m.getOfertaId())
                        .filter(o -> o.getSemestre().equals(semestre))
                        .isPresent())
                .toList();
    }

    private PeriodoMatriculas exigirPeriodoAberto() {
        PeriodoMatriculas periodo = repositorio.getPeriodo()
                .orElseThrow(() -> new IllegalStateException("Não há período de matrículas cadastrado."));
        if (!periodo.isAberto()) {
            throw new IllegalStateException("O período de matrículas está fechado.");
        }
        return periodo;
    }
}
