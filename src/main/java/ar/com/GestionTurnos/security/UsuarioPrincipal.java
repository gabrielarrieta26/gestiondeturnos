package ar.com.GestionTurnos.security;

import ar.com.GestionTurnos.entities.Usuarios;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioPrincipal implements UserDetails {

    private final String username;
    private final String password;
    private final boolean activo;
    private final String autoridad;

    public UsuarioPrincipal(Usuarios usuario) {
        this.username = usuario.getUsername();
        this.password = usuario.getPassword() == null ? "" : usuario.getPassword();
        this.activo = !Boolean.TRUE.equals(usuario.getInactivo());
        this.autoridad = RolAutoridad.de(usuario.getRol());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(autoridad));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
