package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Pacientes;
import ar.com.GestionTurnos.entities.TiposDoc;
import ar.com.GestionTurnos.entities.Turnos;
import ar.com.GestionTurnos.entities.TurnosEstados;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.services.IDiasService;
import ar.com.GestionTurnos.services.IObrasSocialesService;
import ar.com.GestionTurnos.services.IPacientesService;
import ar.com.GestionTurnos.services.IProfesionalesService;
import ar.com.GestionTurnos.services.ITiposDocService;
import ar.com.GestionTurnos.services.ITurnosEstadosService;
import ar.com.GestionTurnos.services.ITurnosGeneralesService;
import ar.com.GestionTurnos.services.ITurnosProfesionalesService;
import ar.com.GestionTurnos.services.ITurnosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class TurnosController {

    @Autowired
    private ITurnosService entityService;
    @Autowired
    private ITurnosGeneralesService turnosGeneralesService;
    @Autowired
    private IPacientesService pacientesService;
    @Autowired
    private ITiposDocService tiposDocService;
    @Autowired
    private IProfesionalesService profesionalesService;
    @Autowired
    private IObrasSocialesService obrasSocialesService;
    @Autowired
    private IDiasService diasService;
    @Autowired
    private ITurnosProfesionalesService turnosProfesionalesService;
    @Autowired
    private ITurnosEstadosService turnosEstadosService;

    @RequestMapping(value = "/turnos", method = RequestMethod.GET)
    public String calendario(Model model,
                             @RequestParam(value = "fecha", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                             @RequestParam(value = "profesionalId", required = false) Integer profesionalId) {

        LocalDate fechaSeleccionada = fecha != null ? fecha : LocalDate.now();

        List<Map<String, String>> eventos = new ArrayList<>();
        for (Turnos t : entityService.getAll()) {
            if (t.getFecha() == null) {
                continue;
            }
            if (Boolean.TRUE.equals(t.getAnulado())) {
                continue;
            }
            Map<String, String> ev = new HashMap<>();
            ev.put("fecha", t.getFecha().toString());
            ev.put("id", t.getId() != null ? t.getId().toString() : "");
            ev.put("profesionalId", t.getProfesional() != null && t.getProfesional().getId() != null
                    ? t.getProfesional().getId().toString() : "");
            String paciente = t.getPaciente() != null
                    ? t.getPaciente().getApellido() + ", " + t.getPaciente().getNombre()
                    : "";
            String medico = t.getProfesional() != null
                    ? t.getProfesional().getApellido() + ", " + t.getProfesional().getNombre()
                    : "";
            String horario = t.getTurnoGeneral() != null && t.getTurnoGeneral().getHorario() != null
                    ? t.getTurnoGeneral().getHorario().getDescrip()
                    : "";
            ev.put("titulo", (horario + " " + paciente).trim());
            ev.put("medico", medico);
            ev.put("estado", t.getEstado() != null && t.getEstado().getDescrip() != null
                    ? t.getEstado().getDescrip() : "");
            eventos.add(ev);
        }

        model.addAttribute("fechaSeleccionada", fechaSeleccionada.toString());
        model.addAttribute("profesionalSeleccionadoId", profesionalId);
        model.addAttribute("profesionales", profesionalesService.getAll());
        model.addAttribute("eventos", eventos);
        return "../turnos/index";
    }

    @RequestMapping(value = "/turnos/nuevo", method = RequestMethod.GET)
    public String nuevo(Model model,
                        @RequestParam(value = "fecha", required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate fechaSeleccionada = fecha != null ? fecha : LocalDate.now();

        Turnos entity = new Turnos();
        entity.setFecha(fechaSeleccionada);
        entity.setEstado(buscarEstado("Pendiente"));
        entity.setAnulado(false);

        cargarFormulario(model, entity, fechaSeleccionada);
        return "../turnos/nuevo";
    }

    @RequestMapping(value = "/turnos/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") String id,
                       @RequestParam(value = "fecha", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                       Model model) {
        Turnos entity = entityService.get(id);
        if (entity == null) {
            return "redirect:/turnos";
        }
        LocalDate fechaSeleccionada = fecha != null
                ? fecha
                : (entity.getFecha() != null ? entity.getFecha() : LocalDate.now());
        if (fecha != null) {
            entity.setFecha(fecha);
        }
        cargarFormulario(model, entity, fechaSeleccionada);
        return "../turnos/nuevo";
    }

    @RequestMapping(value = "/turnos/pacientes/buscar", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> buscarPacientes(
            @RequestParam(value = "apellido", required = false, defaultValue = "") String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return pacientesService.findByApellido(apellido.trim()).stream()
                .map(p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", p.getId());
                    item.put("apellido", p.getApellido());
                    item.put("nombre", p.getNombre());
                    item.put("dni", p.getDni());
                    item.put("tipoDoc", p.getTipoDoc() != null ? p.getTipoDoc().getCodigo() : "");
                    return item;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/turnos/pacientes", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crearPaciente(
            @RequestParam(value = "tipoDoc", required = false) Integer tipoDocId,
            @RequestParam(value = "dni", required = false) String dni,
            @RequestParam(value = "apellido", required = false) String apellido,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "telefono", required = false) String telefono,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "observacion", required = false) String observacion) {

        String errores = "";
        if (tipoDocId == null) {
            errores += "Tipo de documento incorrecto ";
        }
        if (dni == null || dni.trim().isEmpty()) {
            errores += "DNI incorrecto ";
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            errores += "Apellido incorrecto ";
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            errores += "Nombre incorrecto ";
        }
        TiposDoc tipoDoc = tipoDocId != null ? tiposDocService.get(tipoDocId) : null;
        if (tipoDocId != null && tipoDoc == null) {
            errores += "Tipo de documento incorrecto ";
        }
        if (!errores.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", errores.trim());
            return ResponseEntity.badRequest().body(error);
        }

        Pacientes paciente = new Pacientes();
        paciente.setTipoDoc(tipoDoc);
        paciente.setDni(dni.trim());
        paciente.setApellido(apellido.trim());
        paciente.setNombre(nombre.trim());
        paciente.setDireccion(direccion != null ? direccion.trim() : "");
        paciente.setTelefono(telefono != null ? telefono.trim() : "");
        paciente.setEmail(email != null ? email.trim() : "");
        paciente.setObservacion(observacion != null ? observacion.trim() : "");
        try {
            pacientesService.save(paciente);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "No se pudo guardar el paciente");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> item = new HashMap<>();
        item.put("id", paciente.getId());
        item.put("apellido", paciente.getApellido());
        item.put("nombre", paciente.getNombre());
        item.put("dni", paciente.getDni());
        item.put("tipoDoc", paciente.getTipoDoc() != null ? paciente.getTipoDoc().getCodigo() : "");
        return ResponseEntity.ok(item);
    }

    @RequestMapping(value = "/turnos/horarios", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> horariosProfesional(
            @RequestParam("profesionalId") Integer profesionalId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(value = "turnoId", required = false) String turnoId) {

        Dias diaSemana = resolverDiaSemana(fecha.getDayOfWeek());
        Set<Integer> ocupados = entityService.findByFechaAndProfesionalId(fecha, profesionalId).stream()
                .filter(t -> t.getTurnoGeneral() != null)
                .filter(t -> turnoId == null || turnoId.isEmpty() || t.getId() == null || !t.getId().equals(turnoId))
                .map(t -> t.getTurnoGeneral().getId())
                .collect(Collectors.toSet());

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (var tp : turnosProfesionalesService.findByProfesionalId(profesionalId)) {
            TurnosGenerales tg = tp.getTurnoGeneral();
            if (tg == null || tg.getDia() == null || tg.getHorario() == null) {
                continue;
            }
            if (diaSemana != null && !diaSemana.getId().equals(tg.getDia().getId())) {
                continue;
            }
            boolean ocupado = ocupados.contains(tg.getId());
            Map<String, Object> item = new HashMap<>();
            item.put("turnoGeneralId", tg.getId());
            item.put("horario", tg.getHorario().getDescrip());
            item.put("dia", tg.getDia().getDescrip());
            item.put("ocupado", ocupado);
            item.put("estado", ocupado ? "Ocupado" : "Disponible");
            resultado.add(item);
        }

        resultado.sort((a, b) -> String.valueOf(a.get("horario")).compareToIgnoreCase(String.valueOf(b.get("horario"))));
        return resultado;
    }

    @RequestMapping(value = "/turnos", method = RequestMethod.POST)
    public String save(@Validated Turnos entity) {
        entity.setFechaHora(LocalDateTime.now());
        if (entity.getAnulado() == null) {
            entity.setAnulado(false);
        }
        if (entity.getEstado() != null && entity.getEstado().getId() == null) {
            entity.setEstado(null);
        }
        if (entity.getReturno() != null && entity.getReturno().getId() == null) {
            entity.setReturno(null);
        }
        if (entity.getObraSocial() != null && entity.getObraSocial().getId() == null) {
            entity.setObraSocial(null);
        }

        entityService.save(entity);

        LocalDate fechaRedirect = entity.getFecha() != null ? entity.getFecha() : LocalDate.now();
        return "redirect:/turnos?fecha=" + fechaRedirect;
    }

    @RequestMapping(value = "/turnos/anular/{id}", method = RequestMethod.POST)
    public String anular(@PathVariable("id") String id) {
        Turnos entity = entityService.get(id);
        if (entity == null) {
            return "redirect:/turnos";
        }
        entity.setAnulado(true);
        TurnosEstados estadoAnulado = buscarEstado("Anulado");
        if (estadoAnulado != null) {
            entity.setEstado(estadoAnulado);
        }
        entityService.save(entity);

        LocalDate fechaRedirect = entity.getFecha() != null ? entity.getFecha() : LocalDate.now();
        return "redirect:/turnos?fecha=" + fechaRedirect;
    }

    @RequestMapping(value = "/turnos/estados", method = RequestMethod.GET)
    public String estados(Model model,
                          @RequestParam(value = "desde", required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                          @RequestParam(value = "hasta", required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        cargarEstados(model, desde, hasta, desde != null && hasta != null, null, null, null);
        return "../turnos/estados";
    }

    @RequestMapping(value = "/turnos/estados", method = RequestMethod.POST)
    public String guardarEstados(Model model,
                                 @RequestParam(value = "desde", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                 @RequestParam(value = "hasta", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                 @RequestParam(value = "turnoIds", required = false) List<String> turnoIds,
                                 @RequestParam(value = "estadoId", required = false) Long estadoId) {
        if (turnoIds == null || turnoIds.isEmpty()) {
            cargarEstados(model, desde, hasta, true, "Seleccione al menos un turno", estadoId, turnoIds);
            return "../turnos/estados";
        }
        TurnosEstados estado = estadoId != null ? turnosEstadosService.get(estadoId) : null;
        if (estado == null) {
            cargarEstados(model, desde, hasta, true, "Seleccione un estado", estadoId, turnoIds);
            return "../turnos/estados";
        }
        for (String id : turnoIds) {
            Turnos turno = entityService.get(id);
            if (turno != null) {
                turno.setEstado(estado);
                entityService.save(turno);
            }
        }
        LocalDate desdeValor = desde != null ? desde : LocalDate.now();
        LocalDate hastaValor = hasta != null ? hasta : LocalDate.now();
        return "redirect:/turnos/estados?desde=" + desdeValor + "&hasta=" + hastaValor;
    }

    private void cargarEstados(Model model, LocalDate desde, LocalDate hasta, boolean buscar,
                               String message, Long estadoId, List<String> turnoIds) {
        LocalDate desdeValor = desde != null ? desde : LocalDate.now();
        LocalDate hastaValor = hasta != null ? hasta : LocalDate.now();
        if (buscar && desdeValor.isAfter(hastaValor)) {
            buscar = false;
            message = "La fecha desde no puede ser posterior a la fecha hasta";
        }
        model.addAttribute("desde", desdeValor);
        model.addAttribute("hasta", hastaValor);
        model.addAttribute("busqueda", buscar);
        model.addAttribute("message", message);
        model.addAttribute("estadoId", estadoId);
        model.addAttribute("turnoIds", turnoIds != null ? turnoIds : Collections.emptyList());
        model.addAttribute("turnosestados", turnosEstadosService.getAllActivo());
        model.addAttribute("turnos", buscar ? entityService.findByFechaBetween(desdeValor, hastaValor) : Collections.emptyList());
    }

    private void cargarFormulario(Model model, Turnos entity, LocalDate fechaSeleccionada) {
        model.addAttribute("entity", entity);
        model.addAttribute("fechaSeleccionada", fechaSeleccionada.toString());
        model.addAttribute("turnosGenerales", turnosGeneralesPorDiaSemana(fechaSeleccionada));
        model.addAttribute("profesionales", profesionalesService.getAll());
        model.addAttribute("obrassociales", obrasSocialesService.getAll());


        model.addAttribute("turnosExistentes", entityService.getAll());
        model.addAttribute("tiposdoc", tiposDocService.getAll());
        if (entity.getId()==null) {
            List<TurnosEstados> l=turnosEstadosService.getAllActivo();
            model.addAttribute("turnosestados", l);
        }
        else {
            model.addAttribute("turnosestados", turnosEstadosService.getAllActivo());

        }
        if (entity.getPaciente() != null && entity.getPaciente().getId() != null) {
            Pacientes paciente = pacientesService.get(entity.getPaciente().getId());
            model.addAttribute("pacienteSeleccionado", paciente);
        } else {
            model.addAttribute("pacienteSeleccionado", null);
        }
    }

    private List<TurnosGenerales> turnosGeneralesPorDiaSemana(LocalDate fecha) {
        Dias dia = resolverDiaSemana(fecha.getDayOfWeek());
        if (dia == null) {
            return Collections.emptyList();
        }
        return turnosGeneralesService.findByDia(dia);
    }

    private Dias resolverDiaSemana(DayOfWeek dayOfWeek) {
        Set<String> aliases = aliasesDia(dayOfWeek);
        for (Dias dia : diasService.getAll()) {
            String abr = normalizar(dia.getAbreviatura());
            String des = normalizar(dia.getDescrip());
            if (aliases.contains(abr) || aliases.contains(des)) {
                return dia;
            }
        }
        return null;
    }

    private Set<String> aliasesDia(DayOfWeek dayOfWeek) {
        Set<String> aliases = new HashSet<>();
        switch (dayOfWeek) {
            case MONDAY -> {
                aliases.add("lunes");
                aliases.add("lun");
                aliases.add("lu");
            }
            case TUESDAY -> {
                aliases.add("martes");
                aliases.add("mar");
                aliases.add("ma");
            }
            case WEDNESDAY -> {
                aliases.add("miercoles");
                aliases.add("mie");
                aliases.add("mi");
                aliases.add("x");
            }
            case THURSDAY -> {
                aliases.add("jueves");
                aliases.add("jue");
                aliases.add("ju");
            }
            case FRIDAY -> {
                aliases.add("viernes");
                aliases.add("vie");
                aliases.add("vi");
            }
            case SATURDAY -> {
                aliases.add("sabado");
                aliases.add("sab");
                aliases.add("sa");
            }
            case SUNDAY -> {
                aliases.add("domingo");
                aliases.add("dom");
                aliases.add("do");
            }
        }
        return aliases;
    }

    private TurnosEstados buscarEstado(String descrip) {
        if (descrip == null) {
            return null;
        }
        for (TurnosEstados estado : turnosEstadosService.getAllActivo()) {
            if (descrip.equalsIgnoreCase(estado.getDescrip())) {
                return estado;
            }
        }
        return null;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.trim().toLowerCase();
    }
}
