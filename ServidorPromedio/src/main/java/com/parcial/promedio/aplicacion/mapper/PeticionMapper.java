package com.parcial.promedio.aplicacion.mapper;

import com.parcial.promedio.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.parcial.promedio.aplicacion.excepciones.ComandoInvalidoException;
import com.parcial.promedio.dominio.vo.Destinatario;
import java.util.Objects;

/**
 * Mapper de la capa de aplicación. Responsabilidad única: Convertir datos de comandos de
 * petición a objetos de valor del dominio.
 */
public final class PeticionMapper {

    public Destinatario toDestinatario(final ProcesarPeticionUdpCommand comando) {
        if (Objects.isNull(comando)) {
            throw new ComandoInvalidoException("El comando de petición no puede ser nulo.");
        }
        return new Destinatario(comando.ipCliente(), comando.puertoCliente());
    }
}
