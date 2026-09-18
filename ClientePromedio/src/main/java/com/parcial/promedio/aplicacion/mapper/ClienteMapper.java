package com.parcial.promedio.aplicacion.mapper;

import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.aplicacion.dto.ConectarCommand;
import com.parcial.promedio.dominio.modelos.DatosPromedio;
import com.parcial.promedio.dominio.vo.DestinoServidor;
import com.parcial.promedio.dominio.vo.Nota;
import java.util.Objects;

public final class ClienteMapper {
    public DestinoServidor toDestino(final ConectarCommand comando) {
        Objects.requireNonNull(comando, "El comando de conexión es obligatorio.");
        return new DestinoServidor(comando.host(), comando.puerto());
    }

    public DatosPromedio toDatos(final CalcularPromedioCommand comando) {
        Objects.requireNonNull(comando, "El comando de cálculo es obligatorio.");
        return new DatosPromedio(
                new Nota(comando.nota1()), new Nota(comando.nota2()), new Nota(comando.nota3()));
    }
}
