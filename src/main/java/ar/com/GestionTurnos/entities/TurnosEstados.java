package ar.com.GestionTurnos.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "turnosestados")
public class TurnosEstados implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descrip;
    private Boolean inhabilitar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public Boolean getInhabilitar() {
        return inhabilitar;
    }

    public void setInhabilitar(Boolean inhabilitar) {
        this.inhabilitar = inhabilitar;
    }
}
