package br.pucminas.matriculas.persistencia;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Curso;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.Usuario;

import java.util.List;
import java.util.Optional;

public interface Repositorio {
    void carregar();

    void salvar();

    String proximoId(String prefixo);

    List<Usuario> listarUsuarios();

    Optional<Usuario> buscarUsuarioPorLogin(String login);

    Optional<Usuario> buscarUsuarioPorId(String id);

    void adicionarUsuario(Usuario usuario);

    List<Aluno> listarAlunos();

    List<Professor> listarProfessores();

    List<Curso> listarCursos();

    Optional<Curso> buscarCurso(String id);

    void adicionarCurso(Curso curso);

    List<Disciplina> listarDisciplinas();

    Optional<Disciplina> buscarDisciplina(String id);

    void adicionarDisciplina(Disciplina disciplina);

    List<OfertaDisciplina> listarOfertas();

    List<OfertaDisciplina> listarOfertasDoSemestre(String semestre);

    Optional<OfertaDisciplina> buscarOferta(String id);

    Optional<OfertaDisciplina> buscarOferta(String semestre, String disciplinaId);

    void adicionarOferta(OfertaDisciplina oferta);

    List<Matricula> listarMatriculas();

    List<Matricula> listarMatriculasDoAluno(String alunoId);

    List<Matricula> listarMatriculasDaOferta(String ofertaId);

    void adicionarMatricula(Matricula matricula);

    void removerMatricula(String matriculaId);

    Optional<PeriodoMatriculas> getPeriodo();

    void setPeriodo(PeriodoMatriculas periodo);
}
