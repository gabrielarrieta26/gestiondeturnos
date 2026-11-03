package ar.com.GestionTurnos.controller;


import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.entities.Usuarios;
import ar.com.GestionTurnos.services.*;
import ar.com.GestionTurnos.utiles.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfesionalesController {

    @Autowired
    IProfesionalesService entityService;
    @Autowired
    ITiposDocService tipodocService;
    @Autowired
    IEspecialidadesService especialidadService;
    @Autowired
    IUsuariosService usuarioService;

    @RequestMapping(value = "/profesionales", method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<Profesionales> centroPage = entityService.findAll(pageable);
        PageWrapper<Profesionales> page = new PageWrapper<Profesionales>(centroPage, "/profesionales");
        model.addAttribute("entities", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("entity", new Profesionales());
        return "../profesionales/index";
    }

    @RequestMapping("/profesionales/refresh")
    public String refresh() {
        return "redirect:/profesionales";
    }

    @RequestMapping(value = "/profesionales/search", method = RequestMethod.POST)
    public String search(Model model, Profesionales entity) {
        if (entity.getDni().equals("")) {
            return refresh();
        }
        model.addAttribute("entities", entityService.findByDni(entity.getDni()));
        model.addAttribute("entity", new Profesionales());
        model.addAttribute("page", null);
        return "../profesionales/index";
    }

    @RequestMapping("/profesionales/create/{id}")
    public String create(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", new Profesionales());
        model.addAttribute("tiposdoc", tipodocService.getAll());
        model.addAttribute("especialidades", especialidadService.getAll());
        model.addAttribute("usuarios", usuarioService.getAll());
        return "../profesionales/edit";
    }

    @RequestMapping("/profesionales/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.get(id));
        model.addAttribute("tiposdoc", tipodocService.getAll());
        model.addAttribute("especialidades", especialidadService.getAll());
        model.addAttribute("usuarios", usuarioService.getAll());
        return "../profesionales/edit";
    }

    @RequestMapping(value = "profesionales", method = RequestMethod.POST)
    public String save(Model model, @Validated Profesionales entity,
                       @RequestParam(value = "usuarioId", required = false) String usuarioId) {
        String errores = "";
        if ("INHABILITAR".equals(usuarioId)) {
            entity.setUsuario(null);
        } else {
            Usuarios usuario = null;
            try {
                if (usuarioId != null && !usuarioId.isBlank()) {
                    usuario = usuarioService.get(Integer.valueOf(usuarioId));
                }
            } catch (NumberFormatException e) {
                usuario = null;
            }
            if (usuario == null) {
                errores = "Debe seleccionar un usuario";
            } else {
                entity.setUsuario(usuario);
            }
        }

        if (!errores.equals("")) {
            model.addAttribute("message", errores);
            model.addAttribute("entity", entity);
            model.addAttribute("tiposdoc", tipodocService.getAll());
            model.addAttribute("especialidades", especialidadService.getAll());
            model.addAttribute("usuarios", usuarioService.getAll());
            return "../profesionales/edit";
        }

        entityService.save(entity);
        return "redirect:/profesionales";
    }

    @RequestMapping("profesionales/delete/{id}")
    public String delete(@PathVariable Integer id, Model model, Pageable pageable) {
        try {
            Profesionales entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/profesionales";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<Profesionales> centroPage = entityService.findAll(pageable);
            PageWrapper<Profesionales> page = new PageWrapper<Profesionales>(centroPage, "/profesionales");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new Profesionales());
            return "../profesionales/index";
        }
    }

}
