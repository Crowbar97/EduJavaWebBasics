package edu.own.garage;

import edu.own.garage.entities.Model;
import edu.own.garage.entities.OwnModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;

/**
 * GarageManager simple model
 */
public class GarageManager {
    /**
     * factory of sessions with garage data base
     */
    private SessionFactory garage;

    public GarageManager(SessionFactory garage) {
        this.garage = garage;
    }

    // db manipulation
    private List<Model> loadAvailModels() {
        List<Model> availModels;
        try (Session session = garage.getCurrentSession()) {
            session.beginTransaction();
            availModels = session.createQuery("from Model").getResultList();
            session.getTransaction().commit();
        }
        return availModels;
    }
    private List<OwnModel> loadOwnModels() {
        List<OwnModel> ownModels;
        try (Session session = garage.getCurrentSession()) {
            session.beginTransaction();
            ownModels = session.createQuery("from OwnModel").getResultList();
            session.getTransaction().commit();
        }
        return ownModels;
    }

    // printings
    private void printHelp() {
        System.out.println("--- GarageManager Usage ---");
        System.out.println("- help: views this help");
        System.out.println("- show: shows own cars");
        System.out.println("- add: adds new car");
        System.out.println("- remove: removes specified car");
        System.out.println("- exit: closes your garage");
    }
    private Map<String, Set<String>> tokenizeM(List<Model> models) {
        Map<String, Set<String>> marks = new HashMap<>();
        for (Model model : models) {
            String markName = model.getMark().getName();
            String modelName = model.getName();
            if (marks.containsKey(markName))
                marks.get(markName).add(modelName);
            else
                marks.put(markName, new HashSet<>(Collections.singletonList(modelName)));
        }
        return marks;
    }
    private void showAvailCars() {
        List<Model> availModels = loadAvailModels();

        if (availModels.isEmpty()) {
            System.out.println("There are no available cars!");
            return;
        }
        for (Map.Entry<String, Set<String>> mark : tokenizeM(availModels).entrySet())
            System.out.println(mark.getKey() + ": " + mark.getValue());
    }
    private Map<String, Map<String, Integer>> tokenizeOM(List<OwnModel> ownModels) {
        Map<String, Map<String, Integer>> marks = new HashMap<>();
        for (OwnModel ownModel : ownModels) {
            String markName = ownModel.getMark().getName();
            String modelName = ownModel.getName();
            int count = ownModel.getCount();
            if (marks.containsKey(markName))
                marks.get(markName).put(modelName, count);
            else
                marks.put(markName, new HashMap<>(Map.of(modelName, count)));
        }
        return marks;
    }
    private void showOwnCars() {
        List<OwnModel> ownModels = loadOwnModels();

        if (ownModels.isEmpty()) {
            System.out.println("Your garage is empty!");
            return;
        }
        for (Map.Entry<String, Map<String, Integer>> mark : tokenizeOM(ownModels).entrySet())
            System.out.println(mark.getKey() + ": " + mark.getValue());
    }

    // actions
    private void addCar(String markName, String modelName) {
        try (Session session = garage.getCurrentSession()) {
            session.beginTransaction();

            List<Model> model = session.createQuery("from Model m where"
                                                        + " m.name = '" + modelName + "'"
                                                        + " and m.mark.name = '" + markName + "'")
                                                        .getResultList();

            if (model.isEmpty()) {
                System.out.println("Error: Car with mark \"" + markName
                                    + "\" and model \"" + modelName + "\" does not available!");
                return;
            }

            List<OwnModel> ownModel = session.createQuery("from OwnModel m where"
                                                                + " m.model.name = '" + modelName + "'"
                                                                + " and m.model.mark.name = '" + markName + "'")
                                                                .getResultList();

            if (ownModel.isEmpty()) {
                System.out.println("Adding new car...");
                session.save(new OwnModel(model.get(0), 1));
            } else {
                System.out.println("Updating car count...");
                ownModel.get(0).incCount();
            }

            session.getTransaction().commit();
            System.out.println("Done!");
        }
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
    private void removeCar(String markName, String modelName) {
        try (Session session = garage.getCurrentSession()) {
            session.beginTransaction();

            List<OwnModel> ownModel = session.createQuery("from OwnModel m where"
                                                                + " m.model.name = '" + modelName + "'"
                                                                + " and m.model.mark.name = '" + markName + "'")
                                                                .getResultList();

            if (ownModel.isEmpty()) {
                System.out.println("Error: Car with mark \"" + markName
                                    + "\" and model \"" + modelName + "\" does not exist in your garage!");
                return;
            }

            if (ownModel.get(0).getCount() > 1) {
                System.out.println("Updating car count...");
                ownModel.get(0).decCount();
            }
            else {
                System.out.println("Removing last model...");
                session.delete(ownModel.get(0));
            }

            session.getTransaction().commit();
            System.out.println("Done!");
        }
    }
    private void removeCar() {
        if (loadOwnModels().isEmpty()) {
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
                    break;
                default:
                    System.out.println("- unknown command: \"" + cmd + "\"!");
                    printHelp();
                    break;
            }
        }
        System.out.println("See you later!");
    }
}
