package com.parcial.promedio.entrypoint.gui;

import com.parcial.promedio.adaptadores.notificacion.ObservadorCliente;
import com.parcial.promedio.adaptadores.red.util.RedUtil;
import com.parcial.promedio.aplicacion.dto.CalcularPromedioCommand;
import com.parcial.promedio.aplicacion.dto.ConectarCommand;
import com.parcial.promedio.aplicacion.puertos.entrada.CalcularPromedioInputPort;
import com.parcial.promedio.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.parcial.promedio.dominio.enums.EstadoConexion;
import com.parcial.promedio.dominio.excepciones.DominioException;
import com.parcial.promedio.dominio.modelos.EventoCliente;
import com.parcial.promedio.dominio.modelos.ResultadoPromedio;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ClienteFrame extends JFrame implements ObservadorCliente {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final transient GestionarConexionInputPort conexion;
    private final transient CalcularPromedioInputPort calculo;
    private final JTextField host = new JTextField("127.0.0.1");
    private final JTextField puerto = new JTextField("5000");
    private final JTextField nota1 = new JTextField();
    private final JTextField nota2 = new JTextField();
    private final JTextField nota3 = new JTextField();
    private final JButton conectar = new JButton("Conectar");
    private final JButton calcular = new JButton("Calcular Promedio");
    private final JLabel estado = new JLabel("● DESCONECTADO");
    private final JLabel resultado = new JLabel("Promedio: --");
    private final JTextArea logs = new JTextArea();

    public ClienteFrame(final GestionarConexionInputPort conexion, final CalcularPromedioInputPort calculo) {
        super("Cliente UDP - Promedio de 3 Notas (Arquitectura Hexagonal)");
        this.conexion = Objects.requireNonNull(conexion);
        this.calculo = Objects.requireNonNull(calculo);
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 580);
        setLocationRelativeTo(null);
        final JPanel red = new JPanel(new GridLayout(3, 2, 8, 8));
        red.setBorder(BorderFactory.createTitledBorder("Red - IP local: " + RedUtil.obtenerIpLocal()));
        red.add(new JLabel("Host del servidor:"));
        red.add(host);
        red.add(new JLabel("Puerto:"));
        red.add(puerto);
        red.add(estado);
        red.add(conectar);
        final JPanel datos = new JPanel(new GridLayout(4, 2, 8, 8));
        datos.setBorder(BorderFactory.createTitledBorder("Calificaciones (0.0 - 5.0)"));
        datos.add(new JLabel("Nota 1:"));
        datos.add(nota1);
        datos.add(new JLabel("Nota 2:"));
        datos.add(nota2);
        datos.add(new JLabel("Nota 3:"));
        datos.add(nota3);
        datos.add(resultado);
        datos.add(calcular);
        final JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.add(red);
        centro.add(datos);
        logs.setEditable(false);
        final JButton limpiar = new JButton("Limpiar log");
        limpiar.addActionListener(evento -> logs.setText(""));
        final JPanel sur = new JPanel(new BorderLayout());
        sur.add(limpiar, BorderLayout.NORTH);
        sur.add(new JScrollPane(logs), BorderLayout.CENTER);
        final JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(centro, BorderLayout.NORTH);
        contenido.add(sur, BorderLayout.CENTER);
        add(contenido);
        conectar.addActionListener(evento -> alternarConexion());
        calcular.addActionListener(evento -> solicitarCalculo());
        habilitarCalculo(false);
    }

    private void alternarConexion() {
        if (conexion.estaConectado()) {
            conexion.desconectar();
            return;
        }
        try {
            conexion.conectar(new ConectarCommand(host.getText(), Integer.parseInt(puerto.getText().trim())));
        } catch (final NumberFormatException | DominioException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    private void solicitarCalculo() {
        try {
            final double valorNota1 = Double.parseDouble(nota1.getText().trim().replace(',', '.'));
            final double valorNota2 = Double.parseDouble(nota2.getText().trim().replace(',', '.'));
            final double valorNota3 = Double.parseDouble(nota3.getText().trim().replace(',', '.'));
            calcular.setEnabled(false);
            calculo.calcular(new CalcularPromedioCommand(valorNota1, valorNota2, valorNota3));
        } catch (final NumberFormatException | DominioException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    @Override
    public void onEstado(final EstadoConexion nuevoEstado, final String endpoint) {
        SwingUtilities.invokeLater(() -> {
            estado.setText("● " + nuevoEstado + (endpoint.isBlank() ? "" : " - " + endpoint));
            final boolean conectado = nuevoEstado == EstadoConexion.CONECTADO;
            host.setEditable(!conectado);
            puerto.setEditable(!conectado);
            conectar.setText(conectado ? "Desconectar" : "Conectar");
            habilitarCalculo(conectado);
        });
    }

    @Override
    public void onEvento(final EventoCliente evento) {
        SwingUtilities.invokeLater(() -> logs.append("[" + evento.fechaHora().format(FECHA) + "] ["
                + evento.categoria() + "] " + evento.descripcion() + System.lineSeparator()));
    }

    @Override
    public void onResultado(final ResultadoPromedio valor) {
        SwingUtilities.invokeLater(() -> {
            resultado.setText("Promedio: " + valor.promedioFormateado() + " - " + valor.condicion());
            calcular.setEnabled(true);
        });
    }

    @Override
    public void onError(final String mensaje) {
        SwingUtilities.invokeLater(() -> {
            habilitarCalculo(conexion.estaConectado());
            mostrarError(mensaje);
        });
    }

    private void habilitarCalculo(final boolean habilitado) {
        nota1.setEnabled(habilitado);
        nota2.setEnabled(habilitado);
        nota3.setEnabled(habilitado);
        calcular.setEnabled(habilitado);
    }

    private void mostrarError(final String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Cliente UDP", JOptionPane.WARNING_MESSAGE);
    }
}
