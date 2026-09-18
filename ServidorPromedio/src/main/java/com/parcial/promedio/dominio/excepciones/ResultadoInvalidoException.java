package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando el resultado de un cálculo de promedio es inválido. */
public final class ResultadoInvalidoException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResultadoInvalidoException(final String mensaje) {
        super(mensaje);
    }
}
