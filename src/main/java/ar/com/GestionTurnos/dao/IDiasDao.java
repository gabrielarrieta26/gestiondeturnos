package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.Dias;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDiasDao extends JpaRepository<Dias, Integer> {
    @Query("select c from Dias c where c.abreviatura like ?1")
    public List<Dias> findByAbreviatura(String name);

    Page<Dias> findAll(Pageable pageable);
}
