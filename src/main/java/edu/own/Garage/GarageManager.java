package edu.own.Garage;

import java.sql.*;
import java.util.*;

/**
 * GarageManager simple model
 */
public class GarageManager {
    private enum Procedures {
        getAvailCars("{call get_models()}"),
        getCars("{call get_cars()}"),
        saveCars("{call save_cars(?)}");

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
     * "avail" means "available"
     * contains possible cars loaded from .json file
     * pairs of (mark, models)
     */
    private Map<String, Set<String>> availCars;
    /**
     * contains own cars
     * pairs of (mark, (model, count))
     */
    private Map<String, Map<String, Integer>> ownCars;

    public GarageManager(Connection garage) throws SQLException {
        this.garage = garage;
        loadAvailCars();
        loadCars();
    }

    private void adAvailCar(String mark, String model) {
        if (!availCars.containsKey(mark))
            availCars.put(mark, new HashSet<>(Collections.singletonList(model)));
        else
            availCars.get(mark).add(model);
    }
    private void loadAvailCars() throws SQLException {
        System.out.println("Loading available cars...");
        availCars = new HashMap<>();
        try (CallableStatement call = garage.prepareCall(Procedures.getAvailCars.call)) {
            call.execute();
            ResultSet rs = call.getResultSet();
            while(rs.next())
                adAvailCar(rs.getString("mark"), rs.getString("model"));
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
                addCar(rs.getString("mark"), rs.getString("model"), rs.getInt("count"), false);
        }
        System.out.println("Done!");
    }
    private String getStrOwnCars() {
        StringBuilder cars = new StringBuilder();
        ownCars.forEach((mark, models) ->
                models.forEach((model, count) ->
                                cars.append("('").append(mark).append("','").append(model).append("',").append(count).append("),")
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
        addCar(mark, model, 1, true);
    }
    private void addCar(String mark, String model, int count, boolean verbose) {
        if (!(availCars.containsKey(mark) && availCars.get(mark).contains(model))) {
            if (verbose)
                System.out.println("Error: Car with mark \"" + mark + "\" and model \"" + model + "\" does not available!");
            return;
        }

        if (!ownCars.containsKey(mark))
            ownCars.put(mark, new HashMap<>());

        if (!ownCars.get(mark).containsKey(model))
            ownCars.get(mark).put(model, count);
        else
            ownCars.get(mark).replace(model, ownCars.get(mark).get(model) + count);

        if (verbose)
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
