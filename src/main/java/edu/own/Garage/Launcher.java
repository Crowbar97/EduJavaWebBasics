package edu.own.Garage;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        if (args.length == 1)
            try {
                Garage garage = new Garage(args[0]);
                garage.launch();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        else
            System.err.println("Argument missing!\nUsage: Garage <path/to/carBase.json>");
    }
}
