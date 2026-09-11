package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Curso;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.StatusOferta;
import br.pucminas.matriculas.persistencia.Repositorio;

public class SecretariaServico {
    private final Repositorio repositorio;
    private final CobrancaServico cobrancaServico;

    public SecretariaServico(Repositorio repositorio, CobrancaServico cobrancaServico) {
        this.repositorio = repositorio;
        this.cobrancaServico = cobrancaServico;
    }

    public Curso cadastrarCurso(String nome, int creditos) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O curso precisa de um nome.");
        }
        if (creditos <= 0) {
            throw new IllegalArgumentException("O curso precisa de um número de créditos maior que zero.");
        }
        Curso curso = new Curso(repositorio.proximoId("C"), nome.trim(), creditos);
        repositorio.adicionarCurso(curso);
        repositorio.salvar();
        return curso;
    }

    public void atualizarCurso(String id, String nome, int creditos) {
        Curso curso = repositorio.buscarCurso(id).orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));
        if (nome == null || nome.isBlank() || creditos <= 0) {
            throw new IllegalArgumentException("Nome e créditos são obrigatórios.");
        }
        curso.setNome(nome.trim());
        curso.setCreditos(creditos);
        repositorio.salvar();
    }

    public Disciplina cadastrarDisciplina(String codigo, String nome, String cursoId) {
        if (codigo == null || codigo.isBlank() || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Código e nome da disciplina são obrigatórios.");
        }
        Curso curso = repositorio.buscarCurso(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));
        Disciplina disciplina = new Disciplina(repositorio.proximoId("D"), codigo.trim().toUpperCase(), nome.trim(), cursoId);
        repositorio.adicionarDisciplina(disciplina);
        curso.associarDisciplina(disciplina.getId());
        repositorio.salvar();
        return disciplina;
    }

    public void atualizarDisciplina(String id, String codigo, String nome) {
        Disciplina disciplina = repositorio.buscarDisciplina(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));
        if (codigo == null || codigo.isBlank() || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Código e nome são obrigatórios.");
        }
        disciplina.setCodigo(codigo.trim().toUpperCase());
        disciplina.setNome(nome.trim());
        repositorio.salvar();
    }

    public void atribuirProfessor(String disciplinaId, String professorId) {
        Disciplina disciplina = repositorio.buscarDisciplina(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));
        repositorio.buscarUsuarioPorId(professorId)
                .filter(Professor.class::isInstance)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        disciplina.setProfessorId(professorId);
        repositorio.salvar();
    }

    public Professor cadastrarProfessor(String login, String senha, String nome) {
        validarCredenciais(login, senha, nome);
        garantirLoginLivre(login);
        Professor professor = new Professor(repositorio.proximoId("U"), login.trim(), senha, nome.trim());
        repositorio.adicionarUsuario(professor);
        repositorio.salvar();
        return professor;
    }

    public void atualizarProfessor(String id, String senha, String nome) {
        Professor professor = repositorio.listarProfessores().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        professor.setNome(nome.trim());
        if (senha != null && !senha.isBlank()) {
            professor.setSenha(senha);
        }
        repositorio.salvar();
    }

    public Aluno cadastrarAluno(String login, String senha, String nome, String ra) {
        validarCredenciais(login, senha, nome);
        if (ra == null || ra.isBlank()) {
            throw new IllegalArgumentException("RA do aluno é obrigatório.");
        }
        garantirLoginLivre(login);
        Aluno aluno = new Aluno(repositorio.proximoId("U"), login.trim(), senha, nome.trim(), ra.trim());
        repositorio.adicionarUsuario(aluno);
        repositorio.salvar();
        return aluno;
    }

    public void atualizarAluno(String id, String senha, String nome, String ra) {
        Aluno aluno = repositorio.listarAlunos().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        if (nome == null || nome.isBlank() || ra == null || ra.isBlank()) {
            throw new IllegalArgumentException("Nome e RA são obrigatórios.");
        }
        aluno.setNome(nome.trim());
        aluno.setRa(ra.trim());
        if (senha != null && !senha.isBlank()) {
            aluno.setSenha(senha);
        }
        repositorio.salvar();
    }

    public void gerarCurriculoSemestre(String semestre, java.util.List<String> disciplinaIds) {
        if (semestre == null || semestre.isBlank()) {
            throw new IllegalArgumentException("Informe o semestre (ex.: 2026.2).");
        }
        if (disciplinaIds == null || disciplinaIds.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma disciplina para o currículo.");
        }
        for (String disciplinaId : disciplinaIds) {
            repositorio.buscarDisciplina(disciplinaId)
                    .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada: " + disciplinaId));
            if (repositorio.buscarOferta(semestre, disciplinaId).isEmpty()) {
                OfertaDisciplina oferta = new OfertaDisciplina(repositorio.proximoId("O"), semestre, disciplinaId);
                oferta.setStatus(StatusOferta.ABERTA);
                repositorio.adicionarOferta(oferta);
            }
        }
        PeriodoMatriculas periodo = repositorio.getPeriodo().orElse(new PeriodoMatriculas(semestre, false));
        periodo.setSemestre(semestre);
        repositorio.setPeriodo(periodo);
        repositorio.salvar();
    }

    public void abrirPeriodoMatriculas(String semestre) {
        if (repositorio.listarOfertasDoSemestre(semestre).isEmpty()) {
            throw new IllegalStateException("Gere o currículo do semestre antes de abrir o período de matrículas.");
        }
        repositorio.setPeriodo(new PeriodoMatriculas(semestre, true));
        for (OfertaDisciplina oferta : repositorio.listarOfertasDoSemestre(semestre)) {
            if (oferta.getStatus() == StatusOferta.ATIVA || oferta.getStatus() == StatusOferta.CANCELADA) {
                continue;
            }
            int qtd = repositorio.listarMatriculasDaOferta(oferta.getId()).size();
            oferta.setStatus(qtd >= Disciplina.MAXIMO_ALUNOS ? StatusOferta.VAGAS_ENCERRADAS : StatusOferta.ABERTA);
        }
        repositorio.salvar();
    }

    public void encerrarPeriodoEDefinirOfertas() {
        PeriodoMatriculas periodo = repositorio.getPeriodo()
                .orElseThrow(() -> new IllegalStateException("Não há período de matrículas cadastrado."));
        if (!periodo.isAberto()) {
            throw new IllegalStateException("O período de matrículas já está encerrado.");
        }
        String semestre = periodo.getSemestre();
        for (OfertaDisciplina oferta : repositorio.listarOfertasDoSemestre(semestre)) {
            int inscritos = repositorio.listarMatriculasDaOferta(oferta.getId()).size();
            oferta.setStatus(inscritos >= Disciplina.MINIMO_ALUNOS ? StatusOferta.ATIVA : StatusOferta.CANCELADA);
        }
        periodo.setAberto(false);
        repositorio.salvar();
        for (Aluno aluno : repositorio.listarAlunos()) {
            boolean inscrito = repositorio.listarMatriculasDoAluno(aluno.getId()).stream()
                    .anyMatch(m -> repositorio.buscarOferta(m.getOfertaId())
                            .filter(o -> o.getSemestre().equals(semestre))
                            .isPresent());
            if (inscrito) {
                cobrancaServico.notificarAjusteFimPeriodo(aluno, semestre);
            }
        }
    }

    private void validarCredenciais(String login, String senha, String nome) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank() || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Login, senha e nome são obrigatórios.");
        }
    }

    private void garantirLoginLivre(String login) {
        repositorio.buscarUsuarioPorLogin(login).ifPresent(u -> {
            throw new IllegalArgumentException("Já existe um usuário com este login.");
        });
    }
}
