package ar.com.GestionTurnos.services;


import ar.com.GestionTurnos.entities.ObrasSociales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IObrasSocialesService {

    List<ObrasSociales> getAll();
    Page<ObrasSociales> findAll(Pageable pageable);
    List<ObrasSociales> findByCodigo(String codigo);
    List<ObrasSociales> findSuspendidas();
    ObrasSociales get(Integer id);
    void save(ObrasSociales entity);
    String delete(ObrasSociales entity);
}
