package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.ObrasSociales;
import ar.com.GestionTurnos.entities.Turnos;
import ar.com.GestionTurnos.entities.TurnosGenerales;
import ar.com.GestionTurnos.models.InformeTurnoFila;
import ar.com.GestionTurnos.services.IInformeTurnosService;
import ar.com.GestionTurnos.services.ITurnosService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InformeTurnosService implements IInformeTurnosService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private ITurnosService turnosService;

    private volatile JasperReport reporte;

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdf(LocalDate desde, LocalDate hasta) {
        List<InformeTurnoFila> filas = new ArrayList<>();
        for (Turnos turno : turnosService.findByFechaBetween(desde, hasta)) {
            if (Boolean.TRUE.equals(turno.getAnulado())) {
                continue;
            }
            filas.add(fila(turno));
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("DESDE", desde.format(FECHA));
        parametros.put("HASTA", hasta.format(FECHA));
        try {
            return JasperExportManager.exportReportToPdf(
                    JasperFillManager.fillReport(reporte(), parametros, new JRBeanCollectionDataSource(filas)));
        } catch (JRException e) {
            throw new IllegalStateException("No se pudo generar el informe de turnos.", e);
        }
    }

    private JasperReport reporte() throws JRException {
        JasperReport actual = reporte;
        if (actual == null) {
            synchronized (this) {
                if (reporte == null) {
                    InputStream plantilla = getClass().getResourceAsStream("/reportes/turnos.jrxml");
                    if (plantilla == null) {
                        throw new IllegalStateException("No se encontró la plantilla del informe.");
                    }
                    reporte = JasperCompileManager.compileReport(plantilla);
                }
                actual = reporte;
            }
        }
        return actual;
    }

    private InformeTurnoFila fila(Turnos turno) {
        InformeTurnoFila fila = new InformeTurnoFila();
        fila.setPaciente(persona(turno.getPaciente() == null ? null : turno.getPaciente().getApellido(),
                turno.getPaciente() == null ? null : turno.getPaciente().getNombre()));
        fila.setObraSocial(obra(turno.getObraSocial()));
        fila.setFecha(turno.getFecha() == null ? "" : turno.getFecha().format(FECHA));
        fila.setHora(hora(turno.getTurnoGeneral()));
        fila.setProfesional(persona(turno.getProfesional() == null ? null : turno.getProfesional().getApellido(),
                turno.getProfesional() == null ? null : turno.getProfesional().getNombre()));
        return fila;
    }

    private String persona(String apellido, String nombre) {
        String limpioApellido = apellido == null ? "" : apellido.trim();
        String limpioNombre = nombre == null ? "" : nombre.trim();
        return (limpioApellido + " " + limpioNombre).trim();
    }

    private String obra(ObrasSociales obra) {
        if (obra == null) {
            return "";
        }
        String codigo = obra.getCodigo() == null ? "" : obra.getCodigo().trim();
        String nombre = obra.getNombre() == null ? "" : obra.getNombre().trim();
        if (codigo.isEmpty()) {
            return nombre;
        }
        if (nombre.isEmpty()) {
            return codigo;
        }
        return codigo + " " + nombre;
    }

    private String hora(TurnosGenerales turnoGeneral) {
        if (turnoGeneral == null || turnoGeneral.getHorario() == null) {
            return "";
        }
        Horarios horario = turnoGeneral.getHorario();
        String descrip = horario.getDescrip() == null ? "" : horario.getDescrip().trim();
        String meridiano = horario.getMeridiano() == null ? "" : horario.getMeridiano().trim();
        return (descrip + " " + meridiano).trim();
    }
}
