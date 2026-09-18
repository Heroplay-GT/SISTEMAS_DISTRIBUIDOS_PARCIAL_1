package com.parcial.promedio.adaptadores.red;

import com.parcial.promedio.aplicacion.excepciones.ClienteRedException;
import com.parcial.promedio.dominio.excepciones.RespuestaServidorException;
import com.parcial.promedio.dominio.modelos.DatosPromedio;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;
import com.parcial.promedio.dominio.puertos.salida.ClienteUdpPort;
import com.parcial.promedio.dominio.vo.DestinoServidor;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

public final class AdaptadorClienteUdp implements ClienteUdpPort {

    private static final System.Logger LOG = System.getLogger(AdaptadorClienteUdp.class.getName());

    private final CanalUdp canal;
    private final ProtocoloUdpMapper protocolo;
    private volatile ConexionActiva conexion;

    public AdaptadorClienteUdp(final CanalUdp canal, final ProtocoloUdpMapper protocolo) {
        this.canal = Objects.requireNonNull(canal, "El canal UDP es obligatorio.");
        this.protocolo = Objects.requireNonNull(protocolo, "El mapper de protocolo es obligatorio.");
    }

    @Override
    public synchronized void conectar(final DestinoServidor nuevoDestino) throws ClienteRedException {
        Objects.requireNonNull(nuevoDestino);
        try {
            final InetAddress nuevaDireccion = InetAddress.getByName(nuevoDestino.host());
            canal.abrir();
            final String respuesta = canal.intercambiar(
                    ProtocoloUdpMapper.CONECTAR, nuevaDireccion, nuevoDestino.puerto());
            protocolo.validarConexion(respuesta);
            conexion = new ConexionActiva(nuevaDireccion, nuevoDestino);
        } catch (final SocketTimeoutException excepcion) {
            LOG.log(Level.WARNING, "Fallo UDP durante la conexión por timeout", excepcion);
            canal.cerrar();
            throw new ClienteRedException("El servidor no respondió dentro del tiempo esperado.", excepcion);
        } catch (final UnknownHostException excepcion) {
            LOG.log(Level.WARNING, "Fallo UDP durante la resolución del host", excepcion);
            canal.cerrar();
            throw new ClienteRedException("No se pudo resolver el host " + nuevoDestino.host() + ".", excepcion);
        } catch (final IOException | RespuestaServidorException excepcion) {
            LOG.log(Level.ERROR, "Fallo UDP durante la conexión", excepcion);
            canal.cerrar();
            throw new ClienteRedException("No se pudo conectar con " + nuevoDestino.endpoint() + ".", excepcion);
        }
    }

    @Override
    public synchronized void desconectar() throws ClienteRedException {
        try {
            if (Objects.nonNull(conexion)) {
                final ConexionActiva actual = conexion;
                canal.enviar(ProtocoloUdpMapper.DESCONECTAR, actual.direccion(), actual.destino().puerto());
            }
        } catch (final IOException excepcion) {
            LOG.log(Level.WARNING, "Fallo UDP durante la desconexión", excepcion);
            throw new ClienteRedException("No se pudo notificar la desconexión.", excepcion);
        } finally {
            conexion = null;
            canal.cerrar();
        }
    }

    @Override
    public synchronized ResultadoPromedio solicitarCalculo(final DatosPromedio datos) throws ClienteRedException {
        if (Objects.isNull(conexion)) {
            throw new ClienteRedException("Debe conectarse antes de solicitar un cálculo.");
        }
        try {
            final ConexionActiva actual = conexion;
            final String respuesta = canal.intercambiar(protocolo.serializarCalculo(datos),
                    actual.direccion(), actual.destino().puerto());
            return protocolo.parsearCalculo(respuesta);
        } catch (final SocketTimeoutException excepcion) {
            LOG.log(Level.WARNING, "Fallo UDP durante el cálculo por timeout", excepcion);
            throw new ClienteRedException("El servidor no respondió al cálculo.", excepcion);
        } catch (final IOException | RespuestaServidorException excepcion) {
            LOG.log(Level.ERROR, "Fallo UDP durante el cálculo", excepcion);
            throw new ClienteRedException("Falló la comunicación del cálculo.", excepcion);
        }
    }

    @Override
    public boolean estaConectado() {
        return Objects.nonNull(conexion) && canal.estaAbierto();
    }

    private record ConexionActiva(InetAddress direccion, DestinoServidor destino) {
        private ConexionActiva {
            Objects.requireNonNull(direccion, "La dirección es obligatoria.");
            Objects.requireNonNull(destino, "El destino es obligatorio.");
        }
    }
}
