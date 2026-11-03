package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Pacientes;
import ar.com.GestionTurnos.entities.TiposDoc;
import ar.com.GestionTurnos.services.IPacientesService;
import ar.com.GestionTurnos.services.ITiposDocService;
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

import java.util.List;

@Controller
public class PacientesController {


    @Autowired
    IPacientesService entityService;
    @Autowired
    ITiposDocService tiposdocService;

    @RequestMapping(value = "/pacientes", method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<Pacientes> centroPage = entityService.findAll(pageable);
        PageWrapper<Pacientes> page = new PageWrapper<Pacientes>(centroPage, "/pacientes");
        model.addAttribute("entities", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("entity", new Pacientes());
        return "../pacientes/index";
    }

    @RequestMapping("/pacientes/refresh")
    public String refresh() {
        return "redirect:/pacientes";
    }

    @RequestMapping(value = "/pacientes/search", method = RequestMethod.POST)
    public String search(Model model, Pacientes entity) {
        if (entity.getDni().equals("")) {
            return refresh();
        }
        model.addAttribute("entities", entityService.findByDni(entity.getDni()));
        model.addAttribute("entity", new Pacientes());
        model.addAttribute("page", null);
        return "../pacientes/index";
    }

    @RequestMapping("/pacientes/create/{id}")
    public String create(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", new Pacientes());
        List<TiposDoc> tiposdoc = tiposdocService.getAll();
        model.addAttribute("tiposdoc", tiposdoc);
        return "../pacientes/edit";
    }

    @RequestMapping("/pacientes/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.get(id));
        List<TiposDoc> tiposdoc = tiposdocService.getAll();
        model.addAttribute("tiposdoc", tiposdoc);
        return "../pacientes/edit";
    }

    @RequestMapping(value = "pacientes", method = RequestMethod.POST)
    public String save(Model model, @Validated Pacientes entity) {
        String errores = "";
        if (!errores.equals("")) {
            model.addAttribute("message", errores);
            model.addAttribute("entity", entity);
            List<TiposDoc> tiposdoc = tiposdocService.getAll();
            model.addAttribute("tiposdoc", tiposdoc);
            return "../pacientes/edit";
        }

        entityService.save(entity);
        return "redirect:/pacientes";
    }

    @RequestMapping("pacientes/delete/{id}")
    public String delete(@PathVariable Integer id, Model model, Pageable pageable) {
        try {
            Pacientes entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/pacientes";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<Pacientes> centroPage = entityService.findAll(pageable);
            PageWrapper<Pacientes> page = new PageWrapper<Pacientes>(centroPage, "/pacientes");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new Pacientes());
            return "../pacientes/index";
        }
    }

}
