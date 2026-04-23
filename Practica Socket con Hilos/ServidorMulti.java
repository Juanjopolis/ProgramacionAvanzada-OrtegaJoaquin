import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorMulti {

    // Mapa compartido de clientes conectados: nombre -> handler
    // ConcurrentHashMap para acceso seguro desde múltiples hilos
    static final Map<String, ClienteHandler> clientes = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log("=== SERVIDOR MULTICLIENTE INICIADO ===");
        log("Escuchando en puerto 5000...");
        log("======================================\n");

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            // Loop infinito: acepta clientes de forma continua
            while (true) {
                Socket socket = serverSocket.accept();
                log("Nueva conexión entrante desde: " + socket.getInetAddress().getHostAddress());

                // Se crea y lanza un hilo por cada cliente nuevo
                ClienteHandler handler = new ClienteHandler(socket);
                Thread hilo = new Thread(handler);
                hilo.setDaemon(true); // El hilo muere si el servidor se cierra
                hilo.start();
            }
        } catch (IOException e) {
            log("ERROR en el servidor: " + e.getMessage());
        }
    }

    // Método de log centralizado con timestamp
    public static void log(String mensaje) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + mensaje);
    }
}
