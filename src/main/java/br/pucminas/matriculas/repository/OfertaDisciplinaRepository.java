package br.pucminas.matriculas.repository;

import br.pucminas.matriculas.modelo.OfertaDisciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfertaDisciplinaRepository extends JpaRepository<OfertaDisciplina, String> {
    List<OfertaDisciplina> findBySemestre(String semestre);

    @Query("SELECT o FROM OfertaDisciplina o WHERE o.disciplina.professor.id = :professorId")
    List<OfertaDisciplina> findByProfessorId(@Param("professorId") String professorId);

    @Query("SELECT o FROM OfertaDisciplina o WHERE o.disciplina.professor.id = :professorId AND o.semestre = :semestre")
    List<OfertaDisciplina> findByProfessorIdAndSemestre(@Param("professorId") String professorId, @Param("semestre") String semestre);
}
