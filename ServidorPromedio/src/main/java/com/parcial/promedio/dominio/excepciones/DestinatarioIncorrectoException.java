package com.parcial.promedio.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando el destinatario de red es inválido. */
public final class DestinatarioIncorrectoException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DestinatarioIncorrectoException(final String mensaje) {
        super(mensaje);
    }
}
