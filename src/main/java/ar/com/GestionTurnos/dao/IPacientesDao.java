package ar.com.GestionTurnos.dao;


import ar.com.GestionTurnos.entities.Pacientes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPacientesDao extends JpaRepository<Pacientes, Integer> {
    @Query("select c from Pacientes c where c.dni like ?1")
    List<Pacientes> findByDni(String name);

    @Query("select c from Pacientes c where lower(c.apellido) like lower(?1)")
    List<Pacientes> findByApellido(String apellido);

    Page<Pacientes> findAll(Pageable pageable);
}
