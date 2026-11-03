package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.ITurnosGeneralesDao;
import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.services.ITurnosGeneralesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TurnosGeneralesService implements ITurnosGeneralesService {

    @Autowired
    private ITurnosGeneralesDao entityDao;

    public List<TurnosGenerales> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "estado"));
    }

    public Page<TurnosGenerales> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "estado")));
    }

    public List<TurnosGenerales> findByEstado(Boolean estado) {
        return entityDao.findByEstado(estado);
    }

    public Optional<TurnosGenerales> findByDiaAndHorario(Dias dia, Horarios horario) {
        if (dia == null || horario == null || dia.getId() == null || horario.getId() == null) {
            return Optional.empty();
        }
        return entityDao.findByDiaAndHorario(dia.getId(), horario.getId());
    }

    public List<TurnosGenerales> findByDia(Dias dia) {
        return entityDao.findByDia(dia);
    }

    public TurnosGenerales get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(TurnosGenerales entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(TurnosGenerales entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

}
