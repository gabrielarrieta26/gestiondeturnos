package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Roles;
import ar.com.GestionTurnos.entities.Usuarios;
import ar.com.GestionTurnos.security.UsuarioPrincipal;
import ar.com.GestionTurnos.services.IRolesService;
import ar.com.GestionTurnos.services.IUsuariosService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IngresoRolController {

    @Autowired
    private IRolesService rolesService;

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/sesion/rol/{id}")
    public String formulario(@PathVariable Integer id, Model model) {
        Roles rol = rolesService.get(id);
        if (rol == null) {
            return "redirect:/";
        }
        model.addAttribute("rol", rol);
        return "ingreso-rol";
    }

    @PostMapping("/sesion/rol/{id}")
    public String ingresar(@PathVariable Integer id,
                           @RequestParam(required = false) String password,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           RedirectAttributes redirect) {
        Roles rol = rolesService.get(id);
        if (rol == null) {
            return "redirect:/";
        }
        Usuarios coincidencia = usuarioConPassword(id, password);
        if (coincidencia == null) {
            redirect.addFlashAttribute("error", "Contraseña incorrecta.");
            return "redirect:/sesion/rol/" + id;
        }
        if (passwordEncoder.upgradeEncoding(coincidencia.getPassword())) {
            usuariosService.actualizarPassword(coincidencia, passwordEncoder.encode(password));
        }
        iniciarSesion(coincidencia, request, response);
        return "redirect:/";
    }

    private Usuarios usuarioConPassword(Integer rolId, String password) {
        if (password == null || password.isBlank()) {
            return null;
        }
        for (Usuarios usuario : usuariosService.findByRolId(rolId)) {
            if (Boolean.TRUE.equals(usuario.getInactivo())) {
                continue;
            }
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return usuario;
            }
        }
        return null;
    }

    private void iniciarSesion(Usuarios usuario, HttpServletRequest request, HttpServletResponse response) {
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        request.changeSessionId();
        securityContextRepository.saveContext(context, request, response);
    }
}
