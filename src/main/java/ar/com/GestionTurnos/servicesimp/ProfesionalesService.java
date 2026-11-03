package ar.com.GestionTurnos.servicesimp;


import ar.com.GestionTurnos.dao.IProfesionalesDao;
import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.services.IProfesionalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfesionalesService implements IProfesionalesService {

    @Autowired
    private IProfesionalesDao entityDao;

    public List<Profesionales> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "dni"));
    }

    public Page<Profesionales> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "dni")));
    }

    public List<Profesionales> findByDni(String dni) {
        return entityDao.findByDni("%" + dni + "%");
    }


    public Profesionales get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(Profesionales entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(Profesionales entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
}
