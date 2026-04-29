import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Ejemplo4 extends JFrame{
    public Ejemplo4(){
        super("Registro de Usuarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);

        //MEJORA
        JPanel contenedorPrincipal = new JPanel(new BorderLayout(10, 10));
        contenedorPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contenedorPrincipal);

        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JTextField txtDia = new JTextField(2);
        JTextField txtMes = new JTextField(2);
        JTextField txtAnio = new JTextField(4);

        panelFecha.add(txtDia);
        panelFecha.add(new JLabel("/"));
        panelFecha.add(txtMes);
        panelFecha.add(new JLabel("/"));
        panelFecha.add(txtAnio);

        JPanel panelDatos = new JPanel(new GridLayout(3, 2, 0, 10));
        panelDatos.add(new JLabel("Nombre Completo:"));
        panelDatos.add(new JTextField(15));
        panelDatos.add(new JLabel("DNI / Identificación:"));
        panelDatos.add(new JTextField(15));
        panelDatos.add(new JLabel("Fecha de nacimiento:"));
        panelDatos.add(panelFecha);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.setPreferredSize(new Dimension(100, 30));
        btnCancelar.setPreferredSize(new Dimension(100, 30));

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelDatos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public static void  main(String[] args){
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch(Exception e) {}
        new Ejemplo4().setVisible(true);
    }
}
