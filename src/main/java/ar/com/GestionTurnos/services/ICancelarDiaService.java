package ar.com.GestionTurnos.services;

import ar.com.GestionTurnos.models.CancelacionDia;

public interface ICancelarDiaService {
    void clasificar(CancelacionDia criterio);

    int confirmar(CancelacionDia criterio);
}
