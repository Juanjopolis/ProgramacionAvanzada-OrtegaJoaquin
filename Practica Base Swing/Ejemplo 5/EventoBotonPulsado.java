import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class EventoBotonPulsado implements ActionListener {

    private JButton boton;
    private int contador;

    // Colores que va rotando el botón con cada clic
    private Color[] colores = {
        new Color(52, 152, 219),   // Azul
        new Color(46, 204, 113),   // Verde
        new Color(231, 76, 60),    // Rojo
        new Color(155, 89, 182),   // Púrpura
        new Color(230, 126, 34)    // Naranja
    };

    public EventoBotonPulsado(JButton boton) {
        this.boton = boton;
        this.contador = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        contador++;

        // Cambiar color rotando entre los colores definidos
        Color colorNuevo = colores[contador % colores.length];
        boton.setBackground(colorNuevo);

        // Actualizar el texto del botón con el contador
        boton.setText("¡Gracias! Clics: " + contador);

        // También se imprime en consola
        System.out.println("Botón pulsado " + contador + " vez/veces.");
    }
}
