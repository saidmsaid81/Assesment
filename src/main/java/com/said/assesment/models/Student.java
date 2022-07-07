package com.said.assesment.models;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
public class Student {

    @Id
    @SequenceGenerator(name = "student_sequence", sequenceName = "student_sequence", allocationSize = 1)
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "student_sequence"
    )
    private long id;

    private String name;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "course_name"),
            @JoinColumn(name = "institution_name")
    })
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public Student() {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Course getCourse() {
        return course;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setName(String name) {
        this.name = name;
    }
}
