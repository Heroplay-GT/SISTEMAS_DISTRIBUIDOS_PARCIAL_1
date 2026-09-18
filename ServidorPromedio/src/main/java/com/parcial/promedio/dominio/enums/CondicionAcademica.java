package com.parcial.promedio.dominio.enums;

/** Clasificación diagnóstica del promedio académico en la escala 0.0 - 5.0. */
public enum CondicionAcademica {
    REPROBADO(
            "Reprobado (Insuficiente)",
            "Es necesario reforzar los temas vistos y buscar apoyo académico."),
    APROBADO(
            "Aprobado (Aceptable)",
            "Buen desempeño; continúa reforzando tus conocimientos."),
    NOTABLE(
            "Notable (Bueno)",
            "Muy buen desempeño académico, sigue así."),
    SOBRESALIENTE(
            "Sobresaliente (Muy bueno)",
            "Excelente rendimiento, estás muy cerca de la nota máxima."),
    EXCELENTE(
            "Excelente (Óptimo)",
            "Desempeño excepcional. ¡Felicitaciones!");

    private final String condicion;
    private final String observacion;

    CondicionAcademica(final String condicion, final String observacion) {
        this.condicion = condicion;
        this.observacion = observacion;
    }

    public String getCondicion() {
        return condicion;
    }

    public String getObservacion() {
        return observacion;
    }

    /**
     * Determina la condición académica según el valor del promedio.
     *
     * @param promedio Promedio calculado (escala 0.0 - 5.0).
     * @return La categoría correspondiente.
     */
    public static CondicionAcademica dePromedio(final double promedio) {
        if (promedio < 3.0) {
            return REPROBADO;
        } else if (promedio < 4.0) {
            return APROBADO;
        } else if (promedio < 4.5) {
            return NOTABLE;
        } else if (promedio < 5.0) {
            return SOBRESALIENTE;
        } else {
            return EXCELENTE;
        }
    }
}
