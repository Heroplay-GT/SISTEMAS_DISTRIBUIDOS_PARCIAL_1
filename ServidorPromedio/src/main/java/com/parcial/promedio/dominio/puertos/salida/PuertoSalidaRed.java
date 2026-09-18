package com.parcial.promedio.dominio.puertos.salida;

import com.parcial.promedio.dominio.modelos.RespuestaCliente;

/**
 * Puerto de salida para la comunicación de red con clientes. Cumple con el estándar
 * hexagonal: recibe exclusivamente objetos del dominio.
 */
public interface PuertoSalidaRed {
    void enviarRespuesta(RespuestaCliente respuesta);
}
