import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class Servidor {

    /** Puerto en el que el servidor escucha las conexiones entrantes. */
    private static final int PUERTO = 5000;

    /** Calificacion minima y maxima permitida (escala 0.0 - 5.0). */
    private static final double NOTA_MIN = 0.0;
    private static final double NOTA_MAX = 5.0;

    public static void main(String[] args) {
        System.out.println("=== Servidor - Promedio de tres calificaciones ===");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO + " ...");
            System.out.println("Esperando conexiones de clientes. (Ctrl+C para detener)\n");

            // El servidor atiende clientes de forma indefinida, uno tras otro.
            while (true) {
                try (Socket cliente = serverSocket.accept()) {
                    System.out.println("Cliente conectado desde: "
                            + cliente.getInetAddress().getHostAddress());
                    atenderCliente(cliente);
                } catch (IOException e) {
                    System.err.println("Error atendiendo a un cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en el puerto "
                    + PUERTO + ": " + e.getMessage());
        }
    }


    private static void atenderCliente(Socket cliente) throws IOException {
        BufferedReader entrada = new BufferedReader(
                new InputStreamReader(cliente.getInputStream()));
        PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(cliente.getOutputStream()), true);
        String linea = entrada.readLine();
        System.out.println("Datos recibidos del cliente: " + linea);

        String respuesta = procesarSolicitud(linea);
        salida.println(respuesta);

        System.out.println("Respuesta enviada al cliente: " + respuesta + "\n");
    }

    private static String procesarSolicitud(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return "ERROR;No se recibieron datos del cliente.";
        }

        String[] partes = linea.split(";");
        if (partes.length != 3) {
            return "ERROR;Se esperaban 3 calificaciones separadas por ';'.";
        }

        double[] notas = new double[3];
        for (int i = 0; i < 3; i++) {
            try {
                notas[i] = Double.parseDouble(partes[i].trim().replace(",", "."));
            } catch (NumberFormatException e) {
                return "ERROR;La calificacion " + (i + 1) + " no es un numero valido.";
            }
            if (notas[i] < NOTA_MIN || notas[i] > NOTA_MAX) {
                return "ERROR;La calificacion " + (i + 1)
                        + " debe estar entre " + NOTA_MIN + " y " + NOTA_MAX + ".";
            }
        }

      
        double promedio = (notas[0] + notas[1] + notas[2]) / 3.0;

        // Se formatea con punto decimal para que el cliente lo interprete igual.
        return "OK;" + String.format(Locale.US, "%.2f", promedio);
    }
}
