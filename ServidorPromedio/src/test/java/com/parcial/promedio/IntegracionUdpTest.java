package com.parcial.promedio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.parcial.promedio.adaptadores.red.AdaptadorNotificacionEvento;
import com.parcial.promedio.adaptadores.red.AdaptadorSalidaUdp;
import com.parcial.promedio.adaptadores.red.CanalUdp;
import com.parcial.promedio.adaptadores.red.mapper.UdpNetworkMapper;
import com.parcial.promedio.aplicacion.mapper.PeticionMapper;
import com.parcial.promedio.aplicacion.mapper.PromedioMapper;
import com.parcial.promedio.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.parcial.promedio.aplicacion.servicios.ProcesarPeticionUdpService;
import com.parcial.promedio.dominio.puertos.salida.PuertoSalidaRed;
import com.parcial.promedio.entrypoint.udp.ReceptorPeticionesUdp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Prueba de integración que levanta el servidor UDP real (canal + receptor + servicio de
 * aplicación) y verifica el protocolo de red completo usando un DatagramSocket de cliente.
 */
class IntegracionUdpTest {

    private CanalUdp canalUdp;
    private ReceptorPeticionesUdp receptor;
    private DatagramSocket clienteSocket;
    private int puertoServidor;

    @BeforeEach
    void iniciarServidor() throws IOException {
        canalUdp = new CanalUdp();
        final AdaptadorNotificacionEvento notificador = new AdaptadorNotificacionEvento();
        final PuertoSalidaRed puertoSalidaRed = new AdaptadorSalidaUdp(canalUdp, new UdpNetworkMapper(), notificador);
        final ProcesarPeticionUdpInputPort servicio = new ProcesarPeticionUdpService(
                puertoSalidaRed, notificador, new PeticionMapper(), new PromedioMapper());

        puertoServidor = 15123;
        canalUdp.abrir(puertoServidor);

        receptor = new ReceptorPeticionesUdp(canalUdp, servicio, notificador);
        receptor.iniciar();

        clienteSocket = new DatagramSocket();
        clienteSocket.setSoTimeout(2000);
    }

    @AfterEach
    void detenerServidor() {
        receptor.detener();
        canalUdp.cerrar();
        clienteSocket.close();
    }

    @Test
    void respondeConectadoOkAlComandoConectar() throws IOException {
        final String respuesta = enviarYRecibir("CONECTAR");
        assertTrue(respuesta.startsWith("CONECTADO_OK;"));
    }

    @Test
    void calculaElPromedioDeTresNotasPorRed() throws IOException {
        final String respuesta = enviarYRecibir("CALCULAR;4.0;3.5;5.0");
        final String[] partes = respuesta.split(";", -1);
        assertEquals("OK_PROMEDIO", partes[0]);
        assertEquals("4.17", partes[1]);
    }

    @Test
    void respondeErrorAntePeticionMalFormada() throws IOException {
        final String respuesta = enviarYRecibir("CALCULAR;4.0");
        assertTrue(respuesta.startsWith("ERROR;"));
    }

    private String enviarYRecibir(final String mensaje) throws IOException {
        final byte[] salida = mensaje.getBytes(StandardCharsets.UTF_8);
        clienteSocket.send(new DatagramPacket(
                salida, salida.length, InetAddress.getLoopbackAddress(), puertoServidor));

        final byte[] buffer = new byte[2048];
        final DatagramPacket entrada = new DatagramPacket(buffer, buffer.length);
        clienteSocket.receive(entrada);
        return new String(entrada.getData(), 0, entrada.getLength(), StandardCharsets.UTF_8);
    }
}
