package com.parcial.promedio.aplicacion.puertos.entrada;

import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;
import java.util.concurrent.CompletableFuture;

public interface CalcularPromedioInputPort {
    CompletableFuture<ResultadoPromedio> calcular(CalcularPromedioCommand comando);
}
