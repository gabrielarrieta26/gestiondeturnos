package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.Turnos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ITurnosDao extends JpaRepository<Turnos, String> {
    Page<Turnos> findAll(Pageable pageable);

    List<Turnos> findByFecha(LocalDate fecha);

    @Query("SELECT t FROM Turnos t WHERE t.fecha = :fecha AND t.profesional.id = :profesionalId AND (t.anulado = false OR t.anulado IS NULL)")
    List<Turnos> findByFechaAndProfesionalId(@Param("fecha") LocalDate fecha, @Param("profesionalId") Integer profesionalId);

    @Query("SELECT t FROM Turnos t LEFT JOIN t.turnoGeneral tg LEFT JOIN tg.horario h WHERE t.fecha >= :desde AND t.fecha <= :hasta ORDER BY t.fecha, h.descrip")
    List<Turnos> findByFechaBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
