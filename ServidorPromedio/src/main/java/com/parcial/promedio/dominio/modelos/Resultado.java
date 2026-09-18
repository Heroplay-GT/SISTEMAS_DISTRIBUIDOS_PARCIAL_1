package com.parcial.promedio.dominio.modelos;

import com.parcial.promedio.dominio.enums.CondicionAcademica;
import com.parcial.promedio.dominio.excepciones.ResultadoInvalidoException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

/**
 * Representa el resultado inmutable del cálculo del promedio.
 * Responsabilidad única: Encapsular los datos del diagnóstico académico y su formateo.
 */
public final class Resultado {

    private final double promedio;
    private final String condicion;
    private final String observacion;
    private final CondicionAcademica condicionAcademica;

    public Resultado(
            final double promedio,
            final String condicion,
            final String observacion,
            final CondicionAcademica condicionAcademica) {
        if (!Double.isFinite(promedio) || Objects.isNull(condicion)
                || Objects.isNull(observacion) || Objects.isNull(condicionAcademica)) {
            throw new ResultadoInvalidoException("El resultado del promedio contiene datos inválidos.");
        }
        this.promedio = promedio;
        this.condicion = condicion;
        this.observacion = observacion;
        this.condicionAcademica = condicionAcademica;
    }

    public Resultado(final double promedio, final String condicion, final String observacion) {
        this(promedio, condicion, observacion, CondicionAcademica.dePromedio(promedio));
    }

    public double getPromedio() {
        return promedio;
    }

    public String getCondicion() {
        return condicion;
    }

    public String getObservacion() {
        return observacion;
    }

    public CondicionAcademica getCondicionAcademica() {
        return condicionAcademica;
    }

    public String getPromedioFormateado() {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        final DecimalFormat df = new DecimalFormat("#.##", symbols);
        return df.format(promedio);
    }
}
