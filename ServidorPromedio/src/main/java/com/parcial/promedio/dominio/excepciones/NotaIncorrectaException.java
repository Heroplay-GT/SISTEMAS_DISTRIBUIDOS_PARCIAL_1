package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando el valor de una nota es inválido. */
public final class NotaIncorrectaException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotaIncorrectaException(final String mensaje) {
        super(mensaje);
    }
}
