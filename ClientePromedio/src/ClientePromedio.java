import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class ClientePromedio extends JFrame {

    private static final double NOTA_MIN = 0.0;
    private static final double NOTA_MAX = 5.0;

    private JTextField txtServidor;
    private JTextField txtPuerto;
    private JTextField txtNota1;
    private JTextField txtNota2;
    private JTextField txtNota3;
    private JLabel lblResultado;
    private JButton btnCalcular;
    private JButton btnLimpiar;

    public ClientePromedio() {
        super("Cliente - Promedio de 3 calificaciones");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 340);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelDatos = new JPanel(new GridLayout(6, 2, 8, 8));
        panelDatos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        panelDatos.add(new JLabel("Servidor (IP o host):"));
        txtServidor = new JTextField("localhost");
        panelDatos.add(txtServidor);

        panelDatos.add(new JLabel("Puerto:"));
        txtPuerto = new JTextField("5000");
        panelDatos.add(txtPuerto);

        panelDatos.add(new JLabel("Calificacion 1 (0.0 - 5.0):"));
        txtNota1 = new JTextField();
        panelDatos.add(txtNota1);

        panelDatos.add(new JLabel("Calificacion 2 (0.0 - 5.0):"));
        txtNota2 = new JTextField();
        panelDatos.add(txtNota2);

        panelDatos.add(new JLabel("Calificacion 3 (0.0 - 5.0):"));
        txtNota3 = new JTextField();
        panelDatos.add(txtNota3);

        btnCalcular = new JButton("Calcular promedio");
        btnLimpiar = new JButton("Limpiar");
        panelDatos.add(btnCalcular);
        panelDatos.add(btnLimpiar);

        add(panelDatos, BorderLayout.CENTER);

        lblResultado = new JLabel("Ingrese las tres calificaciones.", SwingConstants.CENTER);
        lblResultado.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        lblResultado.setFont(lblResultado.getFont().deriveFont(Font.BOLD, 14f));
        add(lblResultado, BorderLayout.SOUTH);

        btnCalcular.addActionListener(e -> calcular());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void limpiar() {
        txtNota1.setText("");
        txtNota2.setText("");
        txtNota3.setText("");
        lblResultado.setForeground(Color.BLACK);
        lblResultado.setText("Ingrese las tres calificaciones.");
        txtNota1.requestFocus();
    }

  
    private void calcular() {
        
        Double n1 = validarNota(txtNota1.getText(), "Calificacion 1");
        if (n1 == null) {
            return;
        }
        Double n2 = validarNota(txtNota2.getText(), "Calificacion 2");
        if (n2 == null) {
            return;
        }
        Double n3 = validarNota(txtNota3.getText(), "Calificacion 3");
        if (n3 == null) {
            return;
        }

        
        String host = txtServidor.getText().trim();
        if (host.isEmpty()) {
            mostrarError("Debe indicar la direccion del servidor.");
            return;
        }
        int puerto;
        try {
            puerto = Integer.parseInt(txtPuerto.getText().trim());
            if (puerto < 1 || puerto > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarError("El puerto debe ser un numero entre 1 y 65535.");
            return;
        }

        
        String peticion = n1 + ";" + n2 + ";" + n3;

        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), 4000);
            socket.setSoTimeout(4000);

            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true);

            salida.println(peticion);
            String respuesta = entrada.readLine();

            if (respuesta == null) {
                mostrarError("El servidor cerro la conexion sin responder.");
                return;
            }
            procesarRespuesta(respuesta);

        } catch (UnknownHostException e) {
            mostrarError("No se encontro el servidor: " + host);
        } catch (SocketTimeoutException e) {
            mostrarError("Tiempo de espera agotado al comunicarse con el servidor.");
        } catch (ConnectException e) {
            mostrarError("No se pudo conectar. Verifique que el servidor este "
                    + "encendido en " + host + ":" + puerto + ".");
        } catch (IOException e) {
            mostrarError("Error de comunicacion con el servidor: " + e.getMessage());
        }
    }

 
    private Double validarNota(String texto, String nombre) {
        if (texto == null || texto.trim().isEmpty()) {
            mostrarError(nombre + " no puede estar vacia.");
            return null;
        }
        double valor;
        try {
            valor = Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarError(nombre + " debe ser un numero (ejemplo: 4.5).");
            return null;
        }
        if (valor < NOTA_MIN || valor > NOTA_MAX) {
            mostrarError(nombre + " debe estar entre " + NOTA_MIN + " y " + NOTA_MAX + ".");
            return null;
        }
        return valor;
    }


    private void procesarRespuesta(String respuesta) {
        String[] partes = respuesta.split(";", 2);

        if (partes.length == 2 && "OK".equals(partes[0])) {
            lblResultado.setForeground(new Color(0, 128, 0));
            lblResultado.setText("Promedio academico: " + partes[1]);
        } else if (partes.length == 2 && "ERROR".equals(partes[0])) {
            mostrarError("El servidor rechazo los datos: " + partes[1]);
        } else {
            mostrarError("Respuesta no reconocida del servidor: " + respuesta);
        }
    }

    private void mostrarError(String mensaje) {
        lblResultado.setForeground(Color.RED);
        lblResultado.setText(mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientePromedio().setVisible(true));
    }
}
