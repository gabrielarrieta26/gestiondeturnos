package ar.com.GestionTurnos.services;


import ar.com.GestionTurnos.entities.Especialidades;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEspecialidadesService {

    List<Especialidades> getAll();
    Page<Especialidades> findAll(Pageable pageable);
    List<Especialidades> findByEspecialidad(String especialidad);
    Especialidades get(Integer id);
    void save(Especialidades entity);
    String delete(Especialidades entity);
}
