package ar.com.GestionTurnos.services;


import ar.com.GestionTurnos.entities.Horarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IHorariosService {

    List<Horarios> getAll();
    Page<Horarios> findAll(Pageable pageable);
    List<Horarios> findByDescrip(String descrip);
    Horarios get(Integer id);
    void save(Horarios entity);
    String delete(Horarios entity);
}
