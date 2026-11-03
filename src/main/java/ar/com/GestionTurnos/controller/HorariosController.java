package ar.com.GestionTurnos.controller;


import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.services.IHorariosService;
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
public class HorariosController {


    @Autowired
    IHorariosService entityService;

    @RequestMapping(value = "/horarios", method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<Horarios> centroPage = entityService.findAll(pageable);
        PageWrapper<Horarios> page = new PageWrapper<Horarios>(centroPage, "/horarios");
        model.addAttribute("entities", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("entity", new Horarios());
        return "../horarios/index";
    }

    @RequestMapping("horarios/refresh")
    public String refresh() {
        return "redirect:/horarios";
    }

    @RequestMapping(value = "horarios/search", method = RequestMethod.POST)
    public String search(Model model, Horarios entity) {
        if (entity.getDescrip().equals("")) {
            return refresh();
        }
        model.addAttribute("entities", entityService.findByDescrip(entity.getDescrip()));
        model.addAttribute("entity", new Horarios());
        model.addAttribute("page", null);
        return "../horarios/index";
    }

    @RequestMapping("horarios/create/{id}")
    public String create(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", new Horarios());
        return "../horarios/edit";
    }

    @RequestMapping("horarios/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.get(id));
        return "../horarios/edit";
    }

    @RequestMapping(value = "horarios", method = RequestMethod.POST)
    public String save(Model model, @Validated Horarios entity) {
        if (entity.getDescrip().equals("")) {
            model.addAttribute("message", "Descripción Incorrecta");
            model.addAttribute("entity", entity);
            return "../horarios/edit";
        }

        entityService.save(entity);
        return "redirect:/horarios";
    }

    @RequestMapping("horarios/delete/{id}")
    public String delete(@PathVariable Integer id, Model model, Pageable pageable) {
        try {
            Horarios entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/horarios";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<Horarios> centroPage = entityService.findAll(pageable);
            PageWrapper<Horarios> page = new PageWrapper<Horarios>(centroPage, "/horarios");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new Horarios());
            return "../horarios/index";
        }
    }
}
