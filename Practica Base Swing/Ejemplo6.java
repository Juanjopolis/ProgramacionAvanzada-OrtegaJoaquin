import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Ejemplo6 extends JFrame implements ActionListener {

    // --- Componentes que necesitamos acceder desde actionPerformed ---
    private JTextField campoNombre;
    private JTextField campoApellido;
    private JButton botonSaludar;
    private JButton botonLimpiar;

    public Ejemplo6() {
        // --- Configuración general de la ventana ---
        setTitle("Ejemplo 6: Saludo Interactivo");
        setSize(420, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- Panel principal con fondo oscuro ---
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(44, 62, 80));
        panelPrincipal.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 18));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ---- Fuente base ----
        Font fuenteLabel  = new Font("Arial", Font.BOLD, 14);
        Font fuenteCampo  = new Font("Arial", Font.PLAIN, 14);
        Color colorTexto  = Color.WHITE;

        // --- Fila 1: Nombre ---
        JLabel labelNombre = new JLabel("Nombre:");
        labelNombre.setFont(fuenteLabel);
        labelNombre.setForeground(colorTexto);

        campoNombre = new JTextField(12);
        campoNombre.setFont(fuenteCampo);
        campoNombre.setPreferredSize(new Dimension(160, 30));
        // También saludar al presionar Enter en el campo
        campoNombre.addActionListener(this);

        // --- Fila 2: Apellido ---
        JLabel labelApellido = new JLabel("Apellido:");
        labelApellido.setFont(fuenteLabel);
        labelApellido.setForeground(colorTexto);

        campoApellido = new JTextField(12);
        campoApellido.setFont(fuenteCampo);
        campoApellido.setPreferredSize(new Dimension(160, 30));
        campoApellido.addActionListener(this);

        // --- Botón Saludar ---
        botonSaludar = new JButton("Saludar 👋");
        botonSaludar.setFont(new Font("Arial", Font.BOLD, 14));
        botonSaludar.setBackground(new Color(46, 204, 113));  // Verde
        botonSaludar.setForeground(Color.WHITE);
        botonSaludar.setFocusPainted(false);
        botonSaludar.setBorderPainted(false);
        botonSaludar.setOpaque(true);
        botonSaludar.setPreferredSize(new Dimension(140, 36));
        botonSaludar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Registramos el ActionListener -> ¡somos nosotros mismos!
        botonSaludar.addActionListener(this);

        // --- Botón Limpiar ---
        botonLimpiar = new JButton("Limpiar 🧹");
        botonLimpiar.setFont(new Font("Arial", Font.BOLD, 14));
        botonLimpiar.setBackground(new Color(231, 76, 60));   // Rojo
        botonLimpiar.setForeground(Color.WHITE);
        botonLimpiar.setFocusPainted(false);
        botonLimpiar.setBorderPainted(false);
        botonLimpiar.setOpaque(true);
        botonLimpiar.setPreferredSize(new Dimension(140, 36));
        botonLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonLimpiar.addActionListener(this);

        // --- Agregar todo al panel ---
        panelPrincipal.add(labelNombre);
        panelPrincipal.add(campoNombre);
        panelPrincipal.add(labelApellido);
        panelPrincipal.add(campoApellido);
        panelPrincipal.add(botonSaludar);
        panelPrincipal.add(botonLimpiar);

        add(panelPrincipal);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // ¿El evento vino del botón Saludar o de un campo (Enter)?
        if (e.getSource() == botonSaludar
                || e.getSource() == campoNombre
                || e.getSource() == campoApellido) {

            String nombre   = campoNombre.getText().trim();
            String apellido = campoApellido.getText().trim();

            // Validación: al menos el nombre debe estar completo
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Por favor, ingresá al menos tu nombre.",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE
                );
                campoNombre.requestFocus(); // Devolver el foco al campo
                return;
            }

            // Armar el saludo completo
            String nombreCompleto = apellido.isEmpty() ? nombre : nombre + " " + apellido;

            JOptionPane.showMessageDialog(
                this,
                "¡Hola, " + nombreCompleto + "! 😊",
                "Saludo",
                JOptionPane.INFORMATION_MESSAGE
            );

        // ¿El evento vino del botón Limpiar?
        } else if (e.getSource() == botonLimpiar) {
            campoNombre.setText("");
            campoApellido.setText("");
            campoNombre.requestFocus(); // Devolver foco al primer campo
        }
    }

    // --- Punto de entrada ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ejemplo6());
    }
}
