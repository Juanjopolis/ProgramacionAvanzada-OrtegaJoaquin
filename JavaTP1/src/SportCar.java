import java.util.ArrayList;
import java.util.Scanner;

public class SportCar extends Car {

    private boolean hasSpoiler;

    // Constructor
    public SportCar(int doors, int horsepower, String name, String licenseplate, boolean hasSpoiler) {
        // Call to the parent class (Car) constructor
        super(doors, horsepower, name, licenseplate);
        this.hasSpoiler = hasSpoiler;
    }

    // --- Method to add and display cars ---
    public static void manageSportCars(Scanner scanner) {
        ArrayList<SportCar> sportCarsList = new ArrayList<>();

        System.out.print("How many sport cars do you want to register?: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        // Loop to add cars
        for (int i = 0; i < quantity; i++) {
            System.out.println("\n--- Registering SportCar #" + (i + 1) + " ---");

            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("License Plate: ");
            String plate = scanner.nextLine();

            System.out.print("Doors: ");
            int doors = scanner.nextInt();

            System.out.print("HP: ");
            int hp = scanner.nextInt();

            System.out.print("Has spoiler? (true/false): ");
            boolean spoiler = scanner.nextBoolean();
            scanner.nextLine(); // Clear buffer

            sportCarsList.add(new SportCar(doors, hp, name, plate, spoiler));
        }

        // Loop to display cars
        System.out.println("\n=== SPORT CARS LIST ===");
        for (SportCar sc : sportCarsList) {
            System.out.println("Car: " + sc.getName() +
                    " | HP: " + sc.getHorsepower() +
                    " | Spoiler: " + (sc.isHasSpoiler() ? "Yes" : "No"));
        }
    }

    // --- Getter and Setter ---

    public boolean isHasSpoiler() {
        return hasSpoiler;
    }

    public void setHasSpoiler(boolean hasSpoiler) {
        this.hasSpoiler = hasSpoiler;
    }
}