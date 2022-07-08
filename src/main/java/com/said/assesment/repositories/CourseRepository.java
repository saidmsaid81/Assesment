package com.said.assesment.repositories;

import com.said.assesment.models.Course;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT course FROM Course course WHERE lower(course.institution.name) = lower(?1)")
    List<Course> getCoursesByInstitution(String institutionName, Sort sort);


    @Query("SELECT course FROM Course course WHERE lower(course.institution.name) = lower(?1)")
    List<Course> getCoursesByInstitution(String institutionName);

    @Query(value = "SELECT EXISTS(SELECT * FROM course WHERE lower(name) = lower(?1) AND institution_id = " +
            "?2)", nativeQuery = true)
    boolean checkIfCourseExists(String courseName, long institutionId);

    @Query("SELECT course FROM Course course WHERE lower(course.name) = lower(?1) AND lower(course.institution.name) " +
            "= lower(?2) ")
    Course getCourseIfExists(String courseName, String institutionName);

    @Query(
            "SELECT course FROM Course course WHERE lower(course.name) LIKE lower(concat" +
                    "('%', ?1,'%')) AND lower(course.institution.name) = lower(?2)"
    )
    List<Course> search(String keyword, String institutionName);

    @Modifying
    @Query("UPDATE Course course SET course.name = ?2 WHERE lower(course.name) = lower(?3) AND course.institution.id = " +
            "?1")
    void updateCourseName(long institutionId, String newName, String oldName);

    @Query("SELECT course FROM Course course WHERE lower(course.name) = lower(?1) AND lower(course.institution.name) " +
            "= lower(?2)")
    Course getCourseByNameAndInstitutionName(String courseName, String institutionName);

}
