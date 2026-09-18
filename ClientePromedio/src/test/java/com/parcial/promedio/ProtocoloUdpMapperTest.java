package com.parcial.promedio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parcial.promedio.adaptadores.red.ProtocoloUdpMapper;
import com.parcial.promedio.dominio.excepciones.RespuestaServidorException;
import com.parcial.promedio.dominio.modelos.DatosPromedio;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;
import com.parcial.promedio.dominio.vo.Nota;
import org.junit.jupiter.api.Test;

class ProtocoloUdpMapperTest {

    private final ProtocoloUdpMapper mapper = new ProtocoloUdpMapper();

    @Test
    void serializaElComandoCalcularConLasTresNotas() {
        final DatosPromedio datos = new DatosPromedio(new Nota(4.0), new Nota(3.5), new Nota(5.0));
        assertEquals("CALCULAR;4.00;3.50;5.00", mapper.serializarCalculo(datos));
    }

    @Test
    void parseaUnaRespuestaExitosaDelServidor() {
        final ResultadoPromedio resultado = mapper.parsearCalculo("OK_PROMEDIO;4.17;Notable (Bueno);Sigue así.");
        assertEquals(4.17, resultado.promedio());
        assertEquals("Notable (Bueno)", resultado.condicion());
    }

    @Test
    void rechazaUnaRespuestaDeErrorDelServidor() {
        assertThrows(RespuestaServidorException.class, () -> mapper.parsearCalculo("ERROR;Datos inválidos"));
    }
}
