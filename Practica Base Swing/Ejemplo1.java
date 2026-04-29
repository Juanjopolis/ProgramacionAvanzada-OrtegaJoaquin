import javax.swing.JFrame;
import java.awt.Color;

public class Ejemplo1 extends JFrame {
    public Ejemplo1() {
        super("Mi primera ventana");

        setSize(400,300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(220,230,240));

        setResizable(false);
    }

    public static void main(String[] args){
        Ejemplo1 ventana = new Ejemplo1();
        ventana.setVisible(true);
    }
}
