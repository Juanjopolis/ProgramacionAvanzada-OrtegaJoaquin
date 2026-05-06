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
    private String nombre;

    public ClienteHandler(Socket socket) {
        this.socket = socket;
    }

    public synchronized void enviar(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String nombreSolicitado = in.readLine();
            if (nombreSolicitado == null || nombreSolicitado.isBlank()) {
                nombreSolicitado = "Usuario";
            }

            nombre = asignarNombre(nombreSolicitado.trim());
            ServidorMulti.clientes.put(nombre, this);
            ServidorMulti.log("Cliente registrado como: " + nombre);

            enviarBienvenida();
            broadcast("[SERVIDOR] *** " + nombre + " se ha conectado. ***", nombre);

            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                ServidorMulti.log("[" + nombre + "]: " + mensaje);
                procesarComando(mensaje.trim());
            }

        } catch (IOException e) {
            ServidorMulti.log("Conexion interrumpida con: " + (nombre != null ? nombre : "desconocido"));
        } finally {
            desconectar();
        }
    }

    // ─────────────────────────────────────────────
    //  NOMBRE UNICO
    // ─────────────────────────────────────────────
    private String asignarNombre(String base) {
        if (!ServidorMulti.clientes.containsKey(base)) {
            return base;
        }
        int sufijo = 2;
        while (ServidorMulti.clientes.containsKey(base + "_" + sufijo)) {
            sufijo++;
        }
        String nuevo = base + "_" + sufijo;
        out.println("[SERVIDOR] El nombre '" + base + "' ya esta en uso. Se te asigno: " + nuevo);
        return nuevo;
    }

    // ─────────────────────────────────────────────
    //  MENU DE BIENVENIDA
    // ─────────────────────────────────────────────
    private void enviarBienvenida() {
        String nd = nombre.length() > 18 ? nombre.substring(0, 15) + "..." : nombre;
        enviar("╔══════════════════════════════════════════╗");
        enviar("║       BIENVENIDO AL SERVIDOR CHAT        ║");
        enviar("╠══════════════════════════════════════════╣");
        enviar("║  Usuario: " + padDer(nd, 31) + "║");
        enviar("╠══════════════════════════════════════════╣");
        enviar("║  COMANDOS:                               ║");
        enviar("║  TIME              Fecha y hora          ║");
        enviar("║  LIST              Ver clientes          ║");
        enviar("║  RESOLVE <expr>    Calcular expresion    ║");
        enviar("║  *ALL <msg>        Mensaje a todos       ║");
        enviar("║  *<usuario> <msg>  Mensaje privado       ║");
        enviar("║  AHORCADO <user>   Desafiar al ahorcado  ║");
        enviar("║  ACEPTAR / RECHAZAR  Responder desafio   ║");
        enviar("║  LETRA <c>         Adivinar una letra    ║");
        enviar("║  ADIVINAR <word>   Adivinar la palabra   ║");
        enviar("║  HELP              Mostrar este menu     ║");
        enviar("║  EXIT              Desconectarse         ║");
        enviar("╚══════════════════════════════════════════╝");
    }

    // ─────────────────────────────────────────────
    //  PROCESAMIENTO DE COMANDOS
    // ─────────────────────────────────────────────
    private void procesarComando(String mensaje) {
        if (mensaje.isEmpty()) return;

        String upper = mensaje.toUpperCase();

        if (upper.equals("EXIT")) {
            enviar("[SERVIDOR] Hasta luego, " + nombre + "!");
            desconectar();

        } else if (upper.equals("HELP")) {
            enviarBienvenida();

        } else if (upper.equals("TIME")) {
            String fh = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy  HH:mm:ss"));
            enviar("[SERVIDOR] Fecha y hora: " + fh);

        } else if (upper.equals("LIST")) {
            StringBuilder sb = new StringBuilder();
            sb.append("[SERVIDOR] Clientes conectados (").append(ServidorMulti.clientes.size()).append("):\n");
            for (String n : ServidorMulti.clientes.keySet()) {
                sb.append("  -> ").append(n);
                if (n.equals(this.nombre)) sb.append("  <- (tu)");
                // indicar si esta en partida
                if (ServidorMulti.partidas.containsKey(n)) sb.append(" [en juego]");
                sb.append("\n");
            }
            enviar(sb.toString().trim());

        } else if (upper.startsWith("RESOLVE ")) {
            String expresion = mensaje.substring(8).trim();
            if (expresion.isEmpty()) {
                enviar("[SERVIDOR] Uso: RESOLVE <expresion>");
                return;
            }
            try {
                double resultado = evaluarExpresion(expresion);
                String resStr = (resultado == Math.floor(resultado) && !Double.isInfinite(resultado))
                        ? String.valueOf((long) resultado)
                        : String.valueOf(resultado);
                enviar("[SERVIDOR] " + expresion + " = " + resStr);
            } catch (Exception e) {
                enviar("[SERVIDOR] Error: expresion matematica invalida -> '" + expresion + "'");
            }

        } else if (upper.startsWith("*ALL ")) {
            String contenido = mensaje.substring(5).trim();
            if (contenido.isEmpty()) { enviar("[SERVIDOR] Uso: *ALL <mensaje>"); return; }
            broadcast("[" + nombre + " -> TODOS]: " + contenido, nombre);
            enviar("[TU -> TODOS]: " + contenido);

        } else if (upper.startsWith("AHORCADO ")) {
            procesarDesafio(mensaje.substring(9).trim());

        } else if (upper.equals("ACEPTAR")) {
            procesarAceptar();

        } else if (upper.equals("RECHAZAR")) {
            procesarRechazar();

        } else if (upper.startsWith("LETRA ")) {
            procesarLetra(mensaje.substring(6).trim());

        } else if (upper.startsWith("ADIVINAR ")) {
            procesarAdivinar(mensaje.substring(9).trim());

        } else if (mensaje.startsWith("*")) {
            int espacio = mensaje.indexOf(' ');
            if (espacio == -1) {
                enviar("[SERVIDOR] Uso: *<usuario> <mensaje>");
                return;
            }
            String destino  = mensaje.substring(1, espacio);
            String contenido = mensaje.substring(espacio + 1).trim();
            if (contenido.isEmpty()) { enviar("[SERVIDOR] El mensaje no puede estar vacio."); return; }
            if (destino.equalsIgnoreCase(this.nombre)) { enviar("[SERVIDOR] No podes enviarte un mensaje a vos mismo."); return; }
            ClienteHandler receptor = ServidorMulti.clientes.get(destino);
            if (receptor == null) {
                enviar("[SERVIDOR] Error: el usuario '" + destino + "' no existe o no esta conectado.");
                enviar("[SERVIDOR] Clientes actuales: " + String.join(", ", ServidorMulti.clientes.keySet()));
            } else {
                receptor.enviar("[" + nombre + " -> ti]: " + contenido);
                enviar("[TU -> " + destino + "]: " + contenido);
            }

        } else {
            enviar("[SERVIDOR] Comando no reconocido: '" + mensaje + "'. Escribi HELP para ver los comandos.");
        }
    }

    // ─────────────────────────────────────────────
    //  AHORCADO: ENVIAR DESAFIO
    // ─────────────────────────────────────────────
    private void procesarDesafio(String destino) {
        if (destino.isEmpty()) {
            enviar("[SERVIDOR] Uso: AHORCADO <usuario>");
            return;
        }
        if (destino.equalsIgnoreCase(nombre)) {
            enviar("[SERVIDOR] No podes desafiarte a vos mismo.");
            return;
        }

        ClienteHandler rival = ServidorMulti.clientes.get(destino);
        if (rival == null) {
            enviar("[SERVIDOR] El usuario '" + destino + "' no esta conectado.");
            enviar("[SERVIDOR] Clientes actuales: " + String.join(", ", ServidorMulti.clientes.keySet()));
            return;
        }

        // Verificar que el rival no este ya en una partida o con un desafio pendiente
        if (ServidorMulti.partidas.containsKey(destino)) {
            enviar("[SERVIDOR] " + destino + " ya esta en una partida de ahorcado.");
            return;
        }
        if (ServidorMulti.desafiosPendientes.containsKey(destino)) {
            enviar("[SERVIDOR] " + destino + " ya tiene un desafio pendiente. Intenta mas tarde.");
            return;
        }

        // Registrar el desafio pendiente
        ServidorMulti.desafiosPendientes.put(destino, nombre);

        // Avisar al rival
        rival.enviar("\n[AHORCADO] *** " + nombre + " te desafio al ahorcado! ***");
        rival.enviar("[AHORCADO] Escribi ACEPTAR para jugar o RECHAZAR para declinar.");

        // Confirmar al retador
        enviar("[SERVIDOR] Desafio enviado a " + destino + ". Esperando respuesta...");
        ServidorMulti.log("[AHORCADO] " + nombre + " desafio a " + destino);
    }

    // ─────────────────────────────────────────────
    //  AHORCADO: ACEPTAR DESAFIO
    // ─────────────────────────────────────────────
    private void procesarAceptar() {
        // Verificar que haya un desafio pendiente para este usuario
        String retador = ServidorMulti.desafiosPendientes.get(nombre);
        if (retador == null) {
            enviar("[SERVIDOR] No tenes ningun desafio pendiente.");
            return;
        }

        ClienteHandler retadorHandler = ServidorMulti.clientes.get(retador);
        if (retadorHandler == null) {
            enviar("[SERVIDOR] " + retador + " se desconecto. El desafio fue cancelado.");
            ServidorMulti.desafiosPendientes.remove(nombre);
            return;
        }

        // Crear la partida: retador = espectador, nombre (este) = adivinador
        PartidaAhorcado partida = new PartidaAhorcado(retador, nombre);
        ServidorMulti.partidas.put(nombre, partida);
        ServidorMulti.desafiosPendientes.remove(nombre);

        ServidorMulti.log("[AHORCADO] Partida iniciada: " + retador + " vs " + nombre);

        // Notificar a ambos
        String inicio = "\n[AHORCADO] *** Partida iniciada! ***\n" +
                        "  Retador (mira):    " + retador + "\n" +
                        "  Adivinador (juega): " + nombre + "\n" +
                        "  " + nombre + ": usa LETRA <c> o ADIVINAR <palabra>\n";

        enviar(inicio);
        retadorHandler.enviar(inicio);

        // Mostrar el tablero inicial a ambos
        String estado = partida.getEstado();
        enviar(estado);
        retadorHandler.enviar(estado);
    }

    // ─────────────────────────────────────────────
    //  AHORCADO: RECHAZAR DESAFIO
    // ─────────────────────────────────────────────
    private void procesarRechazar() {
        String retador = ServidorMulti.desafiosPendientes.get(nombre);
        if (retador == null) {
            enviar("[SERVIDOR] No tenes ningun desafio pendiente.");
            return;
        }

        ServidorMulti.desafiosPendientes.remove(nombre);
        enviar("[SERVIDOR] Rechazaste el desafio de " + retador + ".");

        ClienteHandler retadorHandler = ServidorMulti.clientes.get(retador);
        if (retadorHandler != null) {
            retadorHandler.enviar("[AHORCADO] " + nombre + " rechazo tu desafio.");
        }

        ServidorMulti.log("[AHORCADO] " + nombre + " rechazo el desafio de " + retador);
    }

    // ─────────────────────────────────────────────
    //  AHORCADO: ADIVINAR UNA LETRA
    // ─────────────────────────────────────────────
    private void procesarLetra(String input) {
        PartidaAhorcado partida = ServidorMulti.partidas.get(nombre);
        if (partida == null) {
            enviar("[SERVIDOR] No estas en una partida de ahorcado.");
            return;
        }
        if (input.isEmpty() || input.length() > 1) {
            enviar("[SERVIDOR] Uso: LETRA <una sola letra>   Ej: LETRA a");
            return;
        }

        char letra = input.charAt(0);
        String error = partida.intentarLetra(letra);

        if (error != null) {
            enviar("[AHORCADO] " + error);
            return;
        }

        // Notificar a ambos el resultado
        ClienteHandler retadorHandler = ServidorMulti.clientes.get(partida.getRetador());
        String notif = "[AHORCADO] " + nombre + " probo la letra '" + letra + "'";

        enviar(notif);
        if (retadorHandler != null) retadorHandler.enviar(notif);

        // Verificar estado del juego
        if (partida.gano()) {
            terminarPartida(partida, true);
        } else if (partida.perdio()) {
            terminarPartida(partida, false);
        } else {
            // Mostrar tablero actualizado a ambos
            String estado = partida.getEstado();
            enviar(estado);
            if (retadorHandler != null) retadorHandler.enviar(estado);
        }
    }

    // ─────────────────────────────────────────────
    //  AHORCADO: ADIVINAR PALABRA COMPLETA
    // ─────────────────────────────────────────────
    private void procesarAdivinar(String intento) {
        PartidaAhorcado partida = ServidorMulti.partidas.get(nombre);
        if (partida == null) {
            enviar("[SERVIDOR] No estas en una partida de ahorcado.");
            return;
        }
        if (intento.isEmpty()) {
            enviar("[SERVIDOR] Uso: ADIVINAR <palabra>   Ej: ADIVINAR computadora");
            return;
        }

        ClienteHandler retadorHandler = ServidorMulti.clientes.get(partida.getRetador());
        String notif = "[AHORCADO] " + nombre + " intento adivinar: '" + intento + "'";
        enviar(notif);
        if (retadorHandler != null) retadorHandler.enviar(notif);

        boolean acierto = partida.intentarPalabra(intento);

        if (acierto) {
            terminarPartida(partida, true);
        } else {
            String fallo = "[AHORCADO] Incorrecto! Perdiste un intento.";
            enviar(fallo);
            if (retadorHandler != null) retadorHandler.enviar(fallo);

            if (partida.perdio()) {
                terminarPartida(partida, false);
            } else {
                String estado = partida.getEstado();
                enviar(estado);
                if (retadorHandler != null) retadorHandler.enviar(estado);
            }
        }
    }

    // ─────────────────────────────────────────────
    //  AHORCADO: FIN DE PARTIDA
    // ─────────────────────────────────────────────
    private void terminarPartida(PartidaAhorcado partida, boolean gano) {
        ClienteHandler retadorHandler = ServidorMulti.clientes.get(partida.getRetador());

        // Mostrar tablero final a ambos
        String estadoFinal = partida.getEstado();
        enviar(estadoFinal);
        if (retadorHandler != null) retadorHandler.enviar(estadoFinal);

        String resultado;
        if (gano) {
            resultado = "\n[AHORCADO] *** " + nombre + " GANO! Adivino la palabra: '"
                        + partida.getPalabra() + "' ***";
        } else {
            resultado = "\n[AHORCADO] *** " + nombre + " PERDIO. La palabra era: '"
                        + partida.getPalabra() + "' ***";
        }

        enviar(resultado);
        if (retadorHandler != null) retadorHandler.enviar(resultado);

        enviar("[AHORCADO] Fin de la partida. Podes seguir chateando normalmente.");
        if (retadorHandler != null) retadorHandler.enviar("[AHORCADO] Fin de la partida.");

        // Limpiar la partida del mapa
        ServidorMulti.partidas.remove(nombre);
        ServidorMulti.log("[AHORCADO] Partida finalizada entre " + partida.getRetador() + " y " + nombre);
    }

    // ─────────────────────────────────────────────
    //  BROADCAST
    // ─────────────────────────────────────────────
    private void broadcast(String mensaje, String excepto) {
        for (Map.Entry<String, ClienteHandler> entry : ServidorMulti.clientes.entrySet()) {
            if (!entry.getKey().equals(excepto)) {
                entry.getValue().enviar(mensaje);
            }
        }
    }

    // ─────────────────────────────────────────────
    //  DESCONEXION
    // ─────────────────────────────────────────────
    private void desconectar() {
        if (nombre != null && ServidorMulti.clientes.containsKey(nombre)) {
            ServidorMulti.clientes.remove(nombre);
            ServidorMulti.log("Cliente desconectado: " + nombre);
            broadcast("[SERVIDOR] *** " + nombre + " se ha desconectado. ***", nombre);

            // Si estaba en una partida, avisarle al rival y limpiar
            PartidaAhorcado partida = ServidorMulti.partidas.remove(nombre);
            if (partida != null) {
                ClienteHandler retador = ServidorMulti.clientes.get(partida.getRetador());
                if (retador != null) {
                    retador.enviar("[AHORCADO] " + nombre + " se desconecto. La partida fue cancelada.");
                }
            }

            // Limpiar desafios pendientes donde este usuario era el adivinador
            ServidorMulti.desafiosPendientes.remove(nombre);
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
    //  EVALUADOR DE EXPRESIONES MATEMATICAS
    // ─────────────────────────────────────────────
    public static double evaluarExpresion(String expresion) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < expresion.length()) ? expresion.charAt(pos) : -1; }
            boolean eat(int c) {
                while (ch == ' ') nextChar();
                if (ch == c) { nextChar(); return true; }
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
                        double d = parseFactor();
                        if (d == 0) throw new RuntimeException("Division por cero");
                        x /= d;
                    }
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return  parseFactor();
                if (eat('-')) return -parseFactor();
                double x; int startPos = this.pos;
                if (eat('(')) { x = parseExpression(); if (!eat(')')) throw new RuntimeException("Parentesis sin cerrar"); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expresion.substring(startPos, this.pos));
                } else { throw new RuntimeException("Caracter inesperado: " + (char) ch); }
                if (eat('^')) x = Math.pow(x, parseFactor());
                return x;
            }
        }.parse();
    }
}
