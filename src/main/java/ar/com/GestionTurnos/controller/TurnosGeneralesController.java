package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.models.DiasHorarios;
import ar.com.GestionTurnos.services.IDiasService;
import ar.com.GestionTurnos.services.IHorariosService;
import ar.com.GestionTurnos.services.ITurnosGeneralesService;
import ar.com.GestionTurnos.utiles.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TurnosGeneralesController {


    @Autowired
    ITurnosGeneralesService entityService;
    @Autowired
    IDiasService diaService;
    @Autowired
    IHorariosService horarioService;

    @RequestMapping(value = "/turnosgenerales", method = RequestMethod.GET)
    public String list(Model model, @RequestParam(value = "diaId", required = false) Integer diaId) {
        DiasHorarios diasHorarios = new DiasHorarios();
        List<Dias> dias = diaService.getAll().stream()
                .sorted(Comparator.comparing(Dias::getAbreviatura, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
        diasHorarios.setDias(dias);

        Integer diaSeleccionadoId = diaId;
        if (diaSeleccionadoId == null && !dias.isEmpty()) {
            diaSeleccionadoId = dias.get(0).getId();
        }

        List<Horarios> horarios = horarioService.getAll();
        if (diaSeleccionadoId != null) {
            Dias dia = diaService.get(diaSeleccionadoId);
            for (Horarios horario : horarios) {
                Horarios horarioPersistido = horarioService.get(horario.getId());
                boolean definido = entityService.findByDiaAndHorario(dia, horarioPersistido).isPresent();
                horario.setSel(definido);
            }
        }
        diasHorarios.setHorarios(horarios);

        model.addAttribute("diashorarios", diasHorarios);
        model.addAttribute("diaSeleccionadoId", diaSeleccionadoId);
        return "../turnosgenerales/index";
    }

    @RequestMapping(value = "/turnosgenerales/save", method = RequestMethod.POST)
    public String saveSeleccion(DiasHorarios diasHorarios, @RequestParam("diaId") Integer diaId) {
        Dias dia = diaService.get(diaId);

        if (diasHorarios.getHorarios() != null) {
            for (Horarios horario : diasHorarios.getHorarios()) {
                if (horario.isSel()) {
                    Horarios horarioPersistido = horarioService.get(horario.getId());
                    if (entityService.findByDiaAndHorario(dia, horarioPersistido).isEmpty()) {
                        TurnosGenerales turno = new TurnosGenerales();
                        turno.setDia(dia);
                        turno.setHorario(horarioPersistido);
                        turno.setEstado(true);
                        entityService.save(turno);
                    }
                }
            }
        }

        return "redirect:/turnosgenerales?diaId=" + diaId;
    }

    @RequestMapping("/turnosgenerales/refresh")
    public String refresh() {
        return "redirect:/turnosgenerales";
    }

    @RequestMapping(value = "/turnosgenerales/search", method = {RequestMethod.POST, RequestMethod.GET})
    public String search(@RequestParam(value = "estado", required = false) String estadoStr,
                         @PageableDefault(size = 10) Pageable pageable,
                         Model model) {

        List<TurnosGenerales> turnosFiltrados;

        if (estadoStr == null || estadoStr.trim().isEmpty()) {
            return "redirect:/turnosgenerales";
        }

        // Convertimos la cadena a Boolean
        Boolean estado = Boolean.parseBoolean(estadoStr);

        // Consulta al DAO
        turnosFiltrados = entityService.findByEstado(estado);

        model.addAttribute("entities", turnosFiltrados);
        model.addAttribute("page", null);
        model.addAttribute("entity", new TurnosGenerales());

        return "../turnosgenerales/index1";
    }



    @RequestMapping("/turnosgenerales/create/0")
    public String create(Model model) {
        model.addAttribute("entity", new TurnosGenerales());

        DiasHorarios diasHorarios = new DiasHorarios();
        diasHorarios.setDias(diaService.getAll());
        diasHorarios.setHorarios(horarioService.getAll());

        model.addAttribute("diashorarios", diasHorarios);
        return "../turnosgenerales/edit";
    }

    @RequestMapping("/turnosgenerales/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.get(id));

        DiasHorarios diasHorarios = new DiasHorarios();
        diasHorarios.setDias(diaService.getAll());
        diasHorarios.setHorarios(horarioService.getAll());

        model.addAttribute("diashorarios", diasHorarios);
        return "../turnosgenerales/edit";
    }

    @RequestMapping(value = "turnosgenerales", method = RequestMethod.POST)
    public String save(Model model, @Validated TurnosGenerales entity) {
        String errores = "";
        if (!errores.equals("")) {
            model.addAttribute("message", errores);
            model.addAttribute("entity", entity);
            DiasHorarios diasHorarios = new DiasHorarios();
            diasHorarios.setDias(diaService.getAll());
            diasHorarios.setHorarios(horarioService.getAll());
            model.addAttribute("diashorarios", diasHorarios);
            return "../turnosgenerales/edit";
        }

        entityService.save(entity);
        return "redirect:/turnosgenerales";
    }

    @RequestMapping("turnosgenerales/delete/{id}")
    public String delete(@PathVariable Integer id, Model model, Pageable pageable) {
        try {
            TurnosGenerales entity = entityService.get(id);
            entityService.delete(entity);
            return "redirect:/turnosgenerales";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().toString());
            Page<TurnosGenerales> centroPage = entityService.findAll(pageable);
            PageWrapper<TurnosGenerales> page = new PageWrapper<TurnosGenerales>(centroPage, "/turnosgenerales");
            model.addAttribute("entities", page.getContent());
            model.addAttribute("page", page);
            model.addAttribute("entity", new TurnosGenerales());
            return "../turnosgenerales/index1";
        }
    }
}
