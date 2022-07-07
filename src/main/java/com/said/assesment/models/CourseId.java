package com.said.assesment.models;

import java.io.Serializable;
import java.util.Objects;

public class CourseId implements Serializable {

    private String name;
    private Institution institution;


    public CourseId(String name, Institution institution) {
        this.name = name;
        this.institution = institution;
    }

    public CourseId() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseId courseId = (CourseId) o;
        return Objects.equals(name, courseId.name) && Objects.equals(institution, courseId.institution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, institution);
    }
}
