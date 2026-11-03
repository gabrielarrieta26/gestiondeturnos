package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.entities.Pacientes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPacientesService {

    List<Pacientes> getAll();
    Pacientes get(Integer id);
    void save(Pacientes entity);
    String delete(Pacientes entity);
    Page<Pacientes> findAll(Pageable pageable);
    List<Pacientes> findByDni(String dni);
    List<Pacientes> findByApellido(String apellido);
}
