import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;

public class ClienteMulti {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("╔══════════════════════════╗");
        System.out.println("║     CLIENTE DE CHAT      ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("Ingresá tu nombre de usuario: ");
        String nombreUsuario = sc.nextLine().trim();

        if (nombreUsuario.isEmpty()) {
            nombreUsuario = "Anonimo";
        }

        try {
            System.out.println("\nConectando al servidor...");
            Socket socket = new Socket("localhost", 5000);
            System.out.println("¡Conectado!\n");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // ── Primer mensaje: enviar el nombre de usuario ──
            out.println(nombreUsuario);

            // ── Hilo lector: recibe mensajes del servidor en background ──
            // Sin este hilo, el cliente quedaría bloqueado esperando escribir
            // y no podría recibir mensajes de otros clientes en tiempo real.
            Thread lector = new Thread(() -> {
                try {
                    String msgServidor;
                    while ((msgServidor = in.readLine()) != null) {
                        // Salto de línea para no pisar el prompt ">>"
                        System.out.println("\n" + msgServidor);
                        System.out.print(">> ");
                    }
                } catch (IOException e) {
                    System.out.println("\n[DESCONECTADO DEL SERVIDOR]");
                }
            });
            lector.setDaemon(true); // Muere cuando el main termina
            lector.start();

            // ── Hilo principal: lee lo que escribe el usuario y lo envía ──
            System.out.print(">> ");
            while (sc.hasNextLine()) {
                String input = sc.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.print(">> ");
                    continue;
                }

                out.println(input);

                if (input.equalsIgnoreCase("EXIT")) {
                    break;
                }

                System.out.print(">> ");
            }

            socket.close();
            System.out.println("Cliente desconectado. ¡Hasta luego!");

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
            System.out.println("Asegurate de que el servidor esté corriendo en localhost:5000");
        }
    }
}
