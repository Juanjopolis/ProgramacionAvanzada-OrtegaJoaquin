public class MainBridge {
    public static void main(String[] args) {

        Dispositivo tv = new TV();
        ControlRemoto control = new ControlAvanzado(tv);

        control.encender();
        ((ControlAvanzado) control).subirVolumen();
        control.apagar();

        System.out.println("-----");

        Dispositivo radio = new Radio();
        ControlRemoto control2 = new ControlAvanzado(radio);

        control2.encender();
        ((ControlAvanzado) control2).subirVolumen();
        control2.apagar();
    }
}