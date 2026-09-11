package br.pucminas.matriculas.ui;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Curso;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.TipoMatricula;
import br.pucminas.matriculas.modelo.Usuario;
import br.pucminas.matriculas.persistencia.Repositorio;
import br.pucminas.matriculas.servico.MatriculaServico;
import br.pucminas.matriculas.servico.ProfessorServico;
import br.pucminas.matriculas.servico.SecretariaServico;

import java.util.ArrayList;
import java.util.List;

public class Menus {
    private final Terminal terminal;
    private final Repositorio repositorio;
    private final SecretariaServico secretariaServico;
    private final MatriculaServico matriculaServico;
    private final ProfessorServico professorServico;

    public Menus(Terminal terminal, Repositorio repositorio, SecretariaServico secretariaServico,
                 MatriculaServico matriculaServico, ProfessorServico professorServico) {
        this.terminal = terminal;
        this.repositorio = repositorio;
        this.secretariaServico = secretariaServico;
        this.matriculaServico = matriculaServico;
        this.professorServico = professorServico;
    }

    public void secretaria() {
        while (true) {
            terminal.titulo("Menu Secretaria");
            System.out.println("1) Manter cursos");
            System.out.println("2) Manter disciplinas");
            System.out.println("3) Manter professores");
            System.out.println("4) Manter alunos");
            System.out.println("5) Gerar currículo do semestre");
            System.out.println("6) Abrir período de matrículas");
            System.out.println("7) Encerrar período e definir ofertas (mín. 3 alunos)");
            System.out.println("8) Ver situação das ofertas");
            System.out.println("0) Sair");
            int op = terminal.lerInt("Opção: ");
            try {
                switch (op) {
                    case 1 -> manterCursos();
                    case 2 -> manterDisciplinas();
                    case 3 -> manterProfessores();
                    case 4 -> manterAlunos();
                    case 5 -> gerarCurriculo();
                    case 6 -> abrirPeriodo();
                    case 7 -> secretariaServico.encerrarPeriodoEDefinirOfertas();
                    case 8 -> listarOfertas();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
                if (op == 7) {
                    System.out.println("Período encerrado. Disciplinas com 3 ou mais alunos ficaram ATIVAS; as demais, CANCELADAS.");
                    System.out.println("Sistema de cobranças notificado (ajuste). Veja data/cobrancas.log.");
                }
            } catch (Exception e) {
                terminal.erro(e);
            }
        }
    }

    public void aluno(Aluno aluno) {
        while (true) {
            terminal.titulo("Menu Aluno — " + aluno.getNome());
            imprimirPeriodo();
            System.out.println("1) Matricular-se em disciplina");
            System.out.println("2) Cancelar matrícula");
            System.out.println("3) Consultar minhas matrículas");
            System.out.println("4) Ver ofertas do semestre");
            System.out.println("0) Sair");
            int op = terminal.lerInt("Opção: ");
            try {
                switch (op) {
                    case 1 -> matricular(aluno);
                    case 2 -> cancelar(aluno);
                    case 3 -> minhasMatriculas(aluno);
                    case 4 -> listarOfertas();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                terminal.erro(e);
            }
        }
    }

    public void professor(Professor professor) {
        while (true) {
            terminal.titulo("Menu Professor — " + professor.getNome());
            System.out.println("1) Consultar alunos matriculados por disciplina");
            System.out.println("0) Sair");
            int op = terminal.lerInt("Opção: ");
            try {
                switch (op) {
                    case 1 -> consultarTurma(professor);
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                terminal.erro(e);
            }
        }
    }

    private void manterCursos() {
        terminal.titulo("Cursos");
        repositorio.listarCursos().forEach(c -> System.out.println("  " + c));
        System.out.println("1) Cadastrar  2) Atualizar  0) Voltar");
        int op = terminal.lerInt("Opção: ");
        if (op == 1) {
            String nome = terminal.ler("Nome: ");
            int creditos = terminal.lerInt("Créditos: ");
            Curso curso = secretariaServico.cadastrarCurso(nome, creditos);
            System.out.println("Curso cadastrado: " + curso);
        } else if (op == 2) {
            Curso curso = terminal.escolher("Curso: ", repositorio.listarCursos(), Curso::toString);
            String nome = terminal.ler("Novo nome: ");
            int creditos = terminal.lerInt("Novos créditos: ");
            secretariaServico.atualizarCurso(curso.getId(), nome, creditos);
            System.out.println("Curso atualizado.");
        }
    }

    private void manterDisciplinas() {
        terminal.titulo("Disciplinas");
        for (Disciplina d : repositorio.listarDisciplinas()) {
            String prof = d.getProfessorId() == null ? "-"
                    : repositorio.buscarUsuarioPorId(d.getProfessorId()).map(Usuario::getNome).orElse("?");
            System.out.println("  " + d.getId() + " | " + d + " | curso=" + d.getCursoId() + " | prof=" + prof);
        }
        System.out.println("1) Cadastrar  2) Atualizar  3) Atribuir professor  0) Voltar");
        int op = terminal.lerInt("Opção: ");
        if (op == 1) {
            Curso curso = terminal.escolher("Curso: ", repositorio.listarCursos(), Curso::toString);
            String codigo = terminal.ler("Código: ");
            String nome = terminal.ler("Nome: ");
            Disciplina d = secretariaServico.cadastrarDisciplina(codigo, nome, curso.getId());
            System.out.println("Disciplina cadastrada: " + d);
        } else if (op == 2) {
            Disciplina d = terminal.escolher("Disciplina: ", repositorio.listarDisciplinas(), Disciplina::toString);
            String codigo = terminal.ler("Novo código: ");
            String nome = terminal.ler("Novo nome: ");
            secretariaServico.atualizarDisciplina(d.getId(), codigo, nome);
            System.out.println("Disciplina atualizada.");
        } else if (op == 3) {
            Disciplina d = terminal.escolher("Disciplina: ", repositorio.listarDisciplinas(), Disciplina::toString);
            Professor p = terminal.escolher("Professor: ", repositorio.listarProfessores(), Usuario::toString);
            secretariaServico.atribuirProfessor(d.getId(), p.getId());
            System.out.println("Professor atribuído.");
        }
    }

    private void manterProfessores() {
        terminal.titulo("Professores");
        repositorio.listarProfessores().forEach(p -> System.out.println("  " + p));
        System.out.println("1) Cadastrar  2) Atualizar  0) Voltar");
        int op = terminal.lerInt("Opção: ");
        if (op == 1) {
            secretariaServico.cadastrarProfessor(
                    terminal.ler("Login: "),
                    terminal.ler("Senha: "),
                    terminal.ler("Nome: "));
            System.out.println("Professor cadastrado.");
        } else if (op == 2) {
            Professor p = terminal.escolher("Professor: ", repositorio.listarProfessores(), Usuario::toString);
            secretariaServico.atualizarProfessor(p.getId(), terminal.ler("Nova senha (vazio = manter): "),
                    terminal.ler("Novo nome: "));
            System.out.println("Professor atualizado.");
        }
    }

    private void manterAlunos() {
        terminal.titulo("Alunos");
        repositorio.listarAlunos().forEach(a -> System.out.println("  " + a + " RA=" + a.getRa()));
        System.out.println("1) Cadastrar  2) Atualizar  0) Voltar");
        int op = terminal.lerInt("Opção: ");
        if (op == 1) {
            secretariaServico.cadastrarAluno(
                    terminal.ler("Login: "),
                    terminal.ler("Senha: "),
                    terminal.ler("Nome: "),
                    terminal.ler("RA: "));
            System.out.println("Aluno cadastrado.");
        } else if (op == 2) {
            Aluno a = terminal.escolher("Aluno: ", repositorio.listarAlunos(), u -> u + " RA=" + u.getRa());
            secretariaServico.atualizarAluno(a.getId(),
                    terminal.ler("Nova senha (vazio = manter): "),
                    terminal.ler("Novo nome: "),
                    terminal.ler("Novo RA: "));
            System.out.println("Aluno atualizado.");
        }
    }

    private void gerarCurriculo() {
        String semestre = terminal.ler("Semestre (ex.: 2026.2): ");
        List<Disciplina> todas = repositorio.listarDisciplinas();
        if (todas.isEmpty()) {
            throw new IllegalStateException("Cadastre disciplinas antes de gerar o currículo.");
        }
        System.out.println("Informe os números das disciplinas separados por vírgula (ex.: 1,2,3).");
        for (int i = 0; i < todas.size(); i++) {
            System.out.println((i + 1) + ") " + todas.get(i));
        }
        String escolha = terminal.ler("Disciplinas: ");
        List<String> ids = new ArrayList<>();
        for (String parte : escolha.split(",")) {
            int idx = Integer.parseInt(parte.trim()) - 1;
            ids.add(todas.get(idx).getId());
        }
        secretariaServico.gerarCurriculoSemestre(semestre, ids);
        System.out.println("Currículo do semestre " + semestre + " gerado. Abra o período para os alunos se matricularem.");
    }

    private void abrirPeriodo() {
        String semestre = repositorio.getPeriodo()
                .map(PeriodoMatriculas::getSemestre)
                .orElseGet(() -> terminal.ler("Semestre: "));
        secretariaServico.abrirPeriodoMatriculas(semestre);
        System.out.println("Período de matrículas ABERTO para " + semestre + ".");
    }

    private void listarOfertas() {
        PeriodoMatriculas periodo = repositorio.getPeriodo().orElse(null);
        if (periodo == null) {
            System.out.println("Nenhum semestre/período cadastrado.");
            return;
        }
        imprimirPeriodo();
        for (OfertaDisciplina o : repositorio.listarOfertasDoSemestre(periodo.getSemestre())) {
            Disciplina d = repositorio.buscarDisciplina(o.getDisciplinaId()).orElse(null);
            int qtd = repositorio.listarMatriculasDaOferta(o.getId()).size();
            System.out.println("  " + o.getId() + " | " + (d == null ? o.getDisciplinaId() : d)
                    + " | " + o.getStatus() + " | inscritos=" + qtd + "/" + Disciplina.MAXIMO_ALUNOS);
        }
    }

    private void matricular(Aluno aluno) {
        PeriodoMatriculas periodo = repositorio.getPeriodo()
                .orElseThrow(() -> new IllegalStateException("Não há período cadastrado."));
        List<OfertaDisciplina> ofertas = repositorio.listarOfertasDoSemestre(periodo.getSemestre());
        OfertaDisciplina oferta = terminal.escolher("Oferta: ", ofertas, this::rotuloOferta);
        System.out.println("1) Obrigatória (1ª opção, máx. 4)  2) Optativa (alternativa, máx. 2)");
        int tipoOp = terminal.lerInt("Tipo: ");
        TipoMatricula tipo = tipoOp == 2 ? TipoMatricula.OPTATIVA : TipoMatricula.OBRIGATORIA;
        matriculaServico.matricular(aluno, oferta.getId(), tipo);
        System.out.println("Matrícula confirmada. Sistema de cobranças notificado (data/cobrancas.log).");
    }

    private void cancelar(Aluno aluno) {
        List<Matricula> lista = matriculaServico.listarDoAlunoNoSemestreCorrente(aluno);
        Matricula m = terminal.escolher("Matrícula a cancelar: ", lista, this::rotuloMatricula);
        matriculaServico.cancelar(aluno, m.getId());
        System.out.println("Matrícula cancelada.");
    }

    private void minhasMatriculas(Aluno aluno) {
        List<Matricula> lista = matriculaServico.listarDoAlunoNoSemestreCorrente(aluno);
        if (lista.isEmpty()) {
            System.out.println("Você não possui matrículas neste semestre.");
            return;
        }
        lista.forEach(m -> System.out.println("  " + rotuloMatricula(m)));
    }

    private void consultarTurma(Professor professor) {
        String semestre = repositorio.getPeriodo()
                .map(PeriodoMatriculas::getSemestre)
                .orElseGet(() -> terminal.ler("Semestre: "));
        Disciplina disciplina = terminal.escolher("Disciplina: ",
                professorServico.disciplinasDoProfessor(professor), Disciplina::toString);
        OfertaDisciplina oferta = professorServico.oferta(disciplina.getId(), semestre);
        if (oferta == null) {
            System.out.println("Não há oferta desta disciplina no semestre " + semestre + ".");
            return;
        }
        System.out.println("Status da oferta: " + oferta.getStatus()
                + (oferta.getStatus() == br.pucminas.matriculas.modelo.StatusOferta.CANCELADA
                ? " (a turma não ocorrerá)" : ""));
        List<Aluno> alunos = professorServico.alunosMatriculados(disciplina.getId(), semestre);
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno matriculado.");
            return;
        }
        alunos.forEach(a -> System.out.println("  " + a.getNome() + " | login=" + a.getLogin() + " | RA=" + a.getRa()));
    }

    private void imprimirPeriodo() {
        repositorio.getPeriodo().ifPresentOrElse(
                p -> System.out.println("Semestre " + p.getSemestre() + " | período de matrículas: "
                        + (p.isAberto() ? "ABERTO" : "FECHADO")),
                () -> System.out.println("Nenhum período de matrículas definido."));
    }

    private String rotuloOferta(OfertaDisciplina o) {
        String nome = repositorio.buscarDisciplina(o.getDisciplinaId()).map(Disciplina::toString).orElse(o.getDisciplinaId());
        int qtd = repositorio.listarMatriculasDaOferta(o.getId()).size();
        return o.getId() + " | " + nome + " | " + o.getStatus() + " | " + qtd + " inscritos";
    }

    private String rotuloMatricula(Matricula m) {
        OfertaDisciplina o = repositorio.buscarOferta(m.getOfertaId()).orElse(null);
        String nome = o == null ? m.getOfertaId()
                : repositorio.buscarDisciplina(o.getDisciplinaId()).map(Disciplina::toString).orElse(o.getDisciplinaId());
        String status = o == null ? "?" : o.getStatus().name();
        return m.getId() + " | " + nome + " | " + m.getTipo() + " | status=" + status;
    }
}
