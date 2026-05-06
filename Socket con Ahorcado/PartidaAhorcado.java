import java.util.*;

public class PartidaAhorcado {

    // ─────────────────────────────────────────────
    //  BANCO DE PALABRAS
    // ─────────────────────────────────────────────
    private static final String[] PALABRAS = {
        "programacion", "servidor", "cliente", "socket", "hilo",
        "compilador", "variable", "herencia", "polimorfismo", "interfaz",
        "recursion", "algoritmo", "teclado", "pantalla", "memoria",
        "proceso", "funcion", "bucle", "arreglo", "puntero",
        "abstraccion", "encapsulamiento", "constructor", "excepcion", "iterador"
    };

    // ─────────────────────────────────────────────
    //  DIBUJO DEL AHORCADO (0 errores → 6 errores)
    // ─────────────────────────────────────────────
    private static final String[] DIBUJO = {
        "  +---+\n  |   |\n      |\n      |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n      |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n  |   |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|   |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n /    |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n / \\  |\n      |\n========="
    };

    // ─────────────────────────────────────────────
    //  ESTADO DE LA PARTIDA
    // ─────────────────────────────────────────────
    private final String palabra;
    private final char[] progreso;
    private final Set<Character> letrasUsadas;
    private int errores;
    private boolean terminada;

    private final String retador;    // quien envió el desafío (solo mira)
    private final String adivinador; // quien adivina (el que recibió el desafío)

    public PartidaAhorcado(String retador, String adivinador) {
        this.retador    = retador;
        this.adivinador = adivinador;
        this.palabra    = PALABRAS[new Random().nextInt(PALABRAS.length)];
        this.progreso   = new char[palabra.length()];
        Arrays.fill(progreso, '_');
        this.letrasUsadas = new LinkedHashSet<>();
        this.errores  = 0;
        this.terminada = false;
    }

    // ─────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────
    public String getRetador()    { return retador; }
    public String getAdivinador() { return adivinador; }
    public boolean isTerminada()  { return terminada; }
    public String getPalabra()    { return palabra; }

    // ─────────────────────────────────────────────
    //  INTENTAR UNA LETRA
    //  Devuelve mensaje de error, o null si fue ok
    // ─────────────────────────────────────────────
    public String intentarLetra(char letra) {
        letra = Character.toLowerCase(letra);

        if (!Character.isLetter(letra)) {
            return "'" + letra + "' no es una letra valida.";
        }
        if (letrasUsadas.contains(letra)) {
            return "Ya usaste la letra '" + letra + "'. Proba otra.";
        }

        letrasUsadas.add(letra);

        boolean acierto = false;
        for (int i = 0; i < palabra.length(); i++) {
            if (palabra.charAt(i) == letra) {
                progreso[i] = letra;
                acierto = true;
            }
        }

        if (!acierto) errores++;
        return null; // null = todo ok, seguir
    }

    // ─────────────────────────────────────────────
    //  INTENTAR ADIVINAR LA PALABRA COMPLETA
    // ─────────────────────────────────────────────
    public boolean intentarPalabra(String intento) {
        if (intento.toLowerCase().equals(palabra)) {
            // Revelar toda la palabra
            for (int i = 0; i < palabra.length(); i++) {
                progreso[i] = palabra.charAt(i);
            }
            terminada = true;
            return true;
        }
        errores++; // penalizar intento fallido
        return false;
    }

    // ─────────────────────────────────────────────
    //  VERIFICAR FIN DE PARTIDA
    // ─────────────────────────────────────────────
    public boolean gano() {
        for (char c : progreso) {
            if (c == '_') return false;
        }
        terminada = true;
        return true;
    }

    public boolean perdio() {
        if (errores >= 6) {
            terminada = true;
            return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────
    //  ESTADO VISUAL DEL TABLERO
    // ─────────────────────────────────────────────
    public String getEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(DIBUJO[Math.min(errores, 6)]).append("\n\n");

        sb.append("Palabra:  ");
        for (char c : progreso) {
            sb.append(c == '_' ? "_ " : c + " ");
        }
        sb.append("  (").append(palabra.length()).append(" letras)\n");

        sb.append("Letras usadas: ");
        if (letrasUsadas.isEmpty()) {
            sb.append("ninguna todavia");
        } else {
            letrasUsadas.forEach(c -> sb.append(c).append(" "));
        }
        sb.append("\n");
        sb.append("Errores: ").append(errores).append(" / 6");
        return sb.toString();
    }
}
