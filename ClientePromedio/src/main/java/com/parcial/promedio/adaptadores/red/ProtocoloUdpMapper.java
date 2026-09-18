package com.parcial.promedio.adaptadores.red;

import com.parcial.promedio.dominio.excepciones.RespuestaServidorException;
import com.parcial.promedio.dominio.modelos.DatosPromedio;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;
import java.util.Locale;
import java.util.Objects;

public final class ProtocoloUdpMapper {
    public static final String CONECTAR = "CONECTAR";
    public static final String DESCONECTAR = "DESCONECTAR";

    public String serializarCalculo(final DatosPromedio datos) {
        Objects.requireNonNull(datos, "Los datos del promedio son obligatorios.");
        return String.format(Locale.US, "CALCULAR;%.2f;%.2f;%.2f",
                datos.nota1().valor(), datos.nota2().valor(), datos.nota3().valor());
    }

    public void validarConexion(final String respuesta) {
        if (Objects.isNull(respuesta) || !respuesta.startsWith("CONECTADO_OK;")) {
            throw new RespuestaServidorException("Respuesta de conexión inesperada: " + respuesta);
        }
    }

    public ResultadoPromedio parsearCalculo(final String respuesta) {
        if (Objects.isNull(respuesta) || respuesta.isBlank()) {
            throw new RespuestaServidorException("El servidor envió una respuesta vacía.");
        }
        if (respuesta.startsWith("ERROR;")) {
            throw new RespuestaServidorException("Error del servidor: " + respuesta.substring(6));
        }
        final String[] partes = respuesta.split(";", -1);
        if (partes.length != 4 || !"OK_PROMEDIO".equals(partes[0])) {
            throw new RespuestaServidorException("Respuesta de cálculo no estructurada: " + respuesta);
        }
        try {
            final double promedio = Double.parseDouble(partes[1].replace(',', '.'));
            return new ResultadoPromedio(promedio, partes[1], partes[2], partes[3]);
        } catch (final NumberFormatException excepcion) {
            throw new RespuestaServidorException("El promedio recibido no es numérico.");
        }
    }
}
