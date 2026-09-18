package com.parcial.promedio.adaptadores.notificacion;

import com.parcial.promedio.dominio.enums.EstadoConexion;
import com.parcial.promedio.dominio.modelos.EventoCliente;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;

public interface ObservadorCliente {
    void onEstado(EstadoConexion estado, String endpoint);

    void onEvento(EventoCliente evento);

    void onResultado(ResultadoPromedio resultado);

    void onError(String mensaje);
}
