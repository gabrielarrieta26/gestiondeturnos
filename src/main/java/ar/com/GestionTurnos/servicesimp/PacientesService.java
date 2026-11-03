package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.IPacientesDao;
import ar.com.GestionTurnos.entities.Pacientes;
import ar.com.GestionTurnos.services.IPacientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacientesService implements IPacientesService {

    @Autowired
    private IPacientesDao entityDao;

    public List<Pacientes> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "dni"));
    }

    public Page<Pacientes> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "dni")));
    }

    public List<Pacientes> findByDni(String dni) {
        return entityDao.findByDni("%" + dni + "%");
    }

    public List<Pacientes> findByApellido(String apellido) {
        return entityDao.findByApellido("%" + apellido + "%");
    }

    public Pacientes get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(Pacientes entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(Pacientes entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

}
