package com.parcial.promedio.dominio.vo;

import com.parcial.promedio.dominio.excepciones.NotaIncorrectaException;

/** Value Object inmutable que encapsula y valida una calificación individual (0.0 - 5.0). */
public record Nota(double valor) {

    private static final double MINIMO = 0.0;
    private static final double MAXIMO = 5.0;

    public Nota {
        if (!Double.isFinite(valor) || valor < MINIMO || valor > MAXIMO) {
            throw new NotaIncorrectaException(
                    "La nota debe ser un número entre " + MINIMO + " y " + MAXIMO + ".");
        }
    }
}
