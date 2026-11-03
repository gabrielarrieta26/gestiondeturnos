package ar.com.GestionTurnos.services;

import java.time.LocalDate;

public interface IInformeTurnosService {

    byte[] generarPdf(LocalDate desde, LocalDate hasta);
}
