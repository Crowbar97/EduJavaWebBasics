package edu.own.Garage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * GarageManager simple model
 */
public class GarageManager {
    private enum Queries {;
        private enum Select {
            baseCars("select Makes.name_ as make, Models.name_ as model from Makes, Models, BaseCars\n"
                        + "where Makes.id_ = Models.makeId_ and BaseCars.modelId_ = Models.id_;"),
            ownCars("select Makes.name_ as make, Models.name_ as model, OwnCars.count_ as count from Makes, Models, OwnCars\n"
                        + "where Makes.id_ = Models.makeId_ and OwnCars.modelId_ = Models.id_;");

            private String proc;

            Select(String proc) {
                this.proc = proc;
            }
        }
    }
    private enum Updates {;
        private enum Insert {
            car("insert OwnCars (modelId_, count_)\n"
                    + "select Models.id_, ? from Models, Makes\n"
                        + "where Models.name_ = ? and Makes.name_ = ? and Models.makeId_ = Makes.id_;");

            private String proc;

            Insert(String proc) {
                this.proc = proc;
            }
        }
        private enum Delete {
            allCars("truncate OwnCars;");

            private String proc;

            Delete(String value) {
                this.proc = value;
            }
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
        ResultSet rs = garage.createStatement().executeQuery(Queries.Select.baseCars.proc);
        while(rs.next())
            addBaseCar(rs.getString("make"), rs.getString("model"));
        System.out.println("Done!");
    }
    private void loadCars() throws SQLException {
        System.out.println("Opening the garage...");
        ownCars = new HashMap<>();
        ResultSet rs = garage.createStatement().executeQuery(Queries.Select.ownCars.proc);
        while(rs.next())
            addCar(rs.getString("make"), rs.getString("model"), rs.getInt("count"), false);
        System.out.println("Done!");
    }
    private void saveCars() throws SQLException {
        System.out.println("Closing garage...");
        PreparedStatement s = garage.prepareStatement(Updates.Insert.car.proc);
        s.executeUpdate(Updates.Delete.allCars.proc);
        ownCars.forEach((make, models) ->
                models.forEach((model, count) -> {
                    try {
                        s.setInt(1, count);
                        s.setString(2, model);
                        s.setString(3, make);
                        s.addBatch();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }));
        s.executeBatch();
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
