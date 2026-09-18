package com.parcial.promedio.aplicacion.puertos.entrada;

import com.parcial.promedio.aplicacion.dto.ProcesarPeticionUdpCommand;

/** Puerto de entrada para procesar las peticiones recibidas por el listener UDP. */
public interface ProcesarPeticionUdpInputPort {
    void procesar(ProcesarPeticionUdpCommand comando);
}
