package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando un cálculo de promedio recibe datos inválidos. */
public final class CalculoInvalidoException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CalculoInvalidoException(final String mensaje) {
        super(mensaje);
    }
}
