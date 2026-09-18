package com.parcial.promedio.aplicacion.mapper;

import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.aplicacion.dto.ResultadoPromedioDto;
import com.parcial.promedio.aplicacion.excepciones.ComandoInvalidoException;
import com.parcial.promedio.dominio.modelos.Calculo;
import com.parcial.promedio.dominio.modelos.Resultado;
import com.parcial.promedio.dominio.vo.Nota;
import java.util.Objects;

/**
 * Mapper de la capa de aplicación. Responsabilidad única: Convertir comandos/DTOs de cálculo
 * a entidades del dominio y viceversa.
 */
public final class PromedioMapper {

    public Calculo toDomain(final CalcularPromedioCommand comando) {
        if (Objects.isNull(comando)) {
            throw new ComandoInvalidoException("El comando de cálculo no puede ser nulo.");
        }
        return toDomain(comando.nota1(), comando.nota2(), comando.nota3());
    }

    public Calculo toDomain(final double valorNota1, final double valorNota2, final double valorNota3) {
        final Nota nota1 = new Nota(valorNota1);
        final Nota nota2 = new Nota(valorNota2);
        final Nota nota3 = new Nota(valorNota3);
        return new Calculo(nota1, nota2, nota3);
    }

    public ResultadoPromedioDto toDto(final Resultado resultado) {
        if (Objects.isNull(resultado)) {
            throw new ComandoInvalidoException("El resultado del cálculo no puede ser nulo.");
        }
        return new ResultadoPromedioDto(
                resultado.getPromedio(),
                resultado.getPromedioFormateado(),
                resultado.getCondicion(),
                resultado.getObservacion());
    }
}
