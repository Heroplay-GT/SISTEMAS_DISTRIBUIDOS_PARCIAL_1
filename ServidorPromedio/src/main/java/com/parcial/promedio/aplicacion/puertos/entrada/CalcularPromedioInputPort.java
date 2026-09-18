package com.parcial.promedio.aplicacion.puertos.entrada;

import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.aplicacion.dto.ResultadoPromedioDto;

/** Puerto de entrada (Caso de Uso) para el cálculo del promedio. */
public interface CalcularPromedioInputPort {
    ResultadoPromedioDto calcular(CalcularPromedioCommand comando);
}
