package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.ITiposDocDao;
import ar.com.GestionTurnos.entities.TiposDoc;
import ar.com.GestionTurnos.services.ITiposDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TiposDocService implements ITiposDocService {

    @Autowired
    private ITiposDocDao entityDao;

    public List<TiposDoc> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "codigo"));
    }

    public Page<TiposDoc> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "codigo")));
    }

    public List<TiposDoc> findByCodigo(String codigo) {
        return entityDao.findByCodigo("%" + codigo + "%");
    }

    public TiposDoc get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(TiposDoc entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(TiposDoc entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
}
