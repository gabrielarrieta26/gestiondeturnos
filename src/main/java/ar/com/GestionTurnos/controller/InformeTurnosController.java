package ar.com.GestionTurnos.controller;

import ar.com.GestionTurnos.services.IInformeTurnosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InformeTurnosController {

    private static final String VISTA = "../turnos/informe";

    @Autowired
    private IInformeTurnosService informeTurnosService;

    @GetMapping("/turnos/informe")
    public String formulario(@RequestParam(required = false) String desde,
                             @RequestParam(required = false) String hasta,
                             Model model) {
        model.addAttribute("desde", desde == null ? "" : desde);
        model.addAttribute("hasta", hasta == null ? "" : hasta);
        model.addAttribute("mostrarPdf", periodo(desde, hasta, new ArrayList<>()) != null);
        return VISTA;
    }

    @GetMapping("/turnos/informe/pdf")
    public ResponseEntity<byte[]> pdf(@RequestParam(required = false) String desde,
                                      @RequestParam(required = false) String hasta) {
        LocalDate[] periodo = periodo(desde, hasta, new ArrayList<>());
        if (periodo == null) {
            return ResponseEntity.badRequest().build();
        }
        return pdf(periodo[0], periodo[1]);
    }

    @PostMapping("/turnos/informe")
    public Object generar(@RequestParam(required = false) String desde,
                          @RequestParam(required = false) String hasta,
                          Model model) {
        List<String> errores = new ArrayList<>();
        LocalDate[] periodo = periodo(desde, hasta, errores);
        model.addAttribute("desde", desde == null ? "" : desde);
        model.addAttribute("hasta", hasta == null ? "" : hasta);
        model.addAttribute("mostrarPdf", false);
        if (periodo == null) {
            model.addAttribute("errores", errores);
            return VISTA;
        }
        return "redirect:/turnos/informe?desde=" + periodo[0] + "&hasta=" + periodo[1];
    }

    private ResponseEntity<byte[]> pdf(LocalDate desde, LocalDate hasta) {
        byte[] cuerpo = informeTurnosService.generarPdf(desde, hasta);
        String nombre = "turnos-" + desde + "-al-" + hasta + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(cuerpo);
    }

    private LocalDate[] periodo(String desde, String hasta, List<String> errores) {
        LocalDate desdeFecha = fecha(desde, "Desde", errores);
        LocalDate hastaFecha = fecha(hasta, "Hasta", errores);
        if (desdeFecha != null && hastaFecha != null && hastaFecha.isBefore(desdeFecha)) {
            errores.add("La fecha hasta no puede ser anterior a desde.");
            return null;
        }
        if (!errores.isEmpty()) {
            return null;
        }
        return new LocalDate[] {desdeFecha, hastaFecha};
    }

    private LocalDate fecha(String valor, String etiqueta, List<String> errores) {
        if (valor == null || valor.isBlank()) {
            errores.add("La fecha " + etiqueta.toLowerCase() + " es obligatoria.");
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException e) {
            errores.add("La fecha " + etiqueta.toLowerCase() + " no es válida.");
            return null;
        }
    }
}
