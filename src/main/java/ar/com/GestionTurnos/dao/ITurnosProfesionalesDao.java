package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.TurnosProfesionales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITurnosProfesionalesDao extends JpaRepository<TurnosProfesionales, String> {
    Page<TurnosProfesionales> findAll(Pageable pageable);

    @Query("SELECT t FROM TurnosProfesionales t WHERE t.profesional.id = :profesionalId AND t.turnoGeneral.id = :turnoGeneralId")
    Optional<TurnosProfesionales> findByProfesionalAndTurnoGeneral(@Param("profesionalId") Integer profesionalId,
                                                                   @Param("turnoGeneralId") Integer turnoGeneralId);

    @Query("SELECT t FROM TurnosProfesionales t WHERE t.profesional.id = :profesionalId AND (t.activo = true OR t.activo IS NULL)")
    List<TurnosProfesionales> findByProfesionalId(@Param("profesionalId") Integer profesionalId);
}
