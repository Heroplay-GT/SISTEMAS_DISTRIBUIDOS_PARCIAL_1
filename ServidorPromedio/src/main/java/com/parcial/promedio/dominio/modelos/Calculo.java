package com.parcial.promedio.dominio.modelos;

import com.parcial.promedio.dominio.enums.CondicionAcademica;
import com.parcial.promedio.dominio.excepciones.CalculoInvalidoException;
import com.parcial.promedio.dominio.vo.Nota;
import java.util.Objects;

/**
 * Entidad/Modelo del dominio para el cálculo del promedio de tres calificaciones.
 * Responsabilidad única: Aplicar la fórmula del promedio y el criterio diagnóstico académico.
 */
public final class Calculo {

    private final Nota nota1;
    private final Nota nota2;
    private final Nota nota3;

    public Calculo(final Nota nota1, final Nota nota2, final Nota nota3) {
        if (Objects.isNull(nota1) || Objects.isNull(nota2) || Objects.isNull(nota3)) {
            throw new CalculoInvalidoException(
                    "Las tres calificaciones son obligatorias para calcular el promedio.");
        }
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
    }

    /**
     * Calcula el promedio de las tres calificaciones y determina la condición académica.
     *
     * @return Objeto Resultado con el valor numérico, condición y observación.
     */
    public Resultado calcular() {
        final double promedio = (nota1.valor() + nota2.valor() + nota3.valor()) / 3.0;
        final CondicionAcademica condicion = CondicionAcademica.dePromedio(promedio);

        return new Resultado(
                promedio, condicion.getCondicion(), condicion.getObservacion(), condicion);
    }

    public Nota getNota1() {
        return nota1;
    }

    public Nota getNota2() {
        return nota2;
    }

    public Nota getNota3() {
        return nota3;
    }
}
