package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.IObrasSocialesDao;
import ar.com.GestionTurnos.entities.ObrasSociales;
import ar.com.GestionTurnos.services.IObrasSocialesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObrasSocialesService implements IObrasSocialesService {

    @Autowired
    private IObrasSocialesDao entityDao;

    public List<ObrasSociales> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "codigo"));
    }

    public Page<ObrasSociales> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "codigo")));
    }

    public List<ObrasSociales> findByCodigo(String codigo) {
        return entityDao.findByCodigo("%" + codigo + "%");
    }

    public List<ObrasSociales> findSuspendidas() {
        return entityDao.findBySuspendidaTrue();
    }

    public ObrasSociales get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(ObrasSociales entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(ObrasSociales entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

}
