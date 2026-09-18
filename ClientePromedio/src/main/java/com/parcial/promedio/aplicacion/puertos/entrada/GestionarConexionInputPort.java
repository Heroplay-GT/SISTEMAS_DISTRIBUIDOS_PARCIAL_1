package com.parcial.promedio.aplicacion.puertos.entrada;

import com.parcial.promedio.aplicacion.dto.ConectarCommand;
import java.util.concurrent.CompletableFuture;

public interface GestionarConexionInputPort {
    CompletableFuture<Void> conectar(ConectarCommand comando);

    CompletableFuture<Void> desconectar();

    boolean estaConectado();
}
