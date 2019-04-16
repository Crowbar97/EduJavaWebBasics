package edu.own.garage.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="marks")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "mark", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Model> models;

    // for hibernate
    public Mark() {}
    public Mark(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Mark(" + id + ", \"" + name + "\", " + (models != null ? models : new ArrayList<>()) + ")";
    }
}