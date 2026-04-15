class ControlAvanzado extends ControlRemoto {

    public ControlAvanzado(Dispositivo dispositivo) {
        super(dispositivo);
    }

    public void subirVolumen() {
        dispositivo.setVolumen(10);
    }
}
