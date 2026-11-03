package ar.com.GestionTurnos.dao;


import ar.com.GestionTurnos.entities.Profesionales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProfesionalesDao extends JpaRepository<Profesionales, Integer> {
    @Query("select c from Profesionales c where c.dni like ?1")
    List<Profesionales> findByDni(String name);

    Page<Profesionales> findAll(Pageable pageable);
}
