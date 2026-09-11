package br.pucminas.matriculas.persistencia;

import br.pucminas.matriculas.modelo.Aluno;
import br.pucminas.matriculas.modelo.Curso;
import br.pucminas.matriculas.modelo.Disciplina;
import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.OfertaDisciplina;
import br.pucminas.matriculas.modelo.Perfil;
import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import br.pucminas.matriculas.modelo.Professor;
import br.pucminas.matriculas.modelo.StatusOferta;
import br.pucminas.matriculas.modelo.TipoMatricula;
import br.pucminas.matriculas.modelo.Usuario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RepositorioArquivo implements Repositorio {
    private final Path pasta;
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<OfertaDisciplina> ofertas = new ArrayList<>();
    private final List<Matricula> matriculas = new ArrayList<>();
    private PeriodoMatriculas periodo;
    private final AtomicInteger sequencia = new AtomicInteger(1);

    public RepositorioArquivo(Path pasta) {
        this.pasta = pasta;
    }

    @Override
    public void carregar() {
        try {
            Files.createDirectories(pasta);
            usuarios.clear();
            cursos.clear();
            disciplinas.clear();
            ofertas.clear();
            matriculas.clear();
            periodo = null;
            lerUsuarios();
            lerCursos();
            lerDisciplinas();
            lerOfertas();
            lerMatriculas();
            lerPeriodo();
            atualizarSequencia();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar dados: " + e.getMessage(), e);
        }
    }

    @Override
    public void salvar() {
        try {
            Files.createDirectories(pasta);
            escrever(arquivo("usuarios.csv"), usuarios.stream().map(this::serializarUsuario).toList());
            escrever(arquivo("cursos.csv"), cursos.stream().map(this::serializarCurso).toList());
            escrever(arquivo("disciplinas.csv"), disciplinas.stream().map(this::serializarDisciplina).toList());
            escrever(arquivo("ofertas.csv"), ofertas.stream().map(this::serializarOferta).toList());
            escrever(arquivo("matriculas.csv"), matriculas.stream().map(this::serializarMatricula).toList());
            if (periodo == null) {
                Files.deleteIfExists(arquivo("periodo.csv"));
            } else {
                escrever(arquivo("periodo.csv"), List.of(Csv.join(periodo.getSemestre(), periodo.isAberto())));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar dados: " + e.getMessage(), e);
        }
    }

    @Override
    public String proximoId(String prefixo) {
        return prefixo + sequencia.getAndIncrement();
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorLogin(String login) {
        return usuarios.stream().filter(u -> u.getLogin().equalsIgnoreCase(login)).findFirst();
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorId(String id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public void adicionarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public List<Aluno> listarAlunos() {
        return usuarios.stream().filter(Aluno.class::isInstance).map(Aluno.class::cast).toList();
    }

    @Override
    public List<Professor> listarProfessores() {
        return usuarios.stream().filter(Professor.class::isInstance).map(Professor.class::cast).toList();
    }

    @Override
    public List<Curso> listarCursos() {
        return cursos;
    }

    @Override
    public Optional<Curso> buscarCurso(String id) {
        return cursos.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public void adicionarCurso(Curso curso) {
        cursos.add(curso);
    }

    @Override
    public List<Disciplina> listarDisciplinas() {
        return disciplinas;
    }

    @Override
    public Optional<Disciplina> buscarDisciplina(String id) {
        return disciplinas.stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    @Override
    public void adicionarDisciplina(Disciplina disciplina) {
        disciplinas.add(disciplina);
    }

    @Override
    public List<OfertaDisciplina> listarOfertas() {
        return ofertas;
    }

    @Override
    public List<OfertaDisciplina> listarOfertasDoSemestre(String semestre) {
        return ofertas.stream().filter(o -> o.getSemestre().equals(semestre)).toList();
    }

    @Override
    public Optional<OfertaDisciplina> buscarOferta(String id) {
        return ofertas.stream().filter(o -> o.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<OfertaDisciplina> buscarOferta(String semestre, String disciplinaId) {
        return ofertas.stream()
                .filter(o -> o.getSemestre().equals(semestre) && o.getDisciplinaId().equals(disciplinaId))
                .findFirst();
    }

    @Override
    public void adicionarOferta(OfertaDisciplina oferta) {
        ofertas.add(oferta);
    }

    @Override
    public List<Matricula> listarMatriculas() {
        return matriculas;
    }

    @Override
    public List<Matricula> listarMatriculasDoAluno(String alunoId) {
        return matriculas.stream().filter(m -> m.getAlunoId().equals(alunoId)).toList();
    }

    @Override
    public List<Matricula> listarMatriculasDaOferta(String ofertaId) {
        return matriculas.stream().filter(m -> m.getOfertaId().equals(ofertaId)).toList();
    }

    @Override
    public void adicionarMatricula(Matricula matricula) {
        matriculas.add(matricula);
    }

    @Override
    public void removerMatricula(String matriculaId) {
        matriculas.removeIf(m -> m.getId().equals(matriculaId));
    }

    @Override
    public Optional<PeriodoMatriculas> getPeriodo() {
        return Optional.ofNullable(periodo);
    }

    @Override
    public void setPeriodo(PeriodoMatriculas periodo) {
        this.periodo = periodo;
    }

    private Path arquivo(String nome) {
        return pasta.resolve(nome);
    }

    private void escrever(Path path, List<String> linhas) throws IOException {
        Files.write(path, linhas, StandardCharsets.UTF_8);
    }

    private List<String> lerLinhas(Path path) throws IOException {
        if (!Files.exists(path)) {
            return List.of();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                .filter(l -> !l.isBlank())
                .toList();
    }

    private void lerUsuarios() throws IOException {
        for (String linha : lerLinhas(arquivo("usuarios.csv"))) {
            List<String> c = Csv.split(linha);
            Perfil perfil = Perfil.valueOf(c.get(1));
            Usuario usuario;
            if (perfil == Perfil.ALUNO) {
                usuario = new Aluno(c.get(0), c.get(2), c.get(3), c.get(4), c.size() > 5 ? c.get(5) : "");
            } else if (perfil == Perfil.PROFESSOR) {
                usuario = new Professor(c.get(0), c.get(2), c.get(3), c.get(4));
            } else {
                usuario = new Usuario(c.get(0), c.get(2), c.get(3), c.get(4), Perfil.SECRETARIA);
            }
            usuarios.add(usuario);
        }
    }

    private String serializarUsuario(Usuario u) {
        String extra = u instanceof Aluno aluno ? aluno.getRa() : "";
        return Csv.join(u.getId(), u.getPerfil().name(), u.getLogin(), u.getSenha(), u.getNome(), extra);
    }

    private void lerCursos() throws IOException {
        for (String linha : lerLinhas(arquivo("cursos.csv"))) {
            List<String> c = Csv.split(linha);
            Curso curso = new Curso(c.get(0), c.get(1), Integer.parseInt(c.get(2)));
            if (c.size() > 3 && !c.get(3).isBlank()) {
                Arrays.stream(c.get(3).split(",")).filter(s -> !s.isBlank()).forEach(curso::associarDisciplina);
            }
            cursos.add(curso);
        }
    }

    private String serializarCurso(Curso c) {
        String ids = String.join(",", c.getDisciplinaIds());
        return Csv.join(c.getId(), c.getNome(), c.getCreditos(), ids);
    }

    private void lerDisciplinas() throws IOException {
        for (String linha : lerLinhas(arquivo("disciplinas.csv"))) {
            List<String> c = Csv.split(linha);
            Disciplina d = new Disciplina(c.get(0), c.get(1), c.get(2), c.get(3));
            if (c.size() > 4 && !c.get(4).isBlank()) {
                d.setProfessorId(c.get(4));
            }
            disciplinas.add(d);
        }
    }

    private String serializarDisciplina(Disciplina d) {
        return Csv.join(d.getId(), d.getCodigo(), d.getNome(), d.getCursoId(),
                d.getProfessorId() == null ? "" : d.getProfessorId());
    }

    private void lerOfertas() throws IOException {
        for (String linha : lerLinhas(arquivo("ofertas.csv"))) {
            List<String> c = Csv.split(linha);
            OfertaDisciplina o = new OfertaDisciplina(c.get(0), c.get(1), c.get(2));
            o.setStatus(StatusOferta.valueOf(c.get(3)));
            ofertas.add(o);
        }
    }

    private String serializarOferta(OfertaDisciplina o) {
        return Csv.join(o.getId(), o.getSemestre(), o.getDisciplinaId(), o.getStatus().name());
    }

    private void lerMatriculas() throws IOException {
        for (String linha : lerLinhas(arquivo("matriculas.csv"))) {
            List<String> c = Csv.split(linha);
            matriculas.add(new Matricula(c.get(0), c.get(1), c.get(2), TipoMatricula.valueOf(c.get(3))));
        }
    }

    private String serializarMatricula(Matricula m) {
        return Csv.join(m.getId(), m.getAlunoId(), m.getOfertaId(), m.getTipo().name());
    }

    private void lerPeriodo() throws IOException {
        List<String> linhas = lerLinhas(arquivo("periodo.csv"));
        if (!linhas.isEmpty()) {
            List<String> c = Csv.split(linhas.get(0));
            periodo = new PeriodoMatriculas(c.get(0), Boolean.parseBoolean(c.get(1)));
        }
    }

    private void atualizarSequencia() {
        int max = 0;
        max = Math.max(max, maxNumerico(usuarios.stream().map(Usuario::getId).toList()));
        max = Math.max(max, maxNumerico(cursos.stream().map(Curso::getId).toList()));
        max = Math.max(max, maxNumerico(disciplinas.stream().map(Disciplina::getId).toList()));
        max = Math.max(max, maxNumerico(ofertas.stream().map(OfertaDisciplina::getId).toList()));
        max = Math.max(max, maxNumerico(matriculas.stream().map(Matricula::getId).toList()));
        sequencia.set(max + 1);
    }

    private int maxNumerico(List<String> ids) {
        return ids.stream()
                .map(id -> id.replaceAll("\\D+", ""))
                .filter(s -> !s.isBlank())
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
    }
}
