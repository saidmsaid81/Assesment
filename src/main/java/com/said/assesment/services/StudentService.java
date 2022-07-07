package com.said.assesment.services;

import com.said.assesment.models.Course;
import com.said.assesment.models.Institution;
import com.said.assesment.models.ResponseObject;
import com.said.assesment.models.Student;
import com.said.assesment.repositories.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository mStudentRepository;
    private final CourseService mCourseService;

    public StudentService(StudentRepository studentRepository, CourseService courseService) {
        mStudentRepository = studentRepository;
        mCourseService = courseService;
    }


    public ResponseObject addAStudent(String studentName, String courseName, String institutionName ){
        Course course = new Course(courseName, new Institution(institutionName));
        Student student = new Student(studentName, course);
        Student savedStudent = mStudentRepository.save(student);

        return new ResponseObject(HttpStatus.CREATED.value(),
                "Student was successfully added",
                "Student's ID number: " + savedStudent.getId()
        );
    }

    public ResponseObject deleteStudent(long studentId){
        Optional<Student> optionalStudent = mStudentRepository.findById(studentId);
        if (optionalStudent.isEmpty())
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error deleting student. Student does not exist in the system",
                    ""
            );

        mStudentRepository.deleteById(studentId);
        return new ResponseObject(HttpStatus.OK.value(),
                "Student was successfully deleted",
                ""
        );
    }

    public ResponseObject editStudentName(long studentId, String newName) {
        Optional<Student> optionalStudent = mStudentRepository.findById(studentId);
        if (optionalStudent.isEmpty())
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Editing student name failed. The student you are editing does not exist in the institution.",
                    ""
            );

        optionalStudent.get().setName(newName);
        mStudentRepository.save(optionalStudent.get());

        return new ResponseObject(HttpStatus.OK.value(),
                "Student name was successfully edited",
                ""
        );
    }

    @Transactional
    public ResponseObject changeCourse(long studentId, String newCourse) {
        Optional<Student> optionalStudent = mStudentRepository.findById(studentId);
        if (optionalStudent.isEmpty())
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Changing course failed. The student does not exist in the institution.",
                    ""
            );

        Student student = optionalStudent.get();
        String institutionName = student.getCourse().getInstitution().getName();
        if (mCourseService.checkIfCourseExists(institutionName, newCourse)){
            Course course = new Course(newCourse, new Institution(institutionName));
            student.setCourse(course);
            mStudentRepository.save(student);
            return new ResponseObject(HttpStatus.OK.value(),
                    "Changing course for student was successful",
                    ""
            );
        }
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Changing course failed. " + institutionName + " does not offer " + newCourse,
                    ""
            );
    }

    public ResponseObject transferToAnotherInstitution(long studentId, String newInstitution, String newCourse) {
        Optional<Student> optionalStudent = mStudentRepository.findById(studentId);
        if (optionalStudent.isEmpty())
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Transferring university failed. The student does not exist in the institution.",
                    ""
            );

        if (mCourseService.checkIfCourseExists(newInstitution, newCourse)){
            Student student = optionalStudent.get();
            Course course = new Course(newCourse, new Institution(newInstitution));
            student.setCourse(course);
            mStudentRepository.save(student);
            return new ResponseObject(HttpStatus.OK.value(),
                    "Transferring university was successful",
                    ""
            );
        }
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Transferring university failed. Course does not exist" ,
                    ""
            );
    }

    public ResponseObject getStudentsByInstitution(String institutionName, int pageNumber) {
        PageRequest pageRequest = getPageRequest(pageNumber);
        Page<Student> students = mStudentRepository.getStudentsByCourseInstitutionNameIgnoreCase(institutionName, pageRequest);
        return new ResponseObject(HttpStatus.OK.value(),
                "Students retrieved successfully",
                students.getContent());
    }

    public ResponseObject searchStudentByInstitution(String keyword, String institutionName, int pageNumber) {
        Page<Student> students = mStudentRepository.searchStudentByInstitution(keyword, institutionName, getPageRequest(pageNumber));
        return new ResponseObject(HttpStatus.OK.value(),
                "Searching institutions was successful",
                students.getContent()
        );
    }

    public ResponseObject filterStudentsByInstitutionAndCourse(String institutionName, String courseName, int pageNumber){
        Page<Student> students = mStudentRepository.getStudentByCourse(courseName, institutionName,
                getPageRequest(pageNumber));
        return new ResponseObject(HttpStatus.OK.value(),
                "Students in" +  institutionName + " successfully filtered by course",
                students.getContent());

    }

    public boolean courseHasNoStudent(String courseName, String institutionName) {
        return mStudentRepository.getStudentByCourse(courseName, institutionName).isEmpty();
    }

    private PageRequest getPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber, 10, Sort.by(Sort.Direction.ASC, "name"));
    }
}
