package br.pucminas.matriculas.config;

import br.pucminas.matriculas.modelo.*;
import br.pucminas.matriculas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final PeriodoMatriculasRepository periodoRepository;

    public DatabaseSeeder(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            ProfessorRepository professorRepository,
            CursoRepository cursoRepository,
            DisciplinaRepository disciplinaRepository,
            OfertaDisciplinaRepository ofertaRepository,
            PeriodoMatriculasRepository periodoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.cursoRepository = cursoRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.ofertaRepository = ofertaRepository;
        this.periodoRepository = periodoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("Base de dados já inicializada.");
            return;
        }

        log.info("Iniciando Seed da base de dados do Sistema de Matrículas...");

        // 1. Criar Usuários
        Usuario secretaria = new Usuario("secretaria", "123", "Secretaria Acadêmica", Perfil.SECRETARIA);
        usuarioRepository.save(secretaria);

        Professor profAna = new Professor("prof", "123", "Profa. Dra. Ana Lima", "Engenharia de Software");
        profAna.setTitulacao("Doutora");
        professorRepository.save(profAna);

        Professor profCarlos = new Professor("prof2", "123", "Prof. Me. Carlos Eduardo", "Ciência da Computação");
        profCarlos.setTitulacao("Mestre");
        professorRepository.save(profCarlos);

        Aluno aluno1 = new Aluno("aluno1", "123", "João Silva", "1001");
        Aluno aluno2 = new Aluno("aluno2", "123", "Maria Souza", "1002");
        Aluno aluno3 = new Aluno("aluno3", "123", "Pedro Alves", "1003");
        Aluno aluno4 = new Aluno("aluno4", "123", "Beatriz Costa", "1004");
        alunoRepository.save(aluno1);
        alunoRepository.save(aluno2);
        alunoRepository.save(aluno3);
        alunoRepository.save(aluno4);

        // 2. Criar Curso
        Curso curso = new Curso("Engenharia de Software", 240);
        curso = cursoRepository.save(curso);

        // 3. Criar Disciplinas e Ofertas para 2026.2
        String[][] grade = {
                {"PSC", "Projeto de Software", "prof"},
                {"REQ", "Requisitos de Software", "prof"},
                {"ARQ", "Arquitetura de Software", "prof2"},
                {"TES", "Teste de Software", "prof2"},
                {"BD", "Banco de Dados", "prof"},
                {"IHC", "Interface Humano-Computador", "prof2"}
        };

        for (String[] item : grade) {
            String cod = item[0];
            String nome = item[1];
            String profLogin = item[2];

            Professor prof = profLogin.equals("prof") ? profAna : profCarlos;
            Disciplina d = new Disciplina(cod, nome, curso, prof);
            d = disciplinaRepository.save(d);

            OfertaDisciplina oferta = new OfertaDisciplina("2026.2", d);
            oferta.setStatus(StatusOferta.ABERTA);
            ofertaRepository.save(oferta);
        }

        // 4. Criar Período Aberto 2026.2
        PeriodoMatriculas periodo = new PeriodoMatriculas("2026.2", true);
        periodoRepository.save(periodo);

        log.info("Seed da base de dados concluído com sucesso!");
        log.info("Período ativo: 2026.2 (ABERTO)");
        log.info("Usuários criados:");
        log.info(" - Secretaria: secretaria / 123");
        log.info(" - Professor: prof / 123 | prof2 / 123");
        log.info(" - Alunos: aluno1, aluno2, aluno3, aluno4 / 123");
    }
}
