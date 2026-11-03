package ar.com.GestionTurnos.security;

import ar.com.GestionTurnos.entities.Roles;
import ar.com.GestionTurnos.services.IRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class SeguridadModelAdvice {

    @Autowired
    private IRolesService rolesService;

    @ModelAttribute("rolesCreados")
    public List<Roles> rolesCreados() {
        if (autenticacion() == null) {
            return List.of();
        }
        return rolesService.getAll();
    }

    @ModelAttribute("usuarioActual")
    public String usuarioActual() {
        Authentication auth = autenticacion();
        return auth == null ? null : auth.getName();
    }

    @ModelAttribute("esAdministrador")
    public boolean esAdministrador() {
        Authentication auth = autenticacion();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(authority -> RolAutoridad.ADMIN.equals(authority.getAuthority()));
    }

    private Authentication autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth;
    }
}
