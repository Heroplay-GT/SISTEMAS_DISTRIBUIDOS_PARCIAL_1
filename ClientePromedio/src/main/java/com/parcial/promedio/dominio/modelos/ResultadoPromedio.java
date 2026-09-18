package com.parcial.promedio.dominio.modelos;

import com.parcial.promedio.dominio.excepciones.RespuestaServidorException;
import java.util.Objects;

public record ResultadoPromedio(double promedio, String promedioFormateado, String condicion, String observacion) {
    public ResultadoPromedio {
        if (!Double.isFinite(promedio) || Objects.isNull(promedioFormateado) || promedioFormateado.isBlank()
                || Objects.isNull(condicion) || condicion.isBlank()
                || Objects.isNull(observacion)) {
            throw new RespuestaServidorException("El resultado recibido está incompleto o es inválido.");
        }
    }
}
