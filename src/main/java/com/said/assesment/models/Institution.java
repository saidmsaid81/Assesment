package com.said.assesment.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
public class Institution {
    
    private String name;

    @Id
    @SequenceGenerator(name = "institution_sequence", sequenceName = "institution_sequence", allocationSize = 1)
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "institution_sequence"
    )
    @JsonIgnore
    private long id;

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

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(long id) {
        this.id = id;
    }
}
