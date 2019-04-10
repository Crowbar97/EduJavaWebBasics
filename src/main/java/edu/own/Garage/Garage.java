package edu.own.Garage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Garage simple model
 */
public class Garage {
    /**
     * path to .json file stored possible car enumeration
     */
    private String availCarsFilePath;
    /**
     * contains possible cars loaded from .json file
     * pairs of (marks, models)
     */
    private Map<String, Set<String>> availCars;
    /**
     * contains own cars
     * pairs of (marks, (model, count))
     */
    private Map<String, Map<String, Integer>> ownCars;

    public Garage(String carAvailFilePath) throws IOException {
        this.availCarsFilePath = carAvailFilePath;
        ownCars = new HashMap<>();
        loadAvailCars();
    }

    private void loadAvailCars() throws IOException {
        FileReader reader = new FileReader(availCarsFilePath);
        ObjectMapper mapper = new ObjectMapper();
        availCars = mapper.readValue(reader, mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Set.class));
        reader.close();
        if (availCars.isEmpty())
            throw new IllegalArgumentException("Available cars list must have at least one car!");
    }
    private void saveAvailCars() throws IOException {
        FileWriter writer = new FileWriter(availCarsFilePath);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(writer, availCars);
        writer.close();
    }

    private void printHelp() {
        System.out.println("--- Garage Usage ---");
        System.out.println("- help: views this help");
        System.out.println("- show: shows own cars");
        System.out.println("- add: adds new car");
        System.out.println("- remove: removes specified car");
        System.out.println("- exit: closes your garage");
    }
    private void showAvailCars() {
        if (availCars.isEmpty()) {
            System.out.println("There are no available cars!");
            return;
        }

        for (Map.Entry<String, Set<String>> mark : availCars.entrySet())
            System.out.println(mark.getKey() + ": " + mark.getValue());
    }
    private void showOwnCars() {
        if (ownCars.isEmpty()) {
            System.out.println("Your garage is empty!");
            return;
        }

        for (Map.Entry<String, Map<String, Integer>> mark : ownCars.entrySet())
            System.out.println(mark.getKey() + ": " + mark.getValue());
    }

    private void addCar(String mark, String model) {
        if (!(availCars.containsKey(mark) && availCars.get(mark).contains(model))) {
            System.out.println("Error: Car with mark \"" + mark + "\" and model \"" + model + "\" does not available!");
            return;
        }

        if (!ownCars.containsKey(mark))
            ownCars.put(mark, new HashMap<>());

        if (!ownCars.get(mark).containsKey(model))
            ownCars.get(mark).put(model, 1);
        else
            ownCars.get(mark).replace(model, ownCars.get(mark).get(model) + 1);

        System.out.println("Done!");
    }
    private void addCar() {
        System.out.println("Choose car to add from available cars listed below:");
        showAvailCars();
        Scanner sc = new Scanner(System.in);
        System.out.print("- mark: ");
        String mark = sc.nextLine();
        System.out.print("- model: ");
        String model = sc.nextLine();
        addCar(mark, model);
    }
    private void removeCars(String mark) {
        if (!ownCars.containsKey(mark)) {
            System.out.println("Error: Your garage has not any car with mark \"" + mark + "\", so nothing to remove!");
            return;
        }
        ownCars.remove(mark);
        System.out.println("Done!");
    }
    private void removeCar(String mark, String model) {
        if (!(ownCars.containsKey(mark) && ownCars.get(mark).containsKey(model))) {
            System.out.println("Error: Car with mark \"" + mark + "\" and model \"" + model + "\" does not exist in your garage!");
            return;
        }

        if (ownCars.get(mark).get(model) > 1)
            ownCars.get(mark).replace(model, ownCars.get(mark).get(model) - 1);
        else
            ownCars.get(mark).remove(model);

        if (ownCars.get(mark).isEmpty())
            ownCars.remove(mark);

        System.out.println("Done!");
    }
    private void removeCar() {
        if (ownCars.isEmpty()) {
            System.out.println("Error: your garage is empty, so nothing to remove!");
            return;
        }

        System.out.println("Choose car to remove from own cars listed below:");
        showOwnCars();
        Scanner sc = new Scanner(System.in);
        System.out.print("- mark: ");
        String mark = sc.nextLine();
        System.out.print("- model: ");
        String model = sc.nextLine();
        removeCar(mark, model);
    }

    public void launch() {
        System.out.println("Welcome to garage!");
        printHelp();
        Scanner sc = new Scanner(System.in);
        String cmd = "";
        while (!cmd.equals("exit")) {
            System.out.print("> ");
            cmd = sc.nextLine();
            switch (cmd) {
                case "help":
                    printHelp();
                    break;
                case "show":
                    showOwnCars();
                    break;
                case "add":
                    addCar();
                    break;
                case "remove":
                    removeCar();
                    break;
                case "exit":
                    break;
                default:
                    printHelp();
                    break;
            }
        }
        System.out.println("See you later!");
    }
}
