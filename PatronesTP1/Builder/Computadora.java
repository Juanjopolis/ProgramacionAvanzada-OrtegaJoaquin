public class Computadora {
    private String cpu;
    private int ram;
    private String gpu;
    private int almacenamiento;

    // Constructor privado → solo el builder puede crear objetos
    private Computadora(ComputadoraBuilder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.gpu = builder.gpu;
        this.almacenamiento = builder.almacenamiento;
    }

    @Override
    public String toString() {
        return "Computadora:\n" +
                "CPU: " + cpu + "\n" +
                "RAM: " + ram + " GB\n" +
                "GPU: " + gpu + "\n" +
                "Almacenamiento: " + almacenamiento + " GB\n";
    }

    // 🔧 BUILDER
    public static class ComputadoraBuilder {
        private String cpu;
        private int ram;
        private String gpu;
        private int almacenamiento;

        public ComputadoraBuilder setCPU(String cpu) {
            this.cpu = cpu;
            return this; // permite encadenar métodos
        }

        public ComputadoraBuilder setRAM(int ram) {
            this.ram = ram;
            return this;
        }

        public ComputadoraBuilder setGPU(String gpu) {
            this.gpu = gpu;
            return this;
        }

        public ComputadoraBuilder setAlmacenamiento(int almacenamiento) {
            this.almacenamiento = almacenamiento;
            return this;
        }

        // Metodo final que construye el objeto
        public Computadora build() {
            return new Computadora(this);
        }
    }
}
