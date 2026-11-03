package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.entities.TurnosProfesionales;
import ar.com.GestionTurnos.models.ProfesionalesTurnos;
import ar.com.GestionTurnos.services.IProfesionalesService;
import ar.com.GestionTurnos.services.ITurnosGeneralesService;
import ar.com.GestionTurnos.services.ITurnosProfesionalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TurnosProfesionalesController {

    @Autowired
    private ITurnosProfesionalesService entityService;
    @Autowired
    private IProfesionalesService profesionalService;
    @Autowired
    private ITurnosGeneralesService turnosGeneralesService;

    @RequestMapping(value = "/turnosprofesionales", method = RequestMethod.GET)
    public String list(Model model, @RequestParam(value = "profesionalId", required = false) Integer profesionalId) {
        ProfesionalesTurnos profesionalesTurnos = new ProfesionalesTurnos();

        List<Profesionales> profesionales = profesionalService.getAll().stream()
                .sorted(Comparator.comparing(Profesionales::getApellido, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Profesionales::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
        profesionalesTurnos.setProfesionales(profesionales);

        Integer profesionalSeleccionadoId = profesionalId;
        if (profesionalSeleccionadoId == null && !profesionales.isEmpty()) {
            profesionalSeleccionadoId = profesionales.get(0).getId();
        }

        List<TurnosGenerales> turnosGenerales = turnosGeneralesService.getAll();
        if (profesionalSeleccionadoId != null) {
            Profesionales profesional = profesionalService.get(profesionalSeleccionadoId);
            for (TurnosGenerales turno : turnosGenerales) {
                TurnosGenerales turnoPersistido = turnosGeneralesService.get(turno.getId());
                boolean definido = entityService.findByProfesionalAndTurnoGeneral(profesional, turnoPersistido).isPresent();
                turno.setSel(definido);
            }
        }
        profesionalesTurnos.setTurnosGenerales(turnosGenerales);

        model.addAttribute("profesionalesturnos", profesionalesTurnos);
        model.addAttribute("profesionalSeleccionadoId", profesionalSeleccionadoId);
        return "../turnosprofesionales/index";
    }

    @RequestMapping(value = "/turnosprofesionales/save", method = RequestMethod.POST)
    public String saveSeleccion(ProfesionalesTurnos profesionalesTurnos,
                                @RequestParam("profesionalId") Integer profesionalId) {
        Profesionales profesional = profesionalService.get(profesionalId);

        if (profesionalesTurnos.getTurnosGenerales() != null) {
            for (TurnosGenerales turno : profesionalesTurnos.getTurnosGenerales()) {
                if (turno.isSel()) {
                    TurnosGenerales turnoPersistido = turnosGeneralesService.get(turno.getId());
                    if (entityService.findByProfesionalAndTurnoGeneral(profesional, turnoPersistido).isEmpty()) {
                        TurnosProfesionales entity = new TurnosProfesionales();
                        entity.setId(profesional.getId() + "_" + turnoPersistido.getId());
                        entity.setProfesional(profesional);
                        entity.setTurnoGeneral(turnoPersistido);
                        entity.setActivo(true);
                        entityService.save(entity);
                    }
                }
            }
        }

        return "redirect:/turnosprofesionales?profesionalId=" + profesionalId;
    }
}
