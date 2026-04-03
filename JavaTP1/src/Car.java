
import java.util.ArrayList;
import java.util.Scanner;

public class Car {

    private int doors;
    private int horsepower;
    private String name;
    private String licenseplate;

    // Constructor
    public Car(int doors, int horsepower, String name, String licenseplate) {
        this.doors = doors;
        this.horsepower = horsepower;
        this.name = name;
        this.licenseplate = licenseplate;
    }


    // --- Getters ---

    public int getDoors() {
        return doors;
    }

    public int getHorsepower() {
        return horsepower;
    }

    public String getName() {
        return name;
    }

    public String getLicenseplate() {
        return licenseplate;
    }

    // --- Setters ---

    public void setDoors(int doors) {
        this.doors = doors;
    }

    public void setHorsepower(int horsepower) {
        this.horsepower = horsepower;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLicenseplate(String licenseplate) {
        this.licenseplate = licenseplate;
    }


    public static ArrayList<Car> createCarList(Scanner scanner) {
        ArrayList<Car> cars = new ArrayList<>();

        System.out.print("¿Cuántos autos quieres crear?: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        for (int i = 0; i < quantity; i++) {
            System.out.println("\n--- Datos del Auto " + (i + 1) + " ---");

            System.out.print("Nombre: ");
            String name = scanner.nextLine();

            System.out.print("Patente: ");
            String plate = scanner.nextLine();

            System.out.print("Puertas: ");
            int doors = scanner.nextInt();

            System.out.print("HP: ");
            int hp = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            cars.add(new Car(doors, hp, name, plate));
        }

        return cars;
    }
}
