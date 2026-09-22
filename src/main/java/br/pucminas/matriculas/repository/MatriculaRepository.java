package br.pucminas.matriculas.repository;

import br.pucminas.matriculas.modelo.Matricula;
import br.pucminas.matriculas.modelo.TipoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, String> {
    List<Matricula> findByAlunoId(String alunoId);
    
    @Query("SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId AND m.oferta.semestre = :semestre")
    List<Matricula> findByAlunoIdAndSemestre(@Param("alunoId") String alunoId, @Param("semestre") String semestre);

    Optional<Matricula> findByAlunoIdAndOfertaId(String alunoId, String ofertaId);
    boolean existsByAlunoIdAndOfertaId(String alunoId, String ofertaId);

    List<Matricula> findByOfertaId(String ofertaId);
    long countByOfertaId(String ofertaId);

    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.aluno.id = :alunoId AND m.oferta.semestre = :semestre AND m.tipo = :tipo")
    long countByAlunoIdAndSemestreAndTipo(@Param("alunoId") String alunoId, @Param("semestre") String semestre, @Param("tipo") TipoMatricula tipo);
}
