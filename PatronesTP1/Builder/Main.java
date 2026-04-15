public class Main {
    public static void main(String[] args) {

        // Construcción paso a paso
        Computadora pc = new Computadora.ComputadoraBuilder()
                .setCPU("Intel i7")
                .setRAM(16)
                .setGPU("RTX 4060")
                .setAlmacenamiento(1000)
                .build();

        System.out.println(pc);
    }
}