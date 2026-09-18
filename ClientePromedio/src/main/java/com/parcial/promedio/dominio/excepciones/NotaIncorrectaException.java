package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

public final class NotaIncorrectaException extends DominioException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotaIncorrectaException(final String mensaje) {
        super(mensaje);
    }
}
