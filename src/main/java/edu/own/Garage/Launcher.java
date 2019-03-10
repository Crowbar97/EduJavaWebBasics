package edu.own.Garage;

import java.sql.*;

public class Launcher {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if (args.length == 4) {
            Class.forName(args[0]);
            new GarageManager(DriverManager.getConnection(args[1], args[2], args[3])).launch();
        } else
            System.out.println("Arguments mismatch! Usage:"
                    + "\nGarageLauncher \\"
                    + "\n\t<JDBC Driver> \\"
                    + "\n\t<DB connection URL> \\"
                    + "\n\t<User name> \\"
                    + "\n\t<Password>");

    }
}
