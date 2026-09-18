package com.parcial.promedio.aplicacion.servicios;

import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.aplicacion.dto.ConectarCommand;
import com.parcial.promedio.aplicacion.excepciones.ClienteRedException;
import com.parcial.promedio.aplicacion.mapper.ClienteMapper;
import com.parcial.promedio.aplicacion.puertos.entrada.CalcularPromedioInputPort;
import com.parcial.promedio.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.parcial.promedio.dominio.enums.EstadoConexion;
import com.parcial.promedio.dominio.modelos.DatosPromedio;
import com.parcial.promedio.dominio.modelos.EventoCliente;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;
import com.parcial.promedio.dominio.puertos.salida.ClienteUdpPort;
import com.parcial.promedio.dominio.puertos.salida.PuertoNotificacionCliente;
import com.parcial.promedio.dominio.vo.DestinoServidor;
import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Servicio de aplicación que orquesta la conexión y el cálculo de promedio contra el
 * servidor UDP. Responsabilidad única: coordinar el puerto de salida de red, el mapper y las
 * notificaciones hacia la interfaz de usuario, ejecutando las operaciones de red fuera del
 * hilo de eventos de Swing.
 */
public final class ClientePromedioService implements GestionarConexionInputPort, CalcularPromedioInputPort {

    private static final System.Logger LOG = System.getLogger(ClientePromedioService.class.getName());

    private final ClienteUdpPort clienteUdp;
    private final PuertoNotificacionCliente notificador;
    private final ClienteMapper mapper;
    private final Executor executor;

    public ClientePromedioService(
            final ClienteUdpPort clienteUdp,
            final PuertoNotificacionCliente notificador,
            final ClienteMapper mapper,
            final Executor executor) {
        this.clienteUdp = Objects.requireNonNull(clienteUdp, "El puerto UDP del cliente es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
        this.mapper = Objects.requireNonNull(mapper, "El mapper es obligatorio.");
        this.executor = Objects.requireNonNull(executor, "El executor es obligatorio.");
    }

    @Override
    public CompletableFuture<Void> conectar(final ConectarCommand comando) {
        final DestinoServidor destino = mapper.toDestino(comando);
        notificador.notificarEstado(EstadoConexion.CONECTANDO, destino.endpoint());
        return CompletableFuture.runAsync(() -> {
            try {
                clienteUdp.conectar(destino);
                notificador.notificarEstado(EstadoConexion.CONECTADO, destino.endpoint());
                notificador.notificarEvento(new EventoCliente("CONEXIÓN", "Conectado a " + destino.endpoint()));
            } catch (final ClienteRedException excepcion) {
                LOG.log(Level.WARNING, "Falló la operación UDP conectar", excepcion);
                notificador.notificarEstado(EstadoConexion.DESCONECTADO, destino.endpoint());
                notificador.notificarError(excepcion.getMessage());
                throw new CompletionException(excepcion);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> desconectar() {
        return CompletableFuture.runAsync(() -> {
            try {
                clienteUdp.desconectar();
                notificador.notificarEstado(EstadoConexion.DESCONECTADO, "");
                notificador.notificarEvento(new EventoCliente("CONEXIÓN", "Cliente desconectado."));
            } catch (final ClienteRedException excepcion) {
                LOG.log(Level.WARNING, "Falló la operación UDP desconectar", excepcion);
                notificador.notificarError(excepcion.getMessage());
                throw new CompletionException(excepcion);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<ResultadoPromedio> calcular(final CalcularPromedioCommand comando) {
        final DatosPromedio datos = mapper.toDatos(comando);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final ResultadoPromedio resultado = clienteUdp.solicitarCalculo(datos);
                notificador.notificarResultado(resultado);
                return resultado;
            } catch (final ClienteRedException excepcion) {
                LOG.log(Level.WARNING, "Falló la operación UDP calcular", excepcion);
                notificador.notificarError(excepcion.getMessage());
                throw new CompletionException(excepcion);
            }
        }, executor);
    }

    @Override
    public boolean estaConectado() {
        return clienteUdp.estaConectado();
    }
}
