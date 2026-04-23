import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ClienteHandler implements Runnable {

    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nombre; // Nombre asignado por el servidor

    public ClienteHandler(Socket socket) {
        this.socket = socket;
    }

    // Envía un mensaje a ESTE cliente
    public synchronized void enviar(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    @Override
    public void run() {
        try {
            // Inicializar flujos de entrada/salida
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 1) Leer nombre de usuario enviado por el cliente
            String nombreSolicitado = in.readLine();
            if (nombreSolicitado == null || nombreSolicitado.isBlank()) {
                nombreSolicitado = "Usuario";
            }

            // 2) Asignar nombre único
            nombre = asignarNombre(nombreSolicitado.trim());

            // 3) Registrar al cliente en el mapa compartido
            ServidorMulti.clientes.put(nombre, this);
            ServidorMulti.log("Cliente registrado como: " + nombre);

            // 4) Enviar menú de bienvenida
            enviarBienvenida();

            // 5) Notificar al resto que alguien se conectó
            broadcast("[SERVIDOR] *** " + nombre + " se ha conectado. ***", nombre);

            // 6) Bucle principal: leer y procesar comandos
            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                ServidorMulti.log("[" + nombre + "]: " + mensaje);
                procesarComando(mensaje.trim());
            }

        } catch (IOException e) {
            ServidorMulti.log("Conexión interrumpida con: " + (nombre != null ? nombre : "desconocido"));
        } finally {
            desconectar();
        }
    }

    // ─────────────────────────────────────────────
    //  ASIGNACIÓN DE NOMBRE ÚNICO
    // ─────────────────────────────────────────────
    private String asignarNombre(String base) {
        if (!ServidorMulti.clientes.containsKey(base)) {
            return base; // Nombre disponible, se usa tal cual
        }

        // Si ya existe, agregar sufijo numérico: Juan_2, Juan_3, ...
        int sufijo = 2;
        while (ServidorMulti.clientes.containsKey(base + "_" + sufijo)) {
            sufijo++;
        }
        String nuevo = base + "_" + sufijo;

        // Avisar al cliente que su nombre fue cambiado
        out.println("[SERVIDOR] El nombre '" + base + "' ya está en uso. Se te asignó: " + nuevo);
        return nuevo;
    }

    // ─────────────────────────────────────────────
    //  MENÚ DE BIENVENIDA
    // ─────────────────────────────────────────────
    private void enviarBienvenida() {
        String nombreDisplay = nombre.length() > 18
                ? nombre.substring(0, 15) + "..."
                : nombre;

        enviar("╔══════════════════════════════════════════╗");
        enviar("║       BIENVENIDO AL SERVIDOR CHAT        ║");
        enviar("╠══════════════════════════════════════════╣");
        enviar("║  Usuario asignado: " + padDer(nombreDisplay, 22) + "║");
        enviar("╠══════════════════════════════════════════╣");
        enviar("║  COMANDOS DISPONIBLES:                   ║");
        enviar("║  TIME            Fecha y hora actual     ║");
        enviar("║  LIST            Ver clientes conectados ║");
        enviar("║  RESOLVE <expr>  Calcular expresión mat. ║");
        enviar("║  *ALL <msg>      Mensaje a todos         ║");
        enviar("║  *<usuario> <msg>  Mensaje privado       ║");
        enviar("║  HELP            Mostrar este menú       ║");
        enviar("║  EXIT            Desconectarse           ║");
        enviar("╚══════════════════════════════════════════╝");
    }

    // ─────────────────────────────────────────────
    //  PROCESAMIENTO DE COMANDOS
    // ─────────────────────────────────────────────
    private void procesarComando(String mensaje) {
        if (mensaje.isEmpty()) return;

        String upper = mensaje.toUpperCase();

        // EXIT: cerrar conexión
        if (upper.equals("EXIT")) {
            enviar("[SERVIDOR] Hasta luego, " + nombre + "! Conexión cerrada.");
            desconectar();

        // HELP: mostrar menú de nuevo
        } else if (upper.equals("HELP")) {
            enviarBienvenida();

        // TIME: fecha y hora actual
        } else if (upper.equals("TIME")) {
            String fechaHora = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy  HH:mm:ss"));
            enviar("[SERVIDOR] Fecha y hora: " + fechaHora);

        // LIST: listar clientes conectados
        } else if (upper.equals("LIST")) {
            StringBuilder sb = new StringBuilder();
            sb.append("[SERVIDOR] Clientes conectados (").append(ServidorMulti.clientes.size()).append("):\n");
            for (String n : ServidorMulti.clientes.keySet()) {
                sb.append("  → ").append(n);
                if (n.equals(this.nombre)) sb.append("  ← (tú)");
                sb.append("\n");
            }
            enviar(sb.toString().trim());

        // RESOLVE: calcular expresión matemática
        } else if (upper.startsWith("RESOLVE ")) {
            String expresion = mensaje.substring(8).trim();
            if (expresion.isEmpty()) {
                enviar("[SERVIDOR] Uso: RESOLVE <expresión>   Ej: RESOLVE 2+3*5");
                return;
            }
            try {
                double resultado = evaluarExpresion(expresion);
                // Mostrar sin decimales si el resultado es entero
                String resStr = (resultado == Math.floor(resultado) && !Double.isInfinite(resultado))
                        ? String.valueOf((long) resultado)
                        : String.valueOf(resultado);
                enviar("[SERVIDOR] " + expresion + " = " + resStr);
            } catch (Exception e) {
                enviar("[SERVIDOR] Error: expresión matemática inválida → '" + expresion + "'");
            }

        // *ALL: broadcast a todos excepto el emisor
        } else if (upper.startsWith("*ALL ")) {
            String contenido = mensaje.substring(5).trim();
            if (contenido.isEmpty()) {
                enviar("[SERVIDOR] Uso: *ALL <mensaje>");
                return;
            }
            broadcast("[" + nombre + " → TODOS]: " + contenido, nombre);
            enviar("[TÚ → TODOS]: " + contenido);

        // *usuario: mensaje privado
        } else if (mensaje.startsWith("*")) {
            int espacio = mensaje.indexOf(' ');
            if (espacio == -1) {
                enviar("[SERVIDOR] Uso: *<usuario> <mensaje>   Ej: *Juan Hola!");
                return;
            }
            String destino  = mensaje.substring(1, espacio);
            String contenido = mensaje.substring(espacio + 1).trim();

            if (contenido.isEmpty()) {
                enviar("[SERVIDOR] El mensaje no puede estar vacío.");
                return;
            }

            if (destino.equalsIgnoreCase(this.nombre)) {
                enviar("[SERVIDOR] No podés enviarte un mensaje a vos mismo.");
                return;
            }

            ClienteHandler receptor = ServidorMulti.clientes.get(destino);
            if (receptor == null) {
                // El destinatario no existe: avisar al emisor
                enviar("[SERVIDOR] Error: el usuario '" + destino + "' no existe o no está conectado.");
                enviar("[SERVIDOR] Clientes actuales: " + String.join(", ", ServidorMulti.clientes.keySet()));
            } else {
                receptor.enviar("[" + nombre + " → ti]: " + contenido);
                enviar("[TÚ → " + destino + "]: " + contenido);
            }

        // Comando desconocido
        } else {
            enviar("[SERVIDOR] Comando no reconocido: '" + mensaje + "'. Escribí HELP para ver los comandos.");
        }
    }

    // ─────────────────────────────────────────────
    //  BROADCAST: enviar a todos excepto uno
    // ─────────────────────────────────────────────
    private void broadcast(String mensaje, String excepto) {
        for (Map.Entry<String, ClienteHandler> entry : ServidorMulti.clientes.entrySet()) {
            if (!entry.getKey().equals(excepto)) {
                entry.getValue().enviar(mensaje);
            }
        }
    }

    // ─────────────────────────────────────────────
    //  DESCONEXIÓN
    // ─────────────────────────────────────────────
    private void desconectar() {
        if (nombre != null && ServidorMulti.clientes.containsKey(nombre)) {
            ServidorMulti.clientes.remove(nombre);
            ServidorMulti.log("Cliente desconectado: " + nombre);
            broadcast("[SERVIDOR] *** " + nombre + " se ha desconectado. ***", nombre);
        }
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            // ignorar
        }
    }

    // ─────────────────────────────────────────────
    //  UTILIDADES
    // ─────────────────────────────────────────────
    private String padDer(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    // ─────────────────────────────────────────────
    //  EVALUADOR DE EXPRESIONES MATEMÁTICAS
    //  (tomado y mejorado de la versión original)
    // ─────────────────────────────────────────────
    public static double evaluarExpresion(String expresion) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expresion.length()) ? expresion.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expresion.length()) throw new RuntimeException("Token inesperado");
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new RuntimeException("División por cero");
                        x /= divisor;
                    }
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return  parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Paréntesis sin cerrar");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expresion.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Carácter inesperado: " + (char) ch);
                }

                // Potencia: 2^3
                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}
