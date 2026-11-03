package ar.com.GestionTurnos.controller;


import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.services.IDiasService;
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
public class DiasController {


    @Autowired
    IDiasService entityService;

    @RequestMapping(value = "/dias", method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<Dias> centroPage = entityService.findAll(pageable);
        PageWrapper<Dias> page = new PageWrapper<Dias>(centroPage, "/dias");
        model.addAttribute("entities", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("entity", new Dias());
        return "../dias/index";
    }

    @RequestMapping("dias/refresh")
    public String refresh() {
        return "redirect:/dias";
    }

    @RequestMapping(value = "dias/search", method = RequestMethod.POST)
    public String search(Model model, Dias entity) {
        if (entity.getAbreviatura().equals("")) {
            return refresh();
        }
        model.addAttribute("entities", entityService.findByAbreviatura(entity.getAbreviatura()));
        model.addAttribute("entity", new Dias());
        model.addAttribute("page", null);
        return "../dias/index";
    }

    @RequestMapping("dias/create/{id}")
    public String create(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", new Dias());
        return "../dias/edit";
    }

    @RequestMapping("dias/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.get(id));
        return "../dias/edit";
    }

    @RequestMapping(value = "dias", method = RequestMethod.POST)
    public String save(Model model, @Validated Dias entity) {
        if (entity.getDescrip().equals("")) {
            model.addAttribute("message", "Descripción Incorrecta");
            model.addAttribute("entity", entity);
            return "../dias/edit";
        }

        entityService.save(entity);
        return "redirect:/dias";
    }

    @RequestMapping("dias/delete/{id}")
    public String delete(@PathVariable Integer id, Model model, Pageable pageable) {
        try {
            Dias entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/dias";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<Dias> centroPage = entityService.findAll(pageable);
            PageWrapper<Dias> page = new PageWrapper<Dias>(centroPage, "/dias");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new Dias());
            return "../dias/index";
        }
    }
}
