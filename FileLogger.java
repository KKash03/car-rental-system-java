import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class FileLogger {

    public static void logRentedCar(String carId, String customerName, String contact, String license,
                                    LocalDate startDate, LocalDate endDate, boolean insurance, double total) {
        String log = String.format("Car ID: %s, Name: %s, Contact: %s, License: %s, Start Date: %s, End Date: %s, Insurance: %s, Total: %.2f",
                carId, customerName, contact, license, startDate, endDate, insurance ? "Yes" : "No", total);
        writeToFile("rented_cars.txt", log);
    }

    public static void logReturnedCar(String carId, LocalDate returnDate) {
        String log = String.format("Car ID: %s, Returned On: %s", carId, returnDate);
        writeToFile("returned_cars.txt", log);
    }

    private static void writeToFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(content + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ": " + e.getMessage());
        }
    }
}