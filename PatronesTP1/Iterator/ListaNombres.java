class ListaNombres {
    private String[] nombres = {"Juan", "Ana", "Pedro", "Lucia"};

    public Iterador crearIterador() {
        return new IteradorNombres();
    }

    // Implementación del iterador
    private class IteradorNombres implements Iterador {
        int posicion = 0;

        public boolean hasNext() {
            return posicion < nombres.length;
        }

        public String next() {
            return nombres[posicion++];
        }
    }
}