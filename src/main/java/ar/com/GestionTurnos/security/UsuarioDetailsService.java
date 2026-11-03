package ar.com.GestionTurnos.security;

import ar.com.GestionTurnos.dao.IUsuariosDao;
import ar.com.GestionTurnos.entities.Usuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService, UserDetailsPasswordService {

    @Autowired
    private IUsuariosDao usuariosDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuarios usuario = usuariosDao.findOneByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new UsuarioPrincipal(usuario);
    }

    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Usuarios usuario = usuariosDao.findOneByUsername(user.getUsername());
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        usuario.setPassword(newPassword);
        usuariosDao.save(usuario);
        return new UsuarioPrincipal(usuario);
    }
}
