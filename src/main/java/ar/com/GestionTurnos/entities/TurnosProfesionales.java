package ar.com.GestionTurnos.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "turnosProfesionales")
public class TurnosProfesionales implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @JoinColumn(name = "id_profesional", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Profesionales profesional;

    @JoinColumn(name = "id_turnoGeneral", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TurnosGenerales turnoGeneral;

    @Column(name = "activo", columnDefinition = "BIT")
    private Boolean activo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Profesionales getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesionales profesional) {
        this.profesional = profesional;
    }

    public TurnosGenerales getTurnoGeneral() {
        return turnoGeneral;
    }

    public void setTurnoGeneral(TurnosGenerales turnoGeneral) {
        this.turnoGeneral = turnoGeneral;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
