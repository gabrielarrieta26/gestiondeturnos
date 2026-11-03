package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.entities.Profesionales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProfesionalesService {

    List<Profesionales> getAll();
    Profesionales get(Integer id);
    void save(Profesionales entity);
    String delete(Profesionales entity);
    Page<Profesionales> findAll(Pageable pageable);
    List<Profesionales> findByDni(String dni);
}
