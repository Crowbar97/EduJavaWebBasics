package edu.own.Garage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * GarageManager simple model
 */
public class GarageManager {
    private enum Selections {
        baseCars("select Makes.name_ as make, Models.name_ as model from Makes, Models, BaseCars\n"
                + "where Makes.id_ = Models.makeId_ and BaseCars.modelId_ = Models.id_;");

        private String value;

        Selections(String value) {
            this.value = value;
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
        baseCars = new HashMap<>();
        ownCars = new HashMap<>();
        loadCarBase();
    }

    private void addBaseCar(String make, String model) {
        if (!baseCars.containsKey(make))
            baseCars.put(make, new HashSet<>(Collections.singletonList(model)));
        else
            baseCars.get(make).add(model);
    }
    private void loadCarBase() throws SQLException {
        ResultSet rs = garage.createStatement().executeQuery(Selections.baseCars.value);
        while(rs.next())
            addBaseCar(rs.getString("make"), rs.getString("model"));
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
        if (!(baseCars.containsKey(make) && baseCars.get(make).contains(model))) {
            System.out.println("Error: Car with make \"" + make + "\" and model \"" + model + "\" does not exist in the car base!");
            return;
        }

        if (!ownCars.containsKey(make))
            ownCars.put(make, new HashMap<>());

        if (!ownCars.get(make).containsKey(model))
            ownCars.get(make).put(model, 1);
        else
            ownCars.get(make).replace(model, ownCars.get(make).get(model) + 1);

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
