package ar.com.GestionTurnos.models;

import ar.com.GestionTurnos.entities.Dias;
import ar.com.GestionTurnos.entities.Horarios;

import java.util.List;

public class DiasHorarios {

    private List<Dias> dias;
    private List<Horarios> horarios;

    public List<Dias> getDias() {
        return dias;
    }

    public void setDias(List<Dias> dias) {
        this.dias = dias;
    }

    public List<Horarios> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horarios> horarios) {
        this.horarios = horarios;
    }
}
