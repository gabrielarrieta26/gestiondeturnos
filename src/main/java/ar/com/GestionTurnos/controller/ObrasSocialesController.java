package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.ObrasSociales;
import ar.com.GestionTurnos.services.IObrasSocialesService;
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
public class ObrasSocialesController {


    @Autowired
    IObrasSocialesService entityService;

    @RequestMapping(value = "/obrassociales", method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<ObrasSociales> centroPage = entityService.findAll(pageable);
        PageWrapper<ObrasSociales> page = new PageWrapper<ObrasSociales>(centroPage, "/obrassociales");
        model.addAttribute("entities", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("entity", new ObrasSociales());
        return "../obrassociales/index";
    }

    @RequestMapping("obrassociales/refresh")
    public String refresh() {
        return "redirect:/obrassociales";
    }

    @RequestMapping(value = "obrassociales/search", method = RequestMethod.POST)
    public String search(Model model, ObrasSociales entity) {
        if (entity.getCodigo().equals("")) {
            return refresh();
        }
        model.addAttribute("entities", entityService.findByCodigo(entity.getCodigo()));
        model.addAttribute("entity", new ObrasSociales());
        model.addAttribute("page", null);
        return "../obrassociales/index";
    }

    @RequestMapping("obrassociales/create/{id}")
    public String create(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", new ObrasSociales());
        return "../obrassociales/edit";
    }

    @RequestMapping("obrassociales/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.get(id));
        return "../obrassociales/edit";
    }

    @RequestMapping(value = "obrassociales", method = RequestMethod.POST)
    public String save(Model model, @Validated ObrasSociales entity) {
        if (entity.getCodigo().equals("")) {
            model.addAttribute("message", "Descripción Incorrecta");
            model.addAttribute("entity", entity);
            return "../obrassociales/edit";
        }

        entityService.save(entity);
        return "redirect:/obrassociales";
    }

    @RequestMapping(value = "/obrassociales/{id}/activar", method = RequestMethod.POST)
    public String activar(@PathVariable Integer id) {
        ObrasSociales entity = entityService.get(id);
        if (entity != null) {
            entity.setSuspendida(false);
            entityService.save(entity);
        }
        return "redirect:/";
    }

    @RequestMapping("obrassociales/delete/{id}")
    public String delete(@PathVariable Integer id, Model model, Pageable pageable) {
        try {
            ObrasSociales entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/obrassociales";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<ObrasSociales> centroPage = entityService.findAll(pageable);
            PageWrapper<ObrasSociales> page = new PageWrapper<ObrasSociales>(centroPage, "/obrassociales");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new ObrasSociales());
            return "../obrassociales/index";
        }
    }
}
