import java.util.Random;

public class NumeroAleatorios {

    public static void main(String[] args) {
        final int CANTIDAD = 500;
        final int MIN = 10;
        final int MAX = 1000;

        int[] numeros = new int[CANTIDAD];
        Random random = new Random();

        for (int i = 0; i < CANTIDAD; i++) {
            numeros[i] = random.nextInt(MAX - MIN + 1) + MIN;
        }

        long sumaTotal = 0;
        for (int numero : numeros) {
            sumaTotal += numero;
        }

        double promedio = (double) sumaTotal / CANTIDAD;

        System.out.println("=== Resultados ===");
        System.out.println("Cantidad de números generados: " + CANTIDAD);
        System.out.println("Suma total: " + sumaTotal);
        System.out.printf("Promedio: %.2f%n", promedio);
    }
}


/*=== Resultados ===
Cantidad de números generados: 500
Suma total: 253346
Promedio: 506.69*/