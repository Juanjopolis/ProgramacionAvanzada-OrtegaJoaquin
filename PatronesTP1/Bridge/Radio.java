class Radio implements Dispositivo {
    public void encender() {
        System.out.println("Radio encendida");
    }

    public void apagar() {
        System.out.println("Radio apagada");
    }

    public void setVolumen(int volumen) {
        System.out.println("Radio volumen: " + volumen);
    }
}
