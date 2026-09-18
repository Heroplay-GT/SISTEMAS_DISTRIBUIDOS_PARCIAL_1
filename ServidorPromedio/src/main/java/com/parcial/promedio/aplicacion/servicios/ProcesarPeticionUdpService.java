package com.parcial.promedio.aplicacion.servicios;

import com.parcial.promedio.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.parcial.promedio.aplicacion.mapper.PeticionMapper;
import com.parcial.promedio.aplicacion.mapper.PromedioMapper;
import com.parcial.promedio.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.parcial.promedio.dominio.excepciones.DominioException;
import com.parcial.promedio.dominio.modelos.Calculo;
import com.parcial.promedio.dominio.modelos.EventoServidor;
import com.parcial.promedio.dominio.modelos.RespuestaCliente;
import com.parcial.promedio.dominio.modelos.Resultado;
import com.parcial.promedio.dominio.puertos.salida.PuertoNotificacionEvento;
import com.parcial.promedio.dominio.puertos.salida.PuertoSalidaRed;
import com.parcial.promedio.dominio.vo.Destinatario;
import java.util.Locale;
import java.util.Objects;

/**
 * Servicio de aplicación para procesar las peticiones del protocolo UDP de promedio de
 * notas. Responsabilidad única: Decodificar y orquestar el procesamiento de los comandos de
 * la red, coordinar con el dominio y despachar respuestas a través de los puertos de salida.
 */
public final class ProcesarPeticionUdpService implements ProcesarPeticionUdpInputPort {

    private final PuertoSalidaRed puertoSalidaRed;
    private final PuertoNotificacionEvento puertoNotificacion;
    private final PeticionMapper peticionMapper;
    private final PromedioMapper promedioMapper;

    public ProcesarPeticionUdpService(
            final PuertoSalidaRed puertoSalidaRed,
            final PuertoNotificacionEvento puertoNotificacion,
            final PeticionMapper peticionMapper,
            final PromedioMapper promedioMapper) {
        this.puertoSalidaRed = Objects.requireNonNull(puertoSalidaRed, "El puerto de salida es obligatorio.");
        this.puertoNotificacion = Objects.requireNonNull(puertoNotificacion, "El puerto de notificación es obligatorio.");
        this.peticionMapper = Objects.requireNonNull(peticionMapper, "El mapper de petición es obligatorio.");
        this.promedioMapper = Objects.requireNonNull(promedioMapper, "El mapper de cálculo es obligatorio.");
    }

    @Override
    public void procesar(final ProcesarPeticionUdpCommand comando) {
        Objects.requireNonNull(comando, "El comando es obligatorio.");

        final Destinatario destinatario = peticionMapper.toDestinatario(comando);
        final String mensaje = comando.mensaje();

        if (Objects.isNull(mensaje) || mensaje.isBlank()) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.error(destinatario, "Mensaje vacío recibido."));
            notificarEvento(destinatario.endpoint(), "datos recibidos --> [Vacío]");
            return;
        }

        final String comandoTexto = mensaje.trim();

        if (comandoTexto.equalsIgnoreCase("CONECTAR")) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.conectado(
                            destinatario, "Servidor UDP listo para recibir cálculos de promedio"));
            notificarEvento(
                    destinatario.endpoint(),
                    "conectado --> Solicitud de verificación recibida y aceptada");
            return;
        }

        if (comandoTexto.equalsIgnoreCase("DESCONECTAR")) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.desconectado(destinatario, "Sesión finalizada"));
            notificarEvento(destinatario.endpoint(), "desconectado --> Cliente ha cerrado la sesión");
            return;
        }

        if (comandoTexto.toUpperCase(Locale.ROOT).startsWith("CALCULAR;")) {
            procesarCalculo(destinatario, comandoTexto);
            return;
        }

        puertoSalidaRed.enviarRespuesta(
                RespuestaCliente.error(destinatario, "Comando no reconocido por el servidor UDP."));
        notificarEvento(
                destinatario.endpoint(),
                "datos recibidos --> Comando desconocido: [" + comandoTexto + "]");
    }

    private void procesarCalculo(final Destinatario destinatario, final String comandoTexto) {
        final String[] partes = comandoTexto.split(";", -1);
        if (partes.length != 4) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.error(
                            destinatario, "Formato inválido. Se esperaba CALCULAR;nota1;nota2;nota3"));
            notificarEvento(
                    destinatario.endpoint(),
                    "datos recibidos --> Formato incorrecto: " + comandoTexto);
            return;
        }

        try {
            final double valorNota1 = Double.parseDouble(partes[1].trim().replace(',', '.'));
            final double valorNota2 = Double.parseDouble(partes[2].trim().replace(',', '.'));
            final double valorNota3 = Double.parseDouble(partes[3].trim().replace(',', '.'));

            final Calculo calculo = promedioMapper.toDomain(valorNota1, valorNota2, valorNota3);
            final Resultado resultado = calculo.calcular();

            puertoSalidaRed.enviarRespuesta(RespuestaCliente.calculoExitoso(destinatario, resultado));

            final String logInfo = String.format(
                    Locale.US,
                    "datos recibidos (Notas: %.2f, %.2f, %.2f) --> datos enviados (Promedio: %s, %s)",
                    calculo.getNota1().valor(),
                    calculo.getNota2().valor(),
                    calculo.getNota3().valor(),
                    resultado.getPromedioFormateado(),
                    resultado.getCondicion());

            notificarEvento(destinatario.endpoint(), logInfo);

        } catch (final NumberFormatException excepcion) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.error(
                            destinatario, "Los parámetros de las notas deben ser numéricos."));
            notificarEvento(
                    destinatario.endpoint(),
                    "datos recibidos --> Error de formato numérico: " + comandoTexto);

        } catch (final DominioException excepcion) {
            puertoSalidaRed.enviarRespuesta(RespuestaCliente.error(destinatario, excepcion.getMessage()));
            notificarEvento(
                    destinatario.endpoint(),
                    "datos recibidos --> Validación fallida: " + excepcion.getMessage());
        }
    }

    private void notificarEvento(final String endpoint, final String descripcion) {
        puertoNotificacion.notificarEvento(new EventoServidor("EVENTO", endpoint, descripcion));
    }
}
