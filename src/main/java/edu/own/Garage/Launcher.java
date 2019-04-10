package edu.own.Garage;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        if (args.length == 1)
            try {
                Garage garage = new Garage(args[0]);
                garage.launch();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        else
            System.out.println("Arguments mismatch!\nThere must be next arguments:\n- path to car base JSON file");
    }
}
