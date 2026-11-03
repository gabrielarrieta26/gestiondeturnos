package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITurnosGeneralesDao extends JpaRepository<TurnosGenerales, Integer> {
    @Query("SELECT t FROM TurnosGenerales t WHERE t.estado = :estado OR (:estado = false AND t.estado IS NULL)")
    List<TurnosGenerales> findByEstado(@Param("estado") Boolean estado);

    Page<TurnosGenerales> findAll(Pageable pageable);

    @Query("SELECT t FROM TurnosGenerales t WHERE t.dia.id = :diaId AND t.horario.id = :horarioId")
    Optional<TurnosGenerales> findByDiaAndHorario(@Param("diaId") Integer diaId, @Param("horarioId") Integer horarioId);

    List<TurnosGenerales> findByDia(Dias dia);
}
