package com.said.assesment.controllers;

import com.said.assesment.models.Institution;
import com.said.assesment.models.ResponseObject;
import com.said.assesment.services.CourseService;
import com.said.assesment.services.InstitutionService;
import com.said.assesment.services.StudentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@Tag(name = "All Endpoints", description = "This is the documentation for the assessment API as specified in the " +
        "assessment specification document.<br>" +
        "The API always returns a ResponseObject whose specifications are in the Schemas section at the bottom of the" +
        " page" +
        ".<br>" +
        "ResponseObject contains the HTTP status of the" +
        " operation, the message regarding the operation and the list of data if the operation returns some data. If " +
        "operation does not data then the list is empty. ")
public class Controller {

    private final InstitutionService mInstitutionService;
    private final CourseService mCourseService;
    private final StudentService mStudentService;

    public Controller(InstitutionService institutionService, CourseService courseService, StudentService studentService) {
        mInstitutionService = institutionService;
        mCourseService = courseService;
        mStudentService = studentService;
    }


    @PostMapping("/addNewInstitution")
    public ResponseObject addNewInstitution(@RequestParam String institutionName) {

        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error adding institution. Please provide institution name",
                    new ArrayList<>());

        return mInstitutionService.addNewInstitution(institutionName);
    }

    @GetMapping("/listAllInstitutions")
    public ResponseObject listAllInstitutions() {
        return mInstitutionService.listAllInstitutions();
    }


    @GetMapping("/searchInstitutions")
    public ResponseObject searchInstitutions(@Parameter(description = "Keyword is the word you want to search can be " +
            "full institution name or a part of it") String keyword) {
        return mInstitutionService.searchInstitutions(keyword);
    }

    @GetMapping("/sortInstitutionsByName")
    public ResponseObject sortInstitutionsByName(HttpServletRequest request) {

        //Instruction says "sort the list of institutions by name (ascending and descending) simply by
        //clicking the NAME header of the table." which means the sort is toggled from ascending or descending
        // through clicks

        String institutionSessionAttributeName = "institutionSortOrder";
        Sort.Direction direction = toggleSortOrder(request.getSession().getAttribute(institutionSessionAttributeName));

        //Save the new sort order
        request.getSession().setAttribute(institutionSessionAttributeName, direction);
        return mInstitutionService.sortInstitutionsByName(direction);
    }

    @DeleteMapping("/deleteInstitution")
    public ResponseObject deleteInstitution(@RequestParam String institutionName) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error deleting institution. Please provide institution name",
                    new ArrayList<>());

        if (mCourseService.institutionHasNoCourses(institutionName))
            return mInstitutionService.deleteInstitution(institutionName);
        else
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Deleting institution failed. Institution cannot be deleted because it has courses assigned to it.",
                    new ArrayList<>());
    }

    @PatchMapping("/editInstitutionName")
    public ResponseObject editInstitutionName(String newInstitutionName, String oldInstitutionName) {
        if (newInstitutionName == null || oldInstitutionName == null || newInstitutionName.isBlank() || oldInstitutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error editing institution. Please provide the current institution name and the new name",
                    new ArrayList<>());
        return mInstitutionService.editInstitutionName(newInstitutionName, oldInstitutionName);
    }

    @GetMapping("/listAllCoursesByInstitution")
    public ResponseObject listAllCoursesByInstitution(String institutionName) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error retrieving courses in institution. Please provide institution name",
                    new ArrayList<>());

        if (mInstitutionService.getInstitution(institutionName) != null)
            return mCourseService.listAllCoursesByInstitution(institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error retrieving courses. Institution does not exist",
                    new ArrayList<>());
    }

    @GetMapping("/searchCourses")
    public ResponseObject searchCoursesInInstitution(
            @Parameter(description = "Keyword is the word you want to search can be " +
                    "full course name or a part of it") String keyword,
            String institutionName
    ) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error searching courses. Please provide institution name",
                    new ArrayList<>());

        if (mInstitutionService.getInstitution(institutionName) != null)
            return mCourseService.searchCoursesByInstitution(keyword, institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error searching courses in institution. Institution does not exist",
                    new ArrayList<>());
    }

    @GetMapping("/sortCoursesInInstitutionByName")
    public ResponseObject sortCoursesInInstitutionByName(String institutionName, HttpServletRequest request) {

        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error sorting courses. Please provide institution name",
                    new ArrayList<>());

        if (mInstitutionService.getInstitution(institutionName) == null)
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error sorting courses in institution. Institution does not exist",
                    new ArrayList<>());


        //Instruction says "sort the courses by name in ascending and descending order by simply
        //clicking on the course name table header." which means the sort is toggled from ascending or descending
        // through clicks
        String courseSessionAttributeName = "courseSortOrder";
        Sort.Direction direction = toggleSortOrder(request.getSession().getAttribute(courseSessionAttributeName));

        //Save the new sort order
        request.getSession().setAttribute(courseSessionAttributeName, direction);
        return mCourseService.sortCoursesInInstitutionByName(institutionName, direction);
    }

    @DeleteMapping("/deleteCourse")
    public ResponseObject deleteCourse(String courseName, String institutionName) {
        if (courseName == null || institutionName == null || courseName.isBlank() || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error deleting course. Please provide course name and institution name",
                    new ArrayList<>());

        if (mStudentService.courseHasNoStudent(courseName, institutionName))
            return mCourseService.deleteCourse(courseName, institutionName);
        else
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Deleting course failed. Course cannot be deleted because it has student(s) assigned to it.",
                    new ArrayList<>());
    }

    @PostMapping("/addCourseToInstitution")
    public ResponseObject addCourseToInstitution(String courseName, String institutionName) {
        if (courseName == null || institutionName == null || courseName.isBlank() || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error adding course. Please provide course name and institution name",
                    new ArrayList<>());

        Institution institution = mInstitutionService.getInstitution(institutionName);
        if (institution != null)
            return mCourseService.addCourseToInstitution(courseName, institution.getId());
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error adding course to institution. Institution does not exist in the system",
                    new ArrayList<>());
    }

    @PatchMapping("/editCourseName")
    public ResponseObject editCourseName(String institutionName, String newCourseName, String oldCourseName) {
        if (institutionName == null || newCourseName == null || oldCourseName == null ||
                institutionName.isBlank() || newCourseName.isBlank() || oldCourseName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error updating course name. Please provide current course name, new course name and " +
                            "current institution name",
                    new ArrayList<>());

        Institution institution = mInstitutionService.getInstitution(institutionName);
        if (institution != null)
            return mCourseService.editCourseName(institution.getId(), newCourseName, oldCourseName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error updating course name in institution. Institution does not exist",
                    new ArrayList<>());
    }


    @PostMapping("/addStudent")
    public ResponseObject addStudent(String studentName, String courseName, String institutionName) {
        if (studentName == null || courseName == null || institutionName == null ||
                studentName.isBlank() || courseName.isBlank() || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error adding student. Please provide student name, course name and institution name",
                    new ArrayList<>());

        if (mCourseService.getCourseIfExists(courseName, institutionName) != null)
            return mStudentService.addAStudent(studentName, courseName, institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error adding student. Course does not exist.",
                    new ArrayList<>());
    }

    @DeleteMapping("/deleteStudent")
    public ResponseObject deleteStudent(Long studentId) {
        if (studentId == null)
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error deleting student. Please provide student's name",
                    new ArrayList<>());

        return mStudentService.deleteStudent(studentId);
    }

    @PatchMapping("/editStudentName")
    public ResponseObject editStudentName(Long studentId, String newStudentName) {
        if (studentId == null || newStudentName == null || newStudentName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error editing student name. Please provide student id and student new name",
                    new ArrayList<>());

        return mStudentService.editStudentName(studentId, newStudentName);
    }

    @PatchMapping("/changeCourseForStudent")
    public ResponseObject ChangeCourseForStudent(Long studentId, String newCourse) {
        if (studentId == null || newCourse == null || newCourse.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error changing course. Please provide student id and the new course name",
                    new ArrayList<>());

        return mStudentService.changeCourse(studentId, newCourse);
    }

    @PatchMapping("/transferToAnotherInstitution")
    public ResponseObject transferToAnotherInstitution(Long studentId, String newInstitution, String newCourse) {
        if (studentId == null || newInstitution == null || newCourse == null ||
                newInstitution.isBlank() || newCourse.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error transferring student. Please provide student id, new institution name and new course name",
                    new ArrayList<>());

        return mStudentService.transferToAnotherInstitution(studentId, newInstitution, newCourse);
    }

    @GetMapping("/listStudentsByInstitution")
    public ResponseObject listStudentsByInstitution(String institutionName,
                                                    @Parameter(description = "Page number starting from 1. Default " +
                                                            "is 1.") Integer page) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error retrieving students in institution. Please provide institution name",
                    new ArrayList<>());

        page = resolvePage(page);
        if (mInstitutionService.getInstitution(institutionName) != null)
            return mStudentService.getStudentsByInstitution(institutionName, page);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error retrieving students. Institution does not exist",
                    new ArrayList<>());
    }

    @GetMapping("/searchStudentsInInstitution")
    public ResponseObject searchStudentsInInstitution(
            @Parameter(description = "Keyword is the word you want to search can be " +
                    "full student name or a part of it") String keyword,
            String institutionName,
            @Parameter(description = "Page number starting from 1. Default " +
                    "is 1.") Integer page) {

        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error searching students in institution. Please provide institution name",
                    new ArrayList<>());

        page = resolvePage(page);
        if (mInstitutionService.getInstitution(institutionName) != null)
            return mStudentService.searchStudentByInstitution(keyword, institutionName, page);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error searching students in institution. Institution does not exist",
                    new ArrayList<>());
    }

    @GetMapping("/filterStudentsListByCourse")
    public ResponseObject filterStudentsByCourse(String institutionName,
                                                 String courseName,
                                                 @Parameter(description = "Page number starting from 1. Default " +
                                                         "is 1.") Integer page) {

        if (institutionName == null || courseName == null || institutionName.isBlank() || courseName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error filtering students by course. Please provide institution name and course name",
                    new ArrayList<>());

        page = resolvePage(page);
        if (mCourseService.getCourseIfExists(courseName, institutionName) != null)
            return mStudentService.filterStudentsByInstitutionAndCourse(institutionName, courseName, page);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Filtering students by course failed. Course does not exist",
                    new ArrayList<>());
    }

    private Sort.Direction toggleSortOrder(Object sessionAttribute) {
        Sort.Direction direction;

        //If null (new session) or other object type set direction to ascending
        if (!(sessionAttribute instanceof Sort.Direction))
            direction = Sort.Direction.ASC;
        else {
            //Toggle the sort order
            direction = sessionAttribute == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC;
        }

        return direction;
    }

    private Integer resolvePage(Integer page) {
        if (page == null)
            page = 0;
        else
            page -= 1;
        return page;
    }
}
