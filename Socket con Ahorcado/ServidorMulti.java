import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorMulti {

    // Mapa de clientes conectados: nombre -> handler
    static final Map<String, ClienteHandler> clientes = new ConcurrentHashMap<>();

    // Desafios pendientes de ahorcado: nombre del adivinador -> nombre del retador
    // Ej: {"Maria" -> "Juan"} significa que Juan desafio a Maria y ella no respondio aun
    static final Map<String, String> desafiosPendientes = new ConcurrentHashMap<>();

    // Partidas de ahorcado activas: nombre del adivinador -> partida
    // El adivinador es la clave porque es quien envia los comandos LETRA/ADIVINAR
    static final Map<String, PartidaAhorcado> partidas = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log("=== SERVIDOR MULTICLIENTE INICIADO ===");
        log("Escuchando en puerto 5000...");
        log("======================================\n");

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            while (true) {
                Socket socket = serverSocket.accept();
                log("Nueva conexion entrante desde: " + socket.getInetAddress().getHostAddress());

                ClienteHandler handler = new ClienteHandler(socket);
                Thread hilo = new Thread(handler);
                hilo.setDaemon(true);
                hilo.start();
            }
        } catch (IOException e) {
            log("ERROR en el servidor: " + e.getMessage());
        }
    }

    // Metodo de log centralizado con timestamp
    public static void log(String mensaje) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + mensaje);
    }
}
