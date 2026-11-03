package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.Horarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHorariosDao extends JpaRepository<Horarios, Integer> {
    @Query("select c from Horarios c where c.descrip like ?1")
    public List<Horarios> findByDescrip(String name);

    Page<Horarios> findAll(Pageable pageable);
}