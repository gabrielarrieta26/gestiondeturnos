package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.entities.Turnos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ITurnosService {

    List<Turnos> getAll();
    Turnos get(String id);
    void save(Turnos entity);
    String delete(Turnos entity);
    Page<Turnos> findAll(Pageable pageable);
    List<Turnos> findByFecha(LocalDate fecha);
    List<Turnos> findByFechaAndProfesionalId(LocalDate fecha, Integer profesionalId);
    List<Turnos> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
