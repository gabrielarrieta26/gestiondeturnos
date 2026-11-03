package ar.com.GestionTurnos.servicesimp;


import ar.com.GestionTurnos.dao.IEspecialidadesDao;
import ar.com.GestionTurnos.entities.Especialidades;
import ar.com.GestionTurnos.services.IEspecialidadesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EspecialidadesService implements IEspecialidadesService {

    @Autowired
    private IEspecialidadesDao entityDao;

    public List<Especialidades> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "especialidad"));
    }

    public Page<Especialidades> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "especialidad")));
    }

    public List<Especialidades> findByEspecialidad(String especialidad) {
        return entityDao.findByEspecialidad("%" + especialidad + "%");
    }

    public Especialidades get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(Especialidades entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(Especialidades entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

}
