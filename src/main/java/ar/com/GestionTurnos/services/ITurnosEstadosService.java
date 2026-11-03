package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.entities.TurnosEstados;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITurnosEstadosService {
    List<TurnosEstados> getAllActivo();
    Page<TurnosEstados> findAll(Pageable pageable);
    List<TurnosEstados> findByDescripActivo(String descrip);
    TurnosEstados get(Long id);
    void save(TurnosEstados entity);
    String delete(TurnosEstados entity);
}
