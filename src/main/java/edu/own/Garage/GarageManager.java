package edu.own.Garage;

import java.sql.*;
import java.util.*;

/**
 * GarageManager simple model
 */
public class GarageManager {
    private enum Procedures {
        getBaseCars("{call getBaseCars()}"),
        getCars("{call getCars()}"),
        saveCars("{call saveCars(?)}");

        private String call;

        Procedures(String call) {
            this.call = call;
        }
    }
    /**
     * connection with garage data base
     */
    private Connection garage;
    /**
     * contains possible cars loaded from .json file
     * pairs of (make, models)
     */
    private Map<String, Set<String>> baseCars;
    /**
     * contains own cars
     * pairs of (make, (model, count))
     */
    private Map<String, Map<String, Integer>> ownCars;

    public GarageManager(Connection garage) throws SQLException {
        this.garage = garage;
        loadBaseCars();
        loadCars();
    }

    private void addBaseCar(String make, String model) {
        if (!baseCars.containsKey(make))
            baseCars.put(make, new HashSet<>(Collections.singletonList(model)));
        else
            baseCars.get(make).add(model);
    }
    private void loadBaseCars() throws SQLException {
        System.out.println("Loading car base...");
        baseCars = new HashMap<>();
        try (CallableStatement call = garage.prepareCall(Procedures.getBaseCars.call)) {
            call.execute();
            ResultSet rs = call.getResultSet();
            while(rs.next())
                addBaseCar(rs.getString("make"), rs.getString("model"));
        }
        System.out.println("Done!");
    }
    private void loadCars() throws SQLException {
        System.out.println("Opening the garage...");
        ownCars = new HashMap<>();
        try (CallableStatement call = garage.prepareCall(Procedures.getCars.call)) {
            call.execute();
            ResultSet rs = call.getResultSet();
            while (rs.next())
                addCar(rs.getString("make"), rs.getString("model"), rs.getInt("count"), false);
        }
        System.out.println("Done!");
    }
    private String getStrOwnCars() {
        StringBuilder cars = new StringBuilder();
        ownCars.forEach((make, models) ->
                models.forEach((model, count) ->
                                cars.append("('").append(make).append("','").append(model).append("',").append(count).append("),")
                        )
                );
        cars.deleteCharAt(cars.length() - 1);
        return cars.toString();
    }
    private void saveCars() throws SQLException {
        System.out.println("Closing garage...");
        try (CallableStatement call = garage.prepareCall(Procedures.saveCars.call)) {
            call.setString(1, getStrOwnCars());
            call.execute();
        }
        garage.close();
        System.out.println("Done!");
    }

    private void printHelp() {
        System.out.println("--- GarageManager Usage ---");
        System.out.println("- help: views this help");
        System.out.println("- show: shows own cars");
        System.out.println("- add: adds new car");
        System.out.println("- remove: removes specified car");
        System.out.println("- exit: closes your garage");
    }
    private void showBaseCars() {
        if (baseCars.isEmpty()) {
            System.out.println("There are no base cars!");
            return;
        }
        for (Map.Entry<String, Set<String>> make : baseCars.entrySet())
            System.out.println(make.getKey() + ": " + make.getValue());
    }
    private void showOwnCars() {
        if (ownCars.isEmpty()) {
            System.out.println("Your garage is empty!");
            return;
        }
        for (Map.Entry<String, Map<String, Integer>> make : ownCars.entrySet())
            System.out.println(make.getKey() + ": " + make.getValue());
    }

    private void addCar(String make, String model) {
        addCar(make, model, 1, true);
    }
    private void addCar(String make, String model, int count, boolean verbose) {
        if (!(baseCars.containsKey(make) && baseCars.get(make).contains(model))) {
            if (verbose)
                System.out.println("Error: Car with make \"" + make + "\" and model \"" + model + "\" does not exist in the car base!");
            return;
        }

        if (!ownCars.containsKey(make))
            ownCars.put(make, new HashMap<>());

        if (!ownCars.get(make).containsKey(model))
            ownCars.get(make).put(model, count);
        else
            ownCars.get(make).replace(model, ownCars.get(make).get(model) + count);

        if (verbose)
            System.out.println("Done!");
    }
    private void addCar() {
        System.out.println("Choose car to add from base cars listed below:");
        showBaseCars();
        Scanner sc = new Scanner(System.in);
        System.out.print("- make: ");
        String make = sc.nextLine();
        System.out.print("- model: ");
        String model = sc.nextLine();
        addCar(make, model);
    }
    private void removeCar(String make, String model) {
        if (!(ownCars.containsKey(make) && ownCars.get(make).containsKey(model))) {
            System.out.println("Error: Car with make \"" + make + "\" and model \"" + model + "\" does not exist in your garage!");
            return;
        }

        if (ownCars.get(make).get(model) > 1)
            ownCars.get(make).replace(model, ownCars.get(make).get(model) - 1);
        else
            ownCars.get(make).remove(model);

        if (ownCars.get(make).isEmpty())
            ownCars.remove(make);

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
        System.out.print("- make: ");
        String make = sc.nextLine();
        System.out.print("- model: ");
        String model = sc.nextLine();
        removeCar(make, model);
    }

    public void launch() throws SQLException {
        System.out.println("Welcome to the garage!");
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
                    saveCars();
                    break;
                default:
                    printHelp();
                    break;
            }
        }
        System.out.println("See you later!");
    }
}
