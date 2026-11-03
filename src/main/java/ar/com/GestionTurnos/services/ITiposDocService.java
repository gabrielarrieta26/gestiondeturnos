package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.entities.TiposDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITiposDocService {

    List<TiposDoc> getAll();
    Page<TiposDoc> findAll(Pageable pageable);
    List<TiposDoc> findByCodigo(String codigo);
    TiposDoc get(Integer id);
    void save(TiposDoc entity);
    String delete(TiposDoc entity);
}
