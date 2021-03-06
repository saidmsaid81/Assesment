package com.said.assesment.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
public class Course implements Serializable {

    @Id
    @SequenceGenerator(name = "course_sequence", sequenceName = "course_sequence", allocationSize = 1)
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "course_sequence"
    )
    @JsonIgnore
    private long id;
    
    private String name;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    public Course(String name, Institution institution) {
        this.name = name;
        this.institution = institution;
    }


    public Course() {

    }

    public String getName() {
        return name;
    }

    public Institution getInstitution() {
        return institution;
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
