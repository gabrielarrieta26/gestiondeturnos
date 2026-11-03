package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.ITurnosDao;
import ar.com.GestionTurnos.entities.Turnos;
import ar.com.GestionTurnos.services.ITurnosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TurnosService implements ITurnosService {

    private static final DateTimeFormatter FMT_ID = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Autowired
    private ITurnosDao entityDao;

    public List<Turnos> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "fecha"));
    }

    public Page<Turnos> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "fecha")));
    }

    public List<Turnos> findByFecha(LocalDate fecha) {
        return entityDao.findByFecha(fecha);
    }

    public List<Turnos> findByFechaAndProfesionalId(LocalDate fecha, Integer profesionalId) {
        return entityDao.findByFechaAndProfesionalId(fecha, profesionalId);
    }

    public List<Turnos> findByFechaBetween(LocalDate desde, LocalDate hasta) {
        return entityDao.findByFechaBetween(desde, hasta);
    }

    public Turnos get(String id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(Turnos entity) {
        if (entity.getId() == null || entity.getId().trim().isEmpty()) {
            entity.setId(generarIdLegible());
        }
        entityDao.save(entity);
    }

    @Transactional
    public String delete(Turnos entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

    private String generarIdLegible() {
        String base = "TUR-" + LocalDateTime.now().format(FMT_ID);
        String id;
        do {
            int sufijo = ThreadLocalRandom.current().nextInt(100, 1000);
            id = base + "-" + sufijo;
        } while (entityDao.existsById(id));
        return id;
    }
}
