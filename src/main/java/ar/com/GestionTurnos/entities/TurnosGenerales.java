package ar.com.GestionTurnos.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="turnosGenerales")
public class TurnosGenerales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "estado", columnDefinition = "BIT")
    private Boolean estado;

    @JoinColumn(name= "id_dias", referencedColumnName = "id",nullable=false)
    @ManyToOne
    private Dias dia;

    @JoinColumn(name= "id_horarios", referencedColumnName = "id",nullable=false)
    @ManyToOne
    private Horarios horario;

    @Transient
    private boolean sel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Dias getDia() {
        return dia;
    }

    public void setDia(Dias dia) {
        this.dia = dia;
    }

    public Horarios getHorario() {
        return horario;
    }

    public void setHorario(Horarios horario) {
        this.horario = horario;
    }

    public boolean isSel() {
        return sel;
    }

    public void setSel(boolean sel) {
        this.sel = sel;
    }
}
