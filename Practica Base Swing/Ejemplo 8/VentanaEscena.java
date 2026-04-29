import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class VentanaEscena extends JFrame {

    public VentanaEscena() {
        setTitle("Ejemplo 8: Escena Gráfica Interactiva");
        setSize(550, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Agregar el panel con toda la escena
        add(new PanelEscena());

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaEscena());
    }
}
