package ar.com.GestionTurnos.entities;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
public class Turnos implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 40)
    private String id;

    @JoinColumn(name = "id_turnoGeneral", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TurnosGenerales turnoGeneral;

    @JoinColumn(name = "id_paciente", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pacientes paciente;

    @JoinColumn(name = "id_profesional", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Profesionales profesional;

    private String descrip;

    @JoinColumn(name = "id_turnoEstado", referencedColumnName = "id")
    @ManyToOne
    private TurnosEstados estado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaHora;

    @JoinColumn(name = "id_obraSocial", referencedColumnName = "id")
    @ManyToOne
    private ObrasSociales obraSocial;

    @JoinColumn(name = "id_returno", referencedColumnName = "id")
    @ManyToOne
    private Turnos returno;

    @Column(name = "anulado", columnDefinition = "BIT")
    private Boolean anulado;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TurnosGenerales getTurnoGeneral() {
        return turnoGeneral;
    }

    public void setTurnoGeneral(TurnosGenerales turnoGeneral) {
        this.turnoGeneral = turnoGeneral;
    }

    public Pacientes getPaciente() {
        return paciente;
    }

    public void setPaciente(Pacientes paciente) {
        this.paciente = paciente;
    }

    public Profesionales getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesionales profesional) {
        this.profesional = profesional;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public TurnosEstados getEstado() {
        return estado;
    }

    public void setEstado(TurnosEstados estado) {
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public ObrasSociales getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(ObrasSociales obraSocial) {
        this.obraSocial = obraSocial;
    }

    public Turnos getReturno() {
        return returno;
    }

    public void setReturno(Turnos returno) {
        this.returno = returno;
    }

    public Boolean getAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
