package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.ObrasSociales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IObrasSocialesDao extends JpaRepository<ObrasSociales, Integer> {
    @Query("select c from ObrasSociales c where c.codigo like ?1")
    public List<ObrasSociales> findByCodigo(String name);

    @Query("select c from ObrasSociales c where c.suspendida = true order by c.codigo")
    List<ObrasSociales> findBySuspendidaTrue();

    Page<ObrasSociales> findAll(Pageable pageable);
}
