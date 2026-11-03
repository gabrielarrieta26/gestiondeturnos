package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.models.DiasHorarios;
import ar.com.GestionTurnos.services.IDiasService;
import ar.com.GestionTurnos.services.IHorariosService;
import ar.com.GestionTurnos.services.ITurnosGeneralesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/diashorarios")
public class DiasHorariosController {

    @Autowired
    private IDiasService diaService;

    @Autowired
    private IHorariosService horarioService;

    @Autowired
    private ITurnosGeneralesService turnosGeneralesService;

    @GetMapping({""})
    public String index(Model model) {
        DiasHorarios diasHorarios = new DiasHorarios();
        diasHorarios.setDias(diaService.getAll());
        diasHorarios.setHorarios(horarioService.getAll());

        model.addAttribute("diashorarios", diasHorarios);
        model.addAttribute("diaSeleccionado", new Dias());

        return "../diashorarios/index";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(DiasHorarios diasHorarios, @RequestParam("diaId") Integer diaId) {
        Dias dia = diaService.get(diaId);

        if (diasHorarios.getHorarios() != null) {
            for (Horarios horario : diasHorarios.getHorarios()) {
                if (horario.isSel()) {
                    TurnosGenerales turno = new TurnosGenerales();
                    turno.setDia(dia);
                    turno.setHorario(horario);
                    turno.setEstado(true);

                    turnosGeneralesService.save(turno);
                }
            }
        }

        return "redirect:/turnosgenerales";
    }
}