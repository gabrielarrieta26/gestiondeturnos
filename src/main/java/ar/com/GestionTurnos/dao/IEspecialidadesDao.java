package ar.com.GestionTurnos.dao;

import ar.com.GestionTurnos.entities.Especialidades;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEspecialidadesDao extends JpaRepository<Especialidades, Integer> {
    @Query("select c from Especialidades c where c.especialidad like ?1")
    public List<Especialidades> findByEspecialidad(String name);

    Page<Especialidades> findAll(Pageable pageable);
}
