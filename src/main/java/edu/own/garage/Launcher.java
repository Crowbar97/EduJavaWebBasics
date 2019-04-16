package edu.own.garage;

import edu.own.garage.entities.Mark;
import edu.own.garage.entities.Model;
import edu.own.garage.entities.OwnModel;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Launcher {
    public static void main(String[] args) {
        try(SessionFactory sessionFactory = new Configuration()
                                                .configure("hibernate.cfg.xml")
                                                .addAnnotatedClass(Mark.class)
                                                .addAnnotatedClass(Model.class)
                                                .addAnnotatedClass(OwnModel.class)
                                                .buildSessionFactory()) {
            new GarageManager(sessionFactory).launch();
        }
    }
}
