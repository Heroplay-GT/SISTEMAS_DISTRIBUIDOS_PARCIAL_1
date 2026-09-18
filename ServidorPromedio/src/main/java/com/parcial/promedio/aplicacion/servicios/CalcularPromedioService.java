package com.parcial.promedio.aplicacion.servicios;

import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.aplicacion.dto.ResultadoPromedioDto;
import com.parcial.promedio.aplicacion.mapper.PromedioMapper;
import com.parcial.promedio.aplicacion.puertos.entrada.CalcularPromedioInputPort;
import com.parcial.promedio.dominio.modelos.Calculo;
import com.parcial.promedio.dominio.modelos.Resultado;
import java.util.Objects;

/**
 * Servicio de aplicación que implementa el caso de uso de calcular el promedio.
 * Responsabilidad única: Orquestar la conversión del comando a dominio, invocar la lógica
 * del dominio y retornar el resultado en formato DTO.
 */
public final class CalcularPromedioService implements CalcularPromedioInputPort {

    private final PromedioMapper promedioMapper;

    public CalcularPromedioService(final PromedioMapper promedioMapper) {
        this.promedioMapper = Objects.requireNonNull(promedioMapper, "El mapper de cálculo es obligatorio.");
    }

    @Override
    public ResultadoPromedioDto calcular(final CalcularPromedioCommand comando) {
        final Calculo calculo = promedioMapper.toDomain(comando);
        final Resultado resultado = calculo.calcular();
        return promedioMapper.toDto(resultado);
    }
}
