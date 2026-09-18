package com.parcial.promedio.adaptadores.red;

import com.parcial.promedio.dominio.enums.EstadoServidor;
import com.parcial.promedio.dominio.modelos.EventoServidor;

/** Interfaz de observador para componentes interesados en los eventos y estados del servidor. */
public interface ObservadorServidor {
    void onEvento(EventoServidor evento);

    void onCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
