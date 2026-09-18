package com.parcial.promedio.dominio.puertos.salida;

import com.parcial.promedio.dominio.enums.EstadoServidor;
import com.parcial.promedio.dominio.modelos.EventoServidor;

/**
 * Puerto de salida para la emisión de eventos y cambios de estado del servidor hacia
 * observadores externos (como interfaces gráficas o sistemas de logging). Recibe únicamente
 * objetos del dominio.
 */
public interface PuertoNotificacionEvento {
    void notificarEvento(EventoServidor evento);

    void notificarCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
