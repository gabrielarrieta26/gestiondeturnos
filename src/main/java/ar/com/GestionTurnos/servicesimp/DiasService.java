package ar.com.GestionTurnos.servicesimp;


import ar.com.GestionTurnos.dao.IDiasDao;
import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.services.IDiasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiasService implements IDiasService {

    @Autowired
    private IDiasDao entityDao;

    public List<Dias> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "abreviatura"));
    }

    public Page<Dias> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "abreviatura")));
    }

    public List<Dias> findByAbreviatura(String abreviatura) {
        return entityDao.findByAbreviatura("%" + abreviatura + "%");
    }

    public Dias get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(Dias entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(Dias entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
}

