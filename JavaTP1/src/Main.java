import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create a single Scanner instance to be shared
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Car Management System ===");

        // Call the static method from SportCar to handle logic
        SportCar.manageSportCars(scanner);

        // Close the scanner after finishing
        System.out.println("\nExiting program...");
        scanner.close();
    }
}