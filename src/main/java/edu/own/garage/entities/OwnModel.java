package edu.own.garage.entities;

import javax.persistence.*;

@Entity
@Table(name="own_models")
public class OwnModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="model_id")
    private Model model;

    @Column(name="count")
    private int count;

    // for hibernate
    public OwnModel() {}
    public OwnModel(Model model, int count) {
        this.model = model;
        this.count = count;
    }

    public Mark getMark() {
        return model.getMark();
    }
    public String getName() {
        return model.getName();
    }
    public int getCount() {
        return count;
    }

    public void incCount() {
        count++;
    }
    public void decCount() {
        count--;
    }

    @Override
    public String toString() {
        return "OwnModel(" + id + ", \"" + model + "\", " + count + ")";
    }
}