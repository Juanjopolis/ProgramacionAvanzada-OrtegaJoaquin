import javax.swing.*;
import java.awt.*;

public class Ejemplo3 extends JFrame {
    public Ejemplo3(){
        super("Teclado Numérico");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Pantalla tipo calculadora
        JTextField pantalla = new JTextField("0");
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setFont(new Font("Monospaced", Font.BOLD, 24));
        pantalla.setEditable(false);
        add(pantalla, BorderLayout.NORTH);

        // Crear un panel para los botones con GridLayout
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4, 3, 5, 5));

        // Añadir botones del 1 al 9 usando un bucle
        for (int i = 1; i <= 9; i++) {
            JButton boton = new JButton(String.valueOf(i));
            formatearBoton(boton);
            panelBotones.add(boton);
        }

        //Añadir los botones especiales (*, 0, #)
        JButton bAste = new JButton("*");
        JButton bCero = new JButton("0");
        JButton bHash = new JButton("#");

        formatearBoton(bAste);
        formatearBoton(bCero);
        formatearBoton(bHash);

        panelBotones.add(bAste);
        panelBotones.add(bCero);
        panelBotones.add(bHash);

        // Añadir el panel de botones al centro de la ventana
        add(panelBotones, BorderLayout.CENTER);
    }

    // MEJORA
    private void formatearBoton(JButton b) {
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setBackground(Color.WHITE);
        b.setFocusable(false);
    }

    public static void main(String[] args){
        Ejemplo3 ventana = new Ejemplo3();
        ventana.setVisible(true);
    }
}
