package ar.com.GestionTurnos.models;

import ar.com.GestionTurnos.entities.Profesionales;
import ar.com.GestionTurnos.entities.TurnosGenerales;

import java.util.List;

public class ProfesionalesTurnos {

    private List<Profesionales> profesionales;
    private List<TurnosGenerales> turnosGenerales;

    public List<Profesionales> getProfesionales() {
        return profesionales;
    }

    public void setProfesionales(List<Profesionales> profesionales) {
        this.profesionales = profesionales;
    }

    public List<TurnosGenerales> getTurnosGenerales() {
        return turnosGenerales;
    }

    public void setTurnosGenerales(List<TurnosGenerales> turnosGenerales) {
        this.turnosGenerales = turnosGenerales;
    }
}
