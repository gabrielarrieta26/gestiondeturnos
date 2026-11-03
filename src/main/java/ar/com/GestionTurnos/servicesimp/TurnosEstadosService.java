package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.ITurnosEstadosDao;
import ar.com.GestionTurnos.entities.TurnosEstados;
import ar.com.GestionTurnos.services.ITurnosEstadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurnosEstadosService implements ITurnosEstadosService {

    @Autowired
    private ITurnosEstadosDao entityDao;

    public List<TurnosEstados> getAllActivo() {

        return entityDao.findAllActivos();
    }

    public Page<TurnosEstados> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "descrip")));
    }

    public List<TurnosEstados> findByDescripActivo(String descrip) {
        return entityDao.findByDescripActivo("%" + descrip + "%");
    }

    public List<TurnosEstados> findByDescripActivo() {
        return entityDao.findAllActivos();
    }

    public TurnosEstados get(Long id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(TurnosEstados entity) {
        if (entity.getInhabilitar() == null) {
            entity.setInhabilitar(false);
        }
        entityDao.save(entity);
    }

    @Transactional
    public String delete(TurnosEstados entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
}
