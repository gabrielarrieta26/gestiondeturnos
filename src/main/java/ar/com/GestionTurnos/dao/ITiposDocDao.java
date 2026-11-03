package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.TiposDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITiposDocDao extends JpaRepository<TiposDoc, Integer> {
    @Query("select c from TiposDoc c where c.codigo like ?1")
    public List<TiposDoc> findByCodigo(String name);

    Page<TiposDoc> findAll(Pageable pageable);
}
