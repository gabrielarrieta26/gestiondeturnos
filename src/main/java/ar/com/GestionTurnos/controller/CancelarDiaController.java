package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Usuarios;
import ar.com.GestionTurnos.models.CancelacionDia;
import ar.com.GestionTurnos.services.ICancelarDiaService;
import ar.com.GestionTurnos.services.IRolesService;
import ar.com.GestionTurnos.services.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CancelarDiaController {

    private static final String VISTA = "../turnos/cancelar-dia";

    @Autowired
    private ICancelarDiaService cancelarDiaService;
    @Autowired
    private IRolesService rolesService;
    @Autowired
    private IUsuariosService usuariosService;

    @GetMapping("/turnos/cancelar-dia")
    public String formulario(Model model) {
        if (!model.containsAttribute("criterio")) {
            model.addAttribute("criterio", new CancelacionDia());
        }
        cargarCatalogos(model);
        return VISTA;
    }

    @PostMapping("/turnos/cancelar-dia")
    public String procesar(Model model,
                           @RequestParam(value = "accion", defaultValue = "revisar") String accion,
                           @RequestParam(value = "desde", required = false) String desde,
                           @RequestParam(value = "hasta", required = false) String hasta,
                           @RequestParam(value = "horaDesde", required = false) String horaDesde,
                           @RequestParam(value = "horaHasta", required = false) String horaHasta,
                           @RequestParam(value = "alcanceRoles", defaultValue = "elegir") String alcanceRoles,
                           @RequestParam(value = "alcanceUsuarios", defaultValue = "elegir") String alcanceUsuarios,
                           @RequestParam(value = "rolIds", required = false) List<Integer> rolIds,
                           @RequestParam(value = "usuarioIds", required = false) List<Integer> usuarioIds,
                           @RequestParam(value = "motivo", required = false) String motivo,
                           @RequestParam(value = "confirmar", required = false) String confirmar,
                           RedirectAttributes redirect) {
        CancelacionDia criterio = armar(desde, hasta, horaDesde, horaHasta, alcanceRoles, alcanceUsuarios, rolIds, usuarioIds, motivo);
        if (!criterio.getErrores().isEmpty()) {
            model.addAttribute("criterio", criterio);
            cargarCatalogos(model);
            return VISTA;
        }

        cancelarDiaService.clasificar(criterio);
        if ("confirmar".equals(accion)) {
            if (!"true".equals(confirmar)) {
                criterio.getErrores().add("Debe confirmar que revisó la cantidad de turnos.");
                criterio.setRevisado(true);
                model.addAttribute("criterio", criterio);
                cargarCatalogos(model);
                return VISTA;
            }
            if (criterio.getACancelar().isEmpty()) {
                criterio.getErrores().add("No hay turnos para cancelar con este criterio.");
                model.addAttribute("criterio", criterio);
                cargarCatalogos(model);
                return VISTA;
            }
            int cantidad = cancelarDiaService.confirmar(criterio);
            String mensajeOk = cantidad == 1
                    ? "Se canceló 1 turno."
                    : "Se cancelaron " + cantidad + " turnos.";
            redirect.addFlashAttribute("mensajeOk", mensajeOk);
            return "redirect:/turnos/cancelar-dia";
        }

        criterio.setRevisado(true);
        model.addAttribute("criterio", criterio);
        cargarCatalogos(model);
        return VISTA;
    }

    private CancelacionDia armar(String desde, String hasta, String horaDesde, String horaHasta,
                                 String alcanceRoles, String alcanceUsuarios,
                                 List<Integer> rolIds, List<Integer> usuarioIds, String motivo) {
        CancelacionDia criterio = new CancelacionDia();
        criterio.setDesdeTexto(desde == null ? "" : desde);
        criterio.setHastaTexto(hasta == null ? "" : hasta);
        criterio.setHoraDesdeTexto(horaDesde == null ? "" : horaDesde);
        criterio.setHoraHastaTexto(horaHasta == null ? "" : horaHasta);
        criterio.setTodosLosRoles("todos".equals(alcanceRoles));
        criterio.setTodosLosUsuarios("todos".equals(alcanceUsuarios));
        criterio.setRolIds(rolIds);
        criterio.setUsuarioIds(usuarioIds);
        criterio.setMotivo(motivo);
        criterio.setDesde(parseFecha(desde, "desde", criterio.getErrores()));
        criterio.setHasta(parseFecha(hasta, "hasta", criterio.getErrores()));
        criterio.setHoraDesde(parseHora(horaDesde, "desde", criterio.getErrores()));
        criterio.setHoraHasta(parseHora(horaHasta, "hasta", criterio.getErrores()));

        if (criterio.getDesde() != null && criterio.getHasta() != null && criterio.getHasta().isBefore(criterio.getDesde())) {
            criterio.getErrores().add("La fecha hasta no puede ser anterior a la fecha desde.");
        }
        if (horaVacia(horaDesde) != horaVacia(horaHasta)) {
            criterio.getErrores().add("Si indica una hora, complete desde y hasta.");
        }
        if (criterio.getHoraDesde() != null && criterio.getHoraHasta() != null
                && criterio.getHoraHasta().isBefore(criterio.getHoraDesde())) {
            criterio.getErrores().add("La hora hasta no puede ser anterior a la hora desde.");
        }
        if (!criterio.isTodosLosRoles() && criterio.getRolIds().isEmpty()) {
            criterio.getErrores().add("Seleccione al menos un rol.");
        }
        if (!criterio.isTodosLosUsuarios() && criterio.getUsuarioIds().isEmpty()) {
            criterio.getErrores().add("Seleccione al menos un usuario.");
        }
        if (criterio.getMotivo().isBlank()) {
            criterio.getErrores().add("El motivo es obligatorio.");
        } else if (criterio.getMotivo().trim().length() > 500) {
            criterio.getErrores().add("El motivo no puede superar los 500 caracteres.");
        }
        return criterio;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("roles", rolesService.getAll());
        List<Usuarios> usuarios = new ArrayList<>();
        for (Usuarios usuario : usuariosService.getAll()) {
            if (usuario.getRol() != null) {
                usuarios.add(usuario);
            }
        }
        model.addAttribute("usuarios", usuarios);
    }

    private LocalDate parseFecha(String valor, String etiqueta, List<String> errores) {
        if (valor == null || valor.isBlank()) {
            errores.add("La fecha " + etiqueta + " es obligatoria.");
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException e) {
            errores.add("La fecha " + etiqueta + " es incorrecta.");
            return null;
        }
    }

    private LocalTime parseHora(String valor, String etiqueta, List<String> errores) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(valor.trim());
        } catch (DateTimeParseException e) {
            errores.add("La hora " + etiqueta + " es incorrecta.");
            return null;
        }
    }

    private boolean horaVacia(String valor) {
        return valor == null || valor.isBlank();
    }
}
