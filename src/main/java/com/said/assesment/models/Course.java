package com.said.assesment.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(CourseId.class)
public class Course implements Serializable {

    @Id
    private String name;

    @ManyToOne
    @JoinColumn(name = "institution_name")
    @Id
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

}
