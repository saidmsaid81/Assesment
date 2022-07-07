package com.said.assesment.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Institution {

    @Id
    private String name;

    public Institution(String name) {
        this.name = name;
    }

    public Institution() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
