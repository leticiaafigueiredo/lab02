package br.pucminas.matriculas.persistencia;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Curso;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.StatusOferta;
import br.pucminas.matriculas.modelo.Usuario;
import br.pucminas.matriculas.modelo.Perfil;

public final class DadosIniciais {
    private DadosIniciais() {
    }

    public static void garantir(Repositorio repositorio) {
        if (!repositorio.listarUsuarios().isEmpty()) {
            return;
        }
        Usuario secretaria = new Usuario(repositorio.proximoId("U"), "secretaria", "123",
                "Secretaria Acadêmica", Perfil.SECRETARIA);
        repositorio.adicionarUsuario(secretaria);

        Professor professor = new Professor(repositorio.proximoId("U"), "prof", "123", "Ana Lima");
        repositorio.adicionarUsuario(professor);

        repositorio.adicionarUsuario(new Aluno(repositorio.proximoId("U"), "aluno1", "123", "João Silva", "1001"));
        repositorio.adicionarUsuario(new Aluno(repositorio.proximoId("U"), "aluno2", "123", "Maria Souza", "1002"));
        repositorio.adicionarUsuario(new Aluno(repositorio.proximoId("U"), "aluno3", "123", "Pedro Alves", "1003"));

        Curso curso = new Curso(repositorio.proximoId("C"), "Engenharia de Software", 240);
        repositorio.adicionarCurso(curso);

        String[][] disciplinas = {
                {"PSC", "Projeto de Software"},
                {"REQ", "Requisitos de Software"},
                {"ARQ", "Arquitetura de Software"},
                {"TES", "Teste de Software"},
                {"BD", "Banco de Dados"},
                {"IHC", "Interface Humano-Computador"}
        };
        for (String[] info : disciplinas) {
            Disciplina d = new Disciplina(repositorio.proximoId("D"), info[0], info[1], curso.getId());
            d.setProfessorId(professor.getId());
            repositorio.adicionarDisciplina(d);
            curso.associarDisciplina(d.getId());
            OfertaDisciplina oferta = new OfertaDisciplina(repositorio.proximoId("O"), "2026.2", d.getId());
            oferta.setStatus(StatusOferta.ABERTA);
            repositorio.adicionarOferta(oferta);
        }

        repositorio.setPeriodo(new PeriodoMatriculas("2026.2", true));
        repositorio.salvar();
    }
}
