package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando una respuesta hacia el cliente es inválida. */
public final class RespuestaInvalidaException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RespuestaInvalidaException(final String mensaje) {
        super(mensaje);
    }
}
