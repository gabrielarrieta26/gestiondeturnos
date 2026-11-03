package ar.com.GestionTurnos.servicesimp;

import ar.com.GestionTurnos.entities.Horarios;
import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.entities.Turnos;
import ar.com.GestionTurnos.entities.TurnosEstados;
import ar.com.GestionTurnos.entities.Usuarios;
import ar.com.GestionTurnos.models.CancelacionDia;
import ar.com.GestionTurnos.services.ICancelarDiaService;
import ar.com.GestionTurnos.services.ITurnosEstadosService;
import ar.com.GestionTurnos.services.ITurnosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CancelarDiaService implements ICancelarDiaService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("H:mm");

    @Autowired
    private ITurnosService turnosService;
    @Autowired
    private ITurnosEstadosService turnosEstadosService;

    @Override
    public void clasificar(CancelacionDia criterio) {
        List<Turnos> bajas = new ArrayList<>();
        int atendidos = 0;
        int yaCancelados = 0;
        int sinUsuario = 0;
        for (Turnos turno : turnosService.findByFechaBetween(criterio.getDesde(), criterio.getHasta())) {
            LocalTime hora = horaDe(turno);
            if (!entraEnHorario(hora, criterio)) {
                continue;
            }
            Usuarios usuario = usuarioDe(turno);
            boolean sinUser = usuario == null || usuario.getRol() == null;
            if (sinUser) {
                if (!(criterio.isTodosLosRoles() && criterio.isTodosLosUsuarios())) {
                    sinUsuario++;
                    continue;
                }
            } else if (!incluye(criterio, usuario)) {
                continue;
            }
            if (esAtendido(turno)) {
                atendidos++;
                continue;
            }
            if (yaCancelado(turno)) {
                yaCancelados++;
                continue;
            }
            bajas.add(turno);
        }
        criterio.setACancelar(bajas);
        criterio.setAtendidos(atendidos);
        criterio.setYaCancelados(yaCancelados);
        criterio.setSinUsuario(sinUsuario);
    }

    @Override
    @Transactional
    public int confirmar(CancelacionDia criterio) {
        clasificar(criterio);
        TurnosEstados estado = buscarEstado("Cancelado");
        if (estado == null) {
            estado = buscarEstado("Anulado");
        }
        String motivo = criterio.getMotivo().trim();
        int cantidad = 0;
        for (Turnos turno : criterio.getACancelar()) {
            turno.setAnulado(true);
            turno.setMotivoCancelacion(motivo);
            if (estado != null) {
                turno.setEstado(estado);
            }
            turnosService.save(turno);
            cantidad++;
        }
        return cantidad;
    }

    private boolean incluye(CancelacionDia criterio, Usuarios usuario) {
        if (!criterio.isTodosLosRoles()
                && (criterio.getRolIds() == null || !criterio.getRolIds().contains(usuario.getRol().getId()))) {
            return false;
        }
        return criterio.isTodosLosUsuarios()
                || (criterio.getUsuarioIds() != null && criterio.getUsuarioIds().contains(usuario.getId()));
    }

    private boolean entraEnHorario(LocalTime hora, CancelacionDia criterio) {
        if (criterio.getHoraDesde() == null && criterio.getHoraHasta() == null) {
            return true;
        }
        if (hora == null) {
            return false;
        }
        if (criterio.getHoraDesde() != null && hora.isBefore(criterio.getHoraDesde())) {
            return false;
        }
        return criterio.getHoraHasta() == null || !hora.isAfter(criterio.getHoraHasta());
    }

    private Usuarios usuarioDe(Turnos turno) {
        Profesionales profesional = turno.getProfesional();
        return profesional == null ? null : profesional.getUsuario();
    }

    private LocalTime horaDe(Turnos turno) {
        if (turno.getTurnoGeneral() == null || turno.getTurnoGeneral().getHorario() == null) {
            return null;
        }
        Horarios horario = turno.getTurnoGeneral().getHorario();
        if (horario.getDescrip() == null || horario.getDescrip().isBlank()) {
            return null;
        }
        try {
            LocalTime hora = LocalTime.parse(horario.getDescrip().trim(), HORA);
            String meridiano = horario.getMeridiano() == null ? "" : horario.getMeridiano().trim();
            if ("PM".equalsIgnoreCase(meridiano) && hora.getHour() < 12) {
                hora = hora.plusHours(12);
            }
            if ("AM".equalsIgnoreCase(meridiano) && hora.getHour() == 12) {
                hora = LocalTime.MIDNIGHT;
            }
            return hora;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean esAtendido(Turnos turno) {
        return "atendido".equals(nombreEstado(turno));
    }

    private boolean yaCancelado(Turnos turno) {
        if (Boolean.TRUE.equals(turno.getAnulado())) {
            return true;
        }
        String estado = nombreEstado(turno);
        return "cancelado".equals(estado) || "anulado".equals(estado);
    }

    private String nombreEstado(Turnos turno) {
        if (turno.getEstado() == null || turno.getEstado().getDescrip() == null) {
            return "";
        }
        return turno.getEstado().getDescrip().trim().toLowerCase();
    }

    private TurnosEstados buscarEstado(String descrip) {
        List<TurnosEstados> estados = turnosEstadosService.findByDescripActivo(descrip);
        if (estados == null) {
            return null;
        }
        for (TurnosEstados estado : estados) {
            if (estado.getDescrip() != null && descrip.equalsIgnoreCase(estado.getDescrip().trim())) {
                return estado;
            }
        }
        return estados.isEmpty() ? null : estados.get(0);
    }
}
