package br.pucminas.matriculas.repository;

import br.pucminas.matriculas.modelo.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, String> {
    Optional<Disciplina> findByCodigo(String codigo);
    List<Disciplina> findByProfessorId(String professorId);
}
