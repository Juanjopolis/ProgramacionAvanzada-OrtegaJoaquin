class TV implements Dispositivo {
    public void encender() {
        System.out.println("TV encendida");
    }

    public void apagar() {
        System.out.println("TV apagada");
    }

    public void setVolumen(int volumen) {
        System.out.println("TV volumen: " + volumen);
    }
}
