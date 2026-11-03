package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.TurnosEstados;
import ar.com.GestionTurnos.services.ITurnosEstadosService;
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

@Controller
public class TurnosEstadosController {

    @Autowired
    ITurnosEstadosService entityService;

    @RequestMapping(value = "/turnosestados", method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<TurnosEstados> centroPage = entityService.findAll(pageable);
        PageWrapper<TurnosEstados> page = new PageWrapper<TurnosEstados>(centroPage, "/turnosestados");
        model.addAttribute("entities", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("entity", new TurnosEstados());
        return "../turnosestados/index";
    }

    @RequestMapping("turnosestados/refresh")
    public String refresh() {
        return "redirect:/turnosestados";
    }

    @RequestMapping(value = "turnosestados/search", method = RequestMethod.POST)
    public String search(Model model, TurnosEstados entity) {
        if (entity.getDescrip().equals("")) {
            return refresh();
        }
        model.addAttribute("entities", entityService.findByDescripActivo(entity.getDescrip()));
        model.addAttribute("entity", new TurnosEstados());
        model.addAttribute("page", null);
        return "../turnosestados/index";
    }

    @RequestMapping("turnosestados/create/{id}")
    public String create(@PathVariable Long id, Model model) {
        model.addAttribute("entity", new TurnosEstados());
        return "../turnosestados/edit";
    }

    @RequestMapping("turnosestados/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("entity", entityService.get(id));
        return "../turnosestados/edit";
    }

    @RequestMapping(value = "turnosestados", method = RequestMethod.POST)
    public String save(Model model, @Validated TurnosEstados entity) {
        if (entity.getDescrip() == null || entity.getDescrip().equals("")) {
            model.addAttribute("message", "Descripción Incorrecta");
            model.addAttribute("entity", entity);
            return "../turnosestados/edit";
        }

        entityService.save(entity);
        return "redirect:/turnosestados";
    }

    @RequestMapping("turnosestados/delete/{id}")
    public String delete(@PathVariable Long id, Model model, Pageable pageable) {
        try {
            TurnosEstados entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/turnosestados";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<TurnosEstados> centroPage = entityService.findAll(pageable);
            PageWrapper<TurnosEstados> page = new PageWrapper<TurnosEstados>(centroPage, "/turnosestados");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new TurnosEstados());
            return "../turnosestados/index";
        }
    }

}
