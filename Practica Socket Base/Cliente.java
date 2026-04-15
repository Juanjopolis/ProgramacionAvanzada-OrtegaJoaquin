import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {

        try {
            System.out.println("=== CLIENTE INICIADO ===");
            System.out.println("Conectando al servidor...");

            // Se conecta al servidor en localhost puerto 5000
            Socket socket = new Socket("localhost", 5000);

            System.out.println("Conectado al servidor\n");

            // Instrucciones para el usuario
            System.out.println("=== INSTRUCCIONES ===");
            System.out.println("- Escriba cualquier mensaje para enviarlo.");
            System.out.println("- Para calcular use: RESOLVE 2+3*5");
            System.out.println("- Para salir escriba: exit");
            System.out.println("======================\n");

            // Flujo de salida hacia el servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Flujo de entrada desde el servidor
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Para leer lo que escribe el usuario
            Scanner sc = new Scanner(System.in);

            String mensaje;

            // Bucle de comunicación
            while (true) {
                System.out.print(">> ");
                mensaje = sc.nextLine();

                // Se envía el mensaje al servidor
                out.println(mensaje);

                // Se recibe la respuesta del servidor
                String respuesta = in.readLine();
                System.out.println(respuesta);

                // Si el usuario escribe exit, se termina el programa
                if (mensaje.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            // Cierre de conexión
            socket.close();
            System.out.println("Cliente desconectado.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}