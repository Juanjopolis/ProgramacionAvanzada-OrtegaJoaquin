import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;


public class PanelEscena extends JPanel implements MouseMotionListener {

    // Posición y tamaño del sol (para detectar hover)
    private final int SOL_X      = 80;
    private final int SOL_Y      = 50;
    private final int SOL_RADIO  = 50;

    // Estado: ¿el cursor está sobre el sol?
    private boolean cursorSobreSol = false;

    public PanelEscena() {
        setBackground(new Color(135, 206, 235)); // Celeste (cielo)
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Activar antialiasing para bordes suaves
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarCesped(g2);
        dibujarNubes(g2);
        dibujarCasa(g2);
        dibujarSol(g2);
    }

    private void dibujarCesped(Graphics2D g) {
        int altoPanel  = getHeight();
        int anchoPanel = getWidth();
        int alturaCesped = altoPanel / 4; // Ocupa el 25% inferior

        g.setColor(new Color(34, 139, 34)); // Verde oscuro
        g.fillRect(0, altoPanel - alturaCesped, anchoPanel, alturaCesped);

        // Franja más clara encima del césped
        g.setColor(new Color(50, 205, 50));
        g.fillRect(0, altoPanel - alturaCesped, anchoPanel, 15);
    }

    private void dibujarNubes(Graphics2D g) {
        g.setColor(Color.WHITE);

        // Nube 1
        dibujarNube(g, 220, 60);

        // Nube 2 (más pequeña)
        dibujarNube(g, 380, 40);
    }

    private void dibujarNube(Graphics2D g, int x, int y) {
        g.fillOval(x,      y,      80, 45);
        g.fillOval(x + 25, y - 20, 60, 45);
        g.fillOval(x + 50, y,      70, 40);
    }

    private void dibujarCasa(Graphics2D g) {
        int baseY  = getHeight() - getHeight() / 4; // Nivel del suelo
        int casaX  = getWidth() / 2 - 80;
        int casaW  = 160;
        int casaH  = 120;
        int casaY  = baseY - casaH;

        // --- Paredes ---
        g.setColor(new Color(210, 180, 140)); // Beige
        g.fillRect(casaX, casaY, casaW, casaH);

        // --- Techo (triángulo con polígono) ---
        g.setColor(new Color(178, 34, 34)); // Rojo ladrillo
        int[] xTecho = { casaX - 15, casaX + casaW / 2, casaX + casaW + 15 };
        int[] yTecho = { casaY, casaY - 70, casaY };
        g.fillPolygon(xTecho, yTecho, 3);

        // Borde del techo
        g.setColor(new Color(120, 20, 20));
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xTecho, yTecho, 3);

        // --- Puerta ---
        g.setColor(new Color(101, 67, 33)); // Marrón
        int puertaW = 35, puertaH = 55;
        int puertaX = casaX + casaW / 2 - puertaW / 2;
        int puertaY = casaY + casaH - puertaH;
        g.fillRect(puertaX, puertaY, puertaW, puertaH);

        // Pomo de la puerta
        g.setColor(new Color(255, 215, 0)); // Dorado
        g.fillOval(puertaX + puertaW - 10, puertaY + puertaH / 2, 6, 6);

        // --- Ventanas (izquierda y derecha) ---
        dibujarVentana(g, casaX + 15, casaY + 20);
        dibujarVentana(g, casaX + casaW - 55, casaY + 20);

        // --- Marco exterior de la casa ---
        g.setColor(new Color(160, 120, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRect(casaX, casaY, casaW, casaH);
    }

    private void dibujarVentana(Graphics2D g, int x, int y) {
        // Marco
        g.setColor(new Color(101, 67, 33));
        g.fillRect(x, y, 40, 35);

        // Vidrio
        g.setColor(new Color(173, 216, 230)); // Celeste claro
        g.fillRect(x + 3, y + 3, 34, 29);

        // Cruz de la ventana
        g.setColor(new Color(101, 67, 33));
        g.setStroke(new BasicStroke(2));
        g.drawLine(x + 20, y + 3, x + 20, y + 32);
        g.drawLine(x + 3,  y + 17, x + 37, y + 17);
    }

    private void dibujarSol(Graphics2D g) {
        int cx = SOL_X + SOL_RADIO; // Centro X
        int cy = SOL_Y + SOL_RADIO; // Centro Y

        // --- Rayos del sol ---
        g.setColor(new Color(255, 200, 0));
        g.setStroke(new BasicStroke(3));
        int numRayos = 12;
        for (int i = 0; i < numRayos; i++) {
            double angulo = Math.toRadians(i * (360.0 / numRayos));
            int x1 = (int) (cx + (SOL_RADIO + 5)  * Math.cos(angulo));
            int y1 = (int) (cy + (SOL_RADIO + 5)  * Math.sin(angulo));
            int x2 = (int) (cx + (SOL_RADIO + 22) * Math.cos(angulo));
            int y2 = (int) (cy + (SOL_RADIO + 22) * Math.sin(angulo));
            g.drawLine(x1, y1, x2, y2);
        }

        // --- Cuerpo del sol ---
        Color colorSol = cursorSobreSol
            ? new Color(255, 120, 0)   // Naranja cuando hover
            : new Color(255, 215, 0);  // Dorado normal
        g.setColor(colorSol);
        g.fillOval(SOL_X, SOL_Y, SOL_RADIO * 2, SOL_RADIO * 2);

        // Borde
        g.setColor(new Color(200, 150, 0));
        g.setStroke(new BasicStroke(2));
        g.drawOval(SOL_X, SOL_Y, SOL_RADIO * 2, SOL_RADIO * 2);

        // --- Cara del sol ---
        g.setColor(new Color(80, 40, 0)); // Marrón oscuro

        if (cursorSobreSol) {
            // Expresión SORPRENDIDA: ojos grandes + boca en "O"
            g.setStroke(new BasicStroke(2));
            g.fillOval(cx - 20, cy - 15, 12, 14); // Ojo izquierdo
            g.fillOval(cx + 8,  cy - 15, 12, 14); // Ojo derecho
            g.fillOval(cx - 8,  cy + 8,  16, 16); // Boca en "O"
        } else {
            // Expresión FELIZ: ojos normales + sonrisa
            g.setStroke(new BasicStroke(3));
            // Ojos
            g.fillOval(cx - 18, cy - 14, 10, 10);
            g.fillOval(cx + 8,  cy - 14, 10, 10);
            // Sonrisa (arco)
            g.drawArc(cx - 18, cy - 5, 36, 26, 200, 140);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int cx = SOL_X + SOL_RADIO;
        int cy = SOL_Y + SOL_RADIO;
        // Distancia del cursor al centro del sol
        double distancia = Math.sqrt(
            Math.pow(e.getX() - cx, 2) + Math.pow(e.getY() - cy, 2)
        );
        boolean estaba = cursorSobreSol;
        cursorSobreSol = (distancia <= SOL_RADIO);

        // Redibujar solo si el estado cambió
        if (estaba != cursorSobreSol) {
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) { /* no se usa */ }
}
