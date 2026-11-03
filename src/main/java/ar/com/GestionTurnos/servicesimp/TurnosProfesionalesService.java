package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.ITurnosProfesionalesDao;
import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.entities.TurnosProfesionales;
import ar.com.GestionTurnos.services.ITurnosProfesionalesService;
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
public class TurnosProfesionalesService implements ITurnosProfesionalesService {

    @Autowired
    private ITurnosProfesionalesDao entityDao;

    public List<TurnosProfesionales> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Page<TurnosProfesionales> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "id")));
    }

    public TurnosProfesionales get(String id) {
        return entityDao.findById(id).orElse(null);
    }

    public Optional<TurnosProfesionales> findByProfesionalAndTurnoGeneral(Profesionales profesional, TurnosGenerales turnoGeneral) {
        if (profesional == null || turnoGeneral == null || profesional.getId() == null || turnoGeneral.getId() == null) {
            return Optional.empty();
        }
        return entityDao.findByProfesionalAndTurnoGeneral(profesional.getId(), turnoGeneral.getId());
    }

    public List<TurnosProfesionales> findByProfesionalId(Integer profesionalId) {
        return entityDao.findByProfesionalId(profesionalId);
    }

    @Transactional
    public void save(TurnosProfesionales entity) {
        entityDao.save(entity);
    }

    @Transactional
    public String delete(TurnosProfesionales entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
}
