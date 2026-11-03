package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.entities.TurnosProfesionales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITurnosProfesionalesService {

    List<TurnosProfesionales> getAll();
    TurnosProfesionales get(String id);
    void save(TurnosProfesionales entity);
    String delete(TurnosProfesionales entity);
    Page<TurnosProfesionales> findAll(Pageable pageable);
    Optional<TurnosProfesionales> findByProfesionalAndTurnoGeneral(Profesionales profesional, TurnosGenerales turnoGeneral);
    List<TurnosProfesionales> findByProfesionalId(Integer profesionalId);
}
