package com.parcial.promedio.dominio.puertos.salida;

import com.parcial.promedio.aplicacion.excepciones.ClienteRedException;
import com.parcial.promedio.dominio.modelos.DatosPromedio;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;
import com.parcial.promedio.dominio.vo.DestinoServidor;

public interface ClienteUdpPort {
    void conectar(DestinoServidor destino) throws ClienteRedException;

    void desconectar() throws ClienteRedException;

    ResultadoPromedio solicitarCalculo(DatosPromedio datos) throws ClienteRedException;

    boolean estaConectado();
}
