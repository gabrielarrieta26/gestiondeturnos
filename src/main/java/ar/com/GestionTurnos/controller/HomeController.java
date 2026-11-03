package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.services.IObrasSocialesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private IObrasSocialesService obrasSocialesService;

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("obrasSuspendidas", obrasSocialesService.findSuspendidas());
        return "home";
    }

}
