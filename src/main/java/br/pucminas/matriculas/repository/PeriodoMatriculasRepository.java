package br.pucminas.matriculas.repository;

import br.pucminas.matriculas.modelo.PeriodoMatriculas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodoMatriculasRepository extends JpaRepository<PeriodoMatriculas, String> {
    Optional<PeriodoMatriculas> findBySemestre(String semestre);
    Optional<PeriodoMatriculas> findFirstByOrderBySemestreDesc();
}
