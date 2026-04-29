import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Ejemplo7 extends JFrame implements ActionListener {

    private JButton btnInformacion;
    private JButton btnAdvertencia;
    private JButton btnError;
    private JButton btnEntrada;
    private JButton btnConfirmacion;

    public Ejemplo7() {
        setTitle("Ejemplo 7: Cuadros de Diálogo");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- Panel principal ---
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(44, 62, 80));
        panelPrincipal.setLayout(new GridLayout(6, 1, 10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- Título ---
        JLabel titulo = new JLabel("Elegí un tipo de diálogo", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        // --- Crear botones ---
        btnInformacion  = crearBoton("ℹ️  Información",   new Color(52, 152, 219));
        btnAdvertencia  = crearBoton("⚠️  Advertencia",   new Color(230, 126, 34));
        btnError        = crearBoton("❌  Error",          new Color(231, 76, 60));
        btnEntrada      = crearBoton("✏️  Entrada de texto", new Color(155, 89, 182));
        btnConfirmacion = crearBoton("❓  Confirmación",   new Color(46, 204, 113));

        // --- Agregar al panel ---
        panelPrincipal.add(titulo);
        panelPrincipal.add(btnInformacion);
        panelPrincipal.add(btnAdvertencia);
        panelPrincipal.add(btnError);
        panelPrincipal.add(btnEntrada);
        panelPrincipal.add(btnConfirmacion);

        add(panelPrincipal);
        setVisible(true);
    }

    // --- Método auxiliar para crear botones con estilo uniforme ---
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(300, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // --- 1. Diálogo de INFORMACIÓN ---
        if (e.getSource() == btnInformacion) {
            JOptionPane.showMessageDialog(
                this,
                "Esta es una ventana de información.\nAquí podés mostrar mensajes al usuario.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE
            );

        // --- 2. Diálogo de ADVERTENCIA ---
        } else if (e.getSource() == btnAdvertencia) {
            JOptionPane.showMessageDialog(
                this,
                "¡Atención! Esta acción puede tener consecuencias.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE
            );

        // --- 3. Diálogo de ERROR ---
        } else if (e.getSource() == btnError) {
            JOptionPane.showMessageDialog(
                this,
                "Ocurrió un error inesperado.\nPor favor, intentá de nuevo.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

        // --- 4. Diálogo de ENTRADA DE TEXTO ---
        } else if (e.getSource() == btnEntrada) {
            String nombre = JOptionPane.showInputDialog(
                this,
                "¿Cuál es tu nombre?",
                "Entrada de datos",
                JOptionPane.QUESTION_MESSAGE
            );

            // Verificar que el usuario no canceló ni dejó vacío
            if (nombre != null && !nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "¡Hola, " + nombre.trim() + "! Bienvenido/a 😊",
                    "Respuesta",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else if (nombre != null) {
                // Presionó OK pero sin escribir nada
                JOptionPane.showMessageDialog(
                    this,
                    "No ingresaste ningún nombre.",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE
                );
            }
            // Si nombre == null, el usuario canceló → no hacemos nada

        // --- 5. Diálogo de CONFIRMACIÓN (Sí / No) ---
        } else if (e.getSource() == btnConfirmacion) {
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que querés continuar?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                    this,
                    "✅ Confirmado. ¡Seguimos adelante!",
                    "Aceptado",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "❎ Operación cancelada.",
                    "Cancelado",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ejemplo7());
    }
}
