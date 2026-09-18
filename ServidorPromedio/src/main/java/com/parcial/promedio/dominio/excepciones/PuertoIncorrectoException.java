package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando un puerto de red es inválido. */
public final class PuertoIncorrectoException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PuertoIncorrectoException(final String mensaje) {
        super(mensaje);
    }
}
