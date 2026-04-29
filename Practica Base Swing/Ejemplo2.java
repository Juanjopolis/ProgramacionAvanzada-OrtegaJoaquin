import javax.swing.*;
import java.awt.*;

public class Ejemplo2 extends JFrame {
    public Ejemplo2(){

        super("Formulario de Saludo");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Container cp = getContentPane();

        cp.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel etiqueta = new JLabel("Escribe tu nombre: ");
        JTextField campoTexto = new JTextField(15);
        JButton boton = new JButton("Saludar");

        campoTexto.setToolTipText("Introduce aquí tu nombre");
        boton.setToolTipText("Haz clic para recibir un saludo");

        boton.setBackground(new Color(100, 200, 100));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cp.add(etiqueta);
        cp.add(campoTexto);
        cp.add(boton);
    }

    public static void main(String[] args){
        Ejemplo2 ventana = new Ejemplo2();
        ventana.setVisible(true);
    }
}
