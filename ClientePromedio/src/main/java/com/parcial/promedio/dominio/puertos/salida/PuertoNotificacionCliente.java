package com.parcial.promedio.dominio.puertos.salida;

import com.parcial.promedio.dominio.enums.EstadoConexion;
import com.parcial.promedio.dominio.modelos.EventoCliente;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;

public interface PuertoNotificacionCliente {
    void notificarEstado(EstadoConexion estado, String endpoint);

    void notificarEvento(EventoCliente evento);

    void notificarResultado(ResultadoPromedio resultado);

    void notificarError(String mensaje);
}
