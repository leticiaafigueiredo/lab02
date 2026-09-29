package br.pucminas.matriculas.controller;

import br.pucminas.matriculas.dto.*;
import br.pucminas.matriculas.servico.SecretariaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretaria")
public class SecretariaController {

    private final SecretariaService secretariaService;

    public SecretariaController(SecretariaService secretariaService) {
        this.secretariaService = secretariaService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> obterResumo() {
        return ResponseEntity.ok(secretariaService.obterResumo());
    }

    @PostMapping("/encerrar-periodo")
    public ResponseEntity<Void> encerrarPeriodo(@RequestParam(defaultValue = "2026.2") String semestre) {
        secretariaService.encerrarPeriodoComQuorum(semestre);
        return ResponseEntity.ok().build();
    }

    // --- Cursos ---
    @GetMapping("/cursos")
    public ResponseEntity<List<CursoDTO>> listarCursos() {
        return ResponseEntity.ok(secretariaService.listarCursos());
    }

    @PostMapping("/cursos")
    public ResponseEntity<CursoDTO> cadastrarCurso(@Valid @RequestBody CursoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secretariaService.cadastrarCurso(dto));
    }

    // --- Professores ---
    @GetMapping("/professores")
    public ResponseEntity<List<ProfessorDTO>> listarProfessores() {
        return ResponseEntity.ok(secretariaService.listarProfessores());
    }

    @PostMapping("/professores")
    public ResponseEntity<ProfessorDTO> cadastrarProfessor(@Valid @RequestBody ProfessorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secretariaService.cadastrarProfessor(dto));
    }

    // --- Alunos ---
    @GetMapping("/alunos")
    public ResponseEntity<List<AlunoDTO>> listarAlunos() {
        return ResponseEntity.ok(secretariaService.listarAlunos());
    }

    @PostMapping("/alunos")
    public ResponseEntity<AlunoDTO> cadastrarAluno(@Valid @RequestBody AlunoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secretariaService.cadastrarAluno(dto));
    }

    // --- Disciplinas ---
    @GetMapping("/disciplinas")
    public ResponseEntity<List<DisciplinaDTO>> listarDisciplinas() {
        return ResponseEntity.ok(secretariaService.listarDisciplinas());
    }

    @PostMapping("/disciplinas")
    public ResponseEntity<DisciplinaDTO> cadastrarDisciplina(@Valid @RequestBody DisciplinaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secretariaService.cadastrarDisciplina(dto));
    }

    // --- Gerar Oferta / Currículo Semestral ---
    @PostMapping("/ofertas")
    public ResponseEntity<OfertaDisponivelDTO> gerarOfertaSemestral(@Valid @RequestBody GerarOfertaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secretariaService.gerarOfertaSemestral(dto));
    }
}
