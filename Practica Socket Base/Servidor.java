import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Servidor {
    public static void main(String[] args) {

        try {
            // Mensaje de inicio
            System.out.println("=== SERVIDOR INICIADO ===");
            System.out.println("Esperando cliente...");

            // Se crea el servidor en el puerto 5000
            ServerSocket server = new ServerSocket(5000);

            // El servidor queda bloqueado hasta que un cliente se conecta
            Socket client = server.accept();

            System.out.println("Cliente conectado");

            // Flujo de entrada: para leer lo que envía el cliente
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            // Flujo de salida: para enviar mensajes al cliente
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            String mensaje;

            // Bucle principal de comunicación
            while ((mensaje = in.readLine()) != null) {

                // Log en consola del servidor
                System.out.println("Cliente: " + mensaje);

                // Si el cliente escribe "exit", se corta la conexión
                if (mensaje.equalsIgnoreCase("exit")) {
                    out.println("Conexión cerrada por el cliente.");
                    break;
                }

                // Si el mensaje comienza con RESOLVE, se interpreta como operación matemática
                if (mensaje.startsWith("RESOLVE")) {
                    try {
                        // Se extrae la operación (ej: "2+3*5")
                        String operacion = mensaje.substring(8).trim();

                        // Se calcula el resultado
                        double resultado = evaluarExpresion(operacion);

                        // Se envía el resultado al cliente
                        out.println("Resultado: " + resultado);
                    } catch (Exception e) {
                        // Manejo de errores en la operación
                        out.println("Error: operación inválida.");
                    }
                } else {
                    // Respuesta normal (eco del mensaje)
                    out.println("Servidor recibió: " + mensaje);
                }
            }

            // Cierre de conexiones
            client.close();
            server.close();

            System.out.println("Servidor cerrado.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método que evalúa expresiones matemáticas simples
    public static double evaluarExpresion(String expresion) {
        return new Object() {
            int pos = -1, ch;

            // Avanza al siguiente carácter
            void nextChar() {
                ch = (++pos < expresion.length()) ? expresion.charAt(pos) : -1;
            }

            // Verifica y consume un carácter específico
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            // Método principal de parseo
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expresion.length()) throw new RuntimeException("Error");
                return x;
            }

            // Maneja suma y resta
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            // Maneja multiplicación y división
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            // Maneja números, paréntesis y signos
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expresion.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Error");
                }

                return x;
            }
        }.parse();
    }
}