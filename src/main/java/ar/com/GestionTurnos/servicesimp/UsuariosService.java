package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.dao.IUsuariosDao;
import ar.com.GestionTurnos.entities.Usuarios;
import ar.com.GestionTurnos.services.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuariosService implements IUsuariosService {

    @Autowired
    private IUsuariosDao entityDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuarios> getAll() {
        return entityDao.findAll(Sort.by(Sort.Direction.ASC, "username"));
    }

    public Page<Usuarios> findAll(Pageable pageable) {
        return entityDao.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "username")));
    }

    public List<Usuarios> findByDescrip(String descrip) {
        return entityDao.findByDescrip("%" + descrip + "%");
    }


    public Usuarios get(Integer id) {
        return entityDao.findById(id).orElse(null);
    }

    @Transactional
    public void save(Usuarios entity) {
        if (entity.getPassorig() != null && !entity.getPassorig().isBlank()) {
            entity.setPassword(passwordEncoder.encode(entity.getPassorig()));
        }
        entityDao.save(entity);
    }

    @Transactional
    public String delete(Usuarios entity) {
        try {
            entityDao.delete(entity);
            return null;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

    public List<Usuarios> findByRolId(Integer rolId) {
        return entityDao.findByRolId(rolId);
    }

    @Transactional
    public void actualizarPassword(Usuarios usuario, String passwordCodificada) {
        usuario.setPassword(passwordCodificada);
        entityDao.save(usuario);
    }

    public Usuarios findUsuario(String usuario, String pass) {
        List<Usuarios> result = entityDao.findUsuario(usuario, pass, PageRequest.of(0, 1));
        try {
            if (result == null) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return null;
        }
    }

}

