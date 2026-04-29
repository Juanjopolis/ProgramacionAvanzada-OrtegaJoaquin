import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class BotonVentana extends JFrame {

    private JButton boton;

    public BotonVentana() {
        // --- Configuración de la ventana ---
        setTitle("Ejemplo 5: Manejo de Eventos");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en pantalla
        setResizable(false);

        // --- Panel de fondo ---
        JPanel panel = new JPanel();
        panel.setBackground(new Color(44, 62, 80)); // Fondo oscuro
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        // --- Configuración del botón ---
        boton = new JButton("¡Púlsame!");
        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.setBackground(new Color(52, 152, 219)); // Azul inicial
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);       // Sin borde al enfocar
        boton.setBorderPainted(false);      // Sin borde visible
        boton.setOpaque(true);              // Necesario para que se vea el color en Mac/Linux
        boton.setPreferredSize(new Dimension(220, 60));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mano

        // --- Registrar el escuchador de eventos ---
        EventoBotonPulsado escuchador = new EventoBotonPulsado(boton);
        boton.addActionListener(escuchador);

        // --- Agregar componentes ---
        panel.add(boton);
        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Ejecutar en el hilo de eventos de Swing (buena práctica)
        SwingUtilities.invokeLater(() -> new BotonVentana());
    }
}
