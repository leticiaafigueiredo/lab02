package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.persistencia.Repositorio;

import java.util.List;

public class ProfessorServico {
    private final Repositorio repositorio;

    public ProfessorServico(Repositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Disciplina> disciplinasDoProfessor(Professor professor) {
        List<Disciplina> atribuidas = repositorio.listarDisciplinas().stream()
                .filter(d -> professor.getId().equals(d.getProfessorId()))
                .toList();
        if (!atribuidas.isEmpty()) {
            return atribuidas;
        }
        return repositorio.listarDisciplinas();
    }

    public List<Aluno> alunosMatriculados(String disciplinaId, String semestre) {
        OfertaDisciplina oferta = repositorio.buscarOferta(semestre, disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Não há oferta desta disciplina no semestre " + semestre + "."));
        return repositorio.listarMatriculasDaOferta(oferta.getId()).stream()
                .map(m -> (Aluno) repositorio.buscarUsuarioPorId(m.getAlunoId()).orElse(null))
                .filter(a -> a != null)
                .toList();
    }

    public OfertaDisciplina oferta(String disciplinaId, String semestre) {
        return repositorio.buscarOferta(semestre, disciplinaId).orElse(null);
    }
}
