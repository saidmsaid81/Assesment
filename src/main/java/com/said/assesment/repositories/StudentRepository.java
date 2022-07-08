package com.said.assesment.repositories;

import com.said.assesment.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT student FROM Student student WHERE lower(student.course.name) = lower(?1) AND lower(student.course" +
            ".institution.name) = lower(?2) ")
    List<Student> getStudentByCourse(String courseName, String institutionName);

    @Query("SELECT student FROM Student student WHERE lower(student.course.name) = lower(?1) AND lower(student.course" +
            ".institution.name) = lower(?2) ")
    Page<Student> getStudentByCourse(String courseName, String institutionName, Pageable pageable);

    Page<Student> getStudentsByCourseInstitutionNameIgnoreCase(String institutionName, Pageable pageable);

    @Query(
            "SELECT student FROM Student student WHERE lower(concat(student.id, ' ', student.name, ' ', student" +
                    ".course.name, ' ')) " +
                    "LIKE lower" +
                    "(concat" +
                    "('%', ?1," +
                    "'%')) AND lower(student.course.institution.name) = lower(?2)"
    )
    Page<Student> searchStudentByInstitution(String keyword, String institutionName, Pageable pageable);
}
