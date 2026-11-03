package ar.com.GestionTurnos.services;


import ar.com.GestionTurnos.entities.Dias;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDiasService {

    List<Dias> getAll();
    Page<Dias> findAll(Pageable pageable);
    List<Dias> findByAbreviatura(String abreviatura);
    Dias get(Integer id);
    void save(Dias entity);
    String delete(Dias entity);
}
