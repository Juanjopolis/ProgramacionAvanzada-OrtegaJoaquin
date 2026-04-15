public class MainIterator {
    public static void main(String[] args) {

        ListaNombres lista = new ListaNombres();
        Iterador iterador = lista.crearIterador();

        while (iterador.hasNext()) {
            System.out.println(iterador.next());
        }
    }
}