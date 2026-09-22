package br.pucminas.matriculas.repository;

import br.pucminas.matriculas.modelo.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, String> {
    Optional<Curso> findByNome(String nome);
}
