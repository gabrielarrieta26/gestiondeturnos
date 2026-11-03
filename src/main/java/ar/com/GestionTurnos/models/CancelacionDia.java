package ar.com.GestionTurnos.models;

import ar.com.GestionTurnos.entities.Turnos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CancelacionDia {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private String desdeTexto = "";
    private String hastaTexto = "";
    private String horaDesdeTexto = "";
    private String horaHastaTexto = "";
    private LocalDate desde;
    private LocalDate hasta;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private boolean todosLosRoles;
    private boolean todosLosUsuarios;
    private List<Integer> rolIds = new ArrayList<>();
    private List<Integer> usuarioIds = new ArrayList<>();
    private String motivo = "";
    private List<String> errores = new ArrayList<>();
    private List<Turnos> aCancelar = new ArrayList<>();
    private int atendidos;
    private int yaCancelados;
    private int sinUsuario;
    private boolean revisado;

    public boolean isAgendaCompleta() {
        return todosLosRoles && todosLosUsuarios;
    }

    public String getPeriodoTexto() {
        if (desde == null || hasta == null) {
            return "";
        }
        if (horaDesde == null && horaHasta == null) {
            return desde.format(FECHA) + " al " + hasta.format(FECHA) + ", día completo";
        }
        String desdeHora = horaDesde == null ? "00:00" : horaDesde.format(HORA);
        String hastaHora = horaHasta == null ? "23:59" : horaHasta.format(HORA);
        return desde.format(FECHA) + " " + desdeHora + " al " + hasta.format(FECHA) + " " + hastaHora;
    }

    public String getDesdeTexto() {
        return desdeTexto;
    }

    public void setDesdeTexto(String desdeTexto) {
        this.desdeTexto = desdeTexto;
    }

    public String getHastaTexto() {
        return hastaTexto;
    }

    public void setHastaTexto(String hastaTexto) {
        this.hastaTexto = hastaTexto;
    }

    public String getHoraDesdeTexto() {
        return horaDesdeTexto;
    }

    public void setHoraDesdeTexto(String horaDesdeTexto) {
        this.horaDesdeTexto = horaDesdeTexto;
    }

    public String getHoraHastaTexto() {
        return horaHastaTexto;
    }

    public void setHoraHastaTexto(String horaHastaTexto) {
        this.horaHastaTexto = horaHastaTexto;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }

    public LocalTime getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(LocalTime horaDesde) {
        this.horaDesde = horaDesde;
    }

    public LocalTime getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(LocalTime horaHasta) {
        this.horaHasta = horaHasta;
    }

    public boolean isTodosLosRoles() {
        return todosLosRoles;
    }

    public void setTodosLosRoles(boolean todosLosRoles) {
        this.todosLosRoles = todosLosRoles;
    }

    public boolean isTodosLosUsuarios() {
        return todosLosUsuarios;
    }

    public void setTodosLosUsuarios(boolean todosLosUsuarios) {
        this.todosLosUsuarios = todosLosUsuarios;
    }

    public List<Integer> getRolIds() {
        return rolIds;
    }

    public void setRolIds(List<Integer> rolIds) {
        this.rolIds = rolIds == null ? new ArrayList<>() : rolIds;
    }

    public List<Integer> getUsuarioIds() {
        return usuarioIds;
    }

    public void setUsuarioIds(List<Integer> usuarioIds) {
        this.usuarioIds = usuarioIds == null ? new ArrayList<>() : usuarioIds;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo == null ? "" : motivo;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores == null ? new ArrayList<>() : errores;
    }

    public List<Turnos> getACancelar() {
        return aCancelar;
    }

    public void setACancelar(List<Turnos> aCancelar) {
        this.aCancelar = aCancelar == null ? new ArrayList<>() : aCancelar;
    }

    public int getAtendidos() {
        return atendidos;
    }

    public void setAtendidos(int atendidos) {
        this.atendidos = atendidos;
    }

    public int getYaCancelados() {
        return yaCancelados;
    }

    public void setYaCancelados(int yaCancelados) {
        this.yaCancelados = yaCancelados;
    }

    public int getSinUsuario() {
        return sinUsuario;
    }

    public void setSinUsuario(int sinUsuario) {
        this.sinUsuario = sinUsuario;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }
}
