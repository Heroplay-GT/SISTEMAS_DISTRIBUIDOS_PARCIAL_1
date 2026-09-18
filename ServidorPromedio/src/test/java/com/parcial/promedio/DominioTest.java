package com.parcial.promedio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parcial.promedio.dominio.enums.CondicionAcademica;
import com.parcial.promedio.dominio.excepciones.NotaIncorrectaException;
import com.parcial.promedio.dominio.modelos.Calculo;
import com.parcial.promedio.dominio.modelos.Resultado;
import com.parcial.promedio.dominio.vo.Nota;
import org.junit.jupiter.api.Test;

class DominioTest {

    @Test
    void calculaElPromedioDeTresNotas() {
        final Calculo calculo = new Calculo(new Nota(4.0), new Nota(3.5), new Nota(5.0));
        final Resultado resultado = calculo.calcular();

        assertEquals(4.17, resultado.getPromedio(), 0.01);
        assertEquals(CondicionAcademica.NOTABLE, resultado.getCondicionAcademica());
    }

    @Test
    void rechazaNotasFueraDeRango() {
        assertThrows(NotaIncorrectaException.class, () -> new Nota(5.1));
        assertThrows(NotaIncorrectaException.class, () -> new Nota(-0.1));
    }

    @Test
    void clasificaLaCondicionAcademicaSegunElPromedio() {
        assertEquals(CondicionAcademica.REPROBADO, CondicionAcademica.dePromedio(2.9));
        assertEquals(CondicionAcademica.APROBADO, CondicionAcademica.dePromedio(3.5));
        assertEquals(CondicionAcademica.EXCELENTE, CondicionAcademica.dePromedio(5.0));
    }
}
