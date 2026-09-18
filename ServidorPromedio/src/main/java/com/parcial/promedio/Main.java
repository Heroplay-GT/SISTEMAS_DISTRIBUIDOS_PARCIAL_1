package com.parcial.promedio;

import com.parcial.promedio.adaptadores.red.AdaptadorControlServidorRed;
import com.parcial.promedio.adaptadores.red.AdaptadorNotificacionEvento;
import com.parcial.promedio.adaptadores.red.AdaptadorSalidaUdp;
import com.parcial.promedio.adaptadores.red.CanalUdp;
import com.parcial.promedio.adaptadores.red.mapper.UdpNetworkMapper;
import com.parcial.promedio.aplicacion.mapper.PeticionMapper;
import com.parcial.promedio.aplicacion.mapper.PromedioMapper;
import com.parcial.promedio.aplicacion.puertos.entrada.GestionarServidorInputPort;
import com.parcial.promedio.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.parcial.promedio.aplicacion.servicios.GestionarServidorService;
import com.parcial.promedio.aplicacion.servicios.ProcesarPeticionUdpService;
import com.parcial.promedio.dominio.puertos.salida.ControladorServidorRedPort;
import com.parcial.promedio.dominio.puertos.salida.PuertoSalidaRed;
import com.parcial.promedio.entrypoint.gui.ServidorFrame;
import com.parcial.promedio.entrypoint.udp.ReceptorPeticionesUdp;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada del programa (Composition Root). Responsabilidad única: Ensamblar las
 * dependencias según los principios SOLID y la Arquitectura Hexagonal, y lanzar la interfaz
 * de usuario.
 */
public final class Main {

    private static final System.Logger LOG = System.getLogger(Main.class.getName());

    private Main() {
        // Evita instanciación – clase de arranque estática
    }

    public static void main(final String[] args) {
        aplicarLookAndFeel();

        // 1. Infraestructura de red compartida
        final CanalUdp canalUdp = new CanalUdp();
        final AdaptadorNotificacionEvento notificador = new AdaptadorNotificacionEvento();
        final UdpNetworkMapper redMapper = new UdpNetworkMapper();
        final PuertoSalidaRed puertoSalidaRed = new AdaptadorSalidaUdp(canalUdp, redMapper, notificador);

        // 2. Mappers de aplicación
        final PeticionMapper peticionMapper = new PeticionMapper();
        final PromedioMapper promedioMapper = new PromedioMapper();

        // 3. Casos de uso (servicios de aplicación)
        final ProcesarPeticionUdpInputPort procesarPeticionPort = new ProcesarPeticionUdpService(puertoSalidaRed,
                notificador, peticionMapper, promedioMapper);

        // 4. Entrypoints
        final ReceptorPeticionesUdp receptorUdp = new ReceptorPeticionesUdp(canalUdp, procesarPeticionPort, notificador);

        final ControladorServidorRedPort controladorRed = new AdaptadorControlServidorRed(canalUdp, receptorUdp::iniciar,
                receptorUdp::detener);

        final GestionarServidorInputPort gestionarServidorPort = new GestionarServidorService(controladorRed, notificador);

        // 5. Lanzar GUI en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(
                () -> {
                    final ServidorFrame frame = new ServidorFrame(gestionarServidorPort);
                    notificador.registrarObservador(frame);
                    frame.setVisible(true);
                });
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException excepcion) {
            LOG.log(System.Logger.Level.DEBUG,
                    "No fue posible aplicar la apariencia del sistema; se usará la predeterminada.", excepcion);
        }
    }
}
