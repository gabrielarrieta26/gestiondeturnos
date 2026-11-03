package ar.com.GestionTurnos.services;


import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITurnosGeneralesService {

    List<TurnosGenerales> getAll();
    TurnosGenerales get(Integer id);
    void save(TurnosGenerales entity);
    String delete(TurnosGenerales entity);
    Page<TurnosGenerales> findAll(Pageable pageable);
    List<TurnosGenerales> findByEstado(Boolean estado);
    Optional<TurnosGenerales> findByDiaAndHorario(Dias dia, Horarios horario);
    List<TurnosGenerales> findByDia(Dias dia);
}
