package br.pucminas.matriculas.repository;

import br.pucminas.matriculas.modelo.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {
    Optional<Aluno> findByLogin(String login);
    Optional<Aluno> findByRa(String ra);
}
