package com.parcial.promedio.dominio.modelos;

import com.parcial.promedio.dominio.vo.Nota;
import java.util.Objects;

public record DatosPromedio(Nota nota1, Nota nota2, Nota nota3) {
    public DatosPromedio {
        Objects.requireNonNull(nota1, "La nota 1 es obligatoria.");
        Objects.requireNonNull(nota2, "La nota 2 es obligatoria.");
        Objects.requireNonNull(nota3, "La nota 3 es obligatoria.");
    }
}
