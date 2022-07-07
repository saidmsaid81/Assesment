package com.said.assesment.controllers;

import com.said.assesment.models.ResponseObject;
import com.said.assesment.services.CourseService;
import com.said.assesment.services.InstitutionService;
import com.said.assesment.services.StudentService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Controller {

    private final InstitutionService mInstitutionService;
    private final CourseService mCourseService;
    private final StudentService mStudentService;

    public Controller(InstitutionService institutionService, CourseService courseService, StudentService studentService) {
        mInstitutionService = institutionService;
        mCourseService = courseService;
        mStudentService = studentService;
    }


    @PostMapping("/addInstitution")
    public ResponseObject addInstitution(@RequestParam String name) {
        if (name == null || name.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error adding institution. Please provide institution name",
                    "");

        return mInstitutionService.addInstitution(name);
    }

    @GetMapping("/listInstitutions")
    public ResponseObject listInstitutions() {
        return mInstitutionService.getAllInstitutions();
    }

    @GetMapping("/searchInstitutions")
    public ResponseObject searchInstitutions(String keyword) {
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

    @PatchMapping ("/editInstitutionName")
    public ResponseObject updateInstitutionName(String newName, String oldName) {
        if (newName == null || oldName == null || newName.isBlank() || oldName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error editing institution. Please provide the current institution name and the new name",
                    "");
        return mInstitutionService.updateInstitutionName(newName, oldName);
    }

    @DeleteMapping("/deleteInstitution")
    public ResponseObject deleteInstitution(String name) {
        if (name == null || name.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error deleting institution. Please provide institution name",
                    "");

        if (mCourseService.institutionHasNoCourses(name))
            return mInstitutionService.deleteInstitution(name);
        else
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Deleting institution failed. Institution cannot be deleted because it has courses assigned to it.",
                    "");
    }

    @GetMapping("/listCoursesByInstitution")
    public ResponseObject listCoursesByInstitution(String institutionName) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error retrieving courses in institution. Please provide institution name",
                    "");

        if (mInstitutionService.checkInstitutionExists(institutionName))
            return mCourseService.getAllCoursesByInstitution(institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error retrieving courses. Institution does not exist",
                    "");
    }

    @GetMapping("/searchCourses")
    public ResponseObject searchCoursesInInstitution(String keyword, String institutionName) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error searching courses. Please provide institution name",
                    "");

        if (mInstitutionService.checkInstitutionExists(institutionName))
            return mCourseService.searchCoursesByInstitution(keyword, institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error searching courses in institution. Institution does not exist",
                    "");
    }

    @GetMapping("/sortCoursesByName")
    public ResponseObject sortCoursesByName(String institutionName, HttpServletRequest request) {

        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error sorting courses. Please provide institution name",
                    "");

        if (!mInstitutionService.checkInstitutionExists(institutionName))
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error sorting courses in institution. Institution does not exist",
                    "");


        //Instruction says "sort the courses by name in ascending and descending order by simply
        //clicking on the course name table header." which means the sort is toggled from ascending or descending
        // through clicks
        String courseSessionAttributeName = "courseSortOrder";
        Sort.Direction direction = toggleSortOrder(request.getSession().getAttribute(courseSessionAttributeName));

        //Save the new sort order
        request.getSession().setAttribute(courseSessionAttributeName, direction);
        return mCourseService.sortCoursesByName(institutionName, direction);
    }

    @PostMapping("/addCourse")
    public ResponseObject addCourseToInstitution(String courseName, String institutionName) {
        if (courseName == null || institutionName == null || courseName.isBlank() || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error adding course. Please provide course name and institution name",
                    "");

        if (mInstitutionService.checkInstitutionExists(institutionName))
            return mCourseService.addCourseToInstitution(courseName, institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error adding course to institution. Institution does not exist in the system",
                    "");
    }

    @PatchMapping ("/editCourseName")
    public ResponseObject updateCourseName(String institutionName, String newName, String oldName) {
        if (institutionName == null || newName == null || oldName == null ||
        institutionName.isBlank() || newName.isBlank() || oldName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error updating course name. Please provide current course name, new course name and " +
                            "current institution name",
                    "");

        if (mInstitutionService.checkInstitutionExists(institutionName))
            return mCourseService.updateCourseName(institutionName, newName, oldName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error updating course name in institution. Institution does not exist",
                    "");
    }

    @DeleteMapping("/deleteCourse")
    public ResponseObject deleteCourse(String courseName, String institutionName) {
        if (courseName == null || institutionName == null || courseName.isBlank() || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error deleting course. Please provide course name and institution name",
                    "");

        if (mStudentService.courseHasNoStudent(courseName, institutionName))
            return mCourseService.deleteCourse(courseName, institutionName);
        else
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Deleting course failed. Course cannot be deleted because it has student(s) assigned to it.",
                    "");
    }


    @PostMapping("/addStudent")
    public ResponseObject addStudent(String studentName, String courseName, String institutionName) {
        if (studentName == null || courseName == null || institutionName == null ||
                studentName.isBlank() || courseName.isBlank() || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error adding student. Please provide student name, course name and institution name",
                    "");

        if (mCourseService.checkIfCourseExists(courseName, institutionName))
            return mStudentService.addAStudent(studentName, courseName, institutionName);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error adding student. Course does not exist.",
                    "");
    }

    @DeleteMapping("/deleteStudent")
    public ResponseObject deleteStudent(Long studentId) {
        if (studentId == null)
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error deleting student. Please provide student's name",
                    "");

        return mStudentService.deleteStudent(studentId);
    }

    @PatchMapping ("/editStudentName")
    public ResponseObject editStudentName(Long studentId, String newName) {
        if (studentId == null || newName == null || newName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error editing student name. Please provide student id and student new name",
                    "");

        return mStudentService.editStudentName(studentId, newName);
    }

    @PatchMapping ("/changeCourseForStudent")
    public ResponseObject ChangeCourseForStudent(Long studentId, String newCourse) {
        if (studentId == null || newCourse == null || newCourse.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error changing course. Please provide student id and the new course name",
                    "");

        return mStudentService.changeCourse(studentId, newCourse);
    }

    @PatchMapping ("/transferToAnotherInstitution")
    public ResponseObject transferToAnotherInstitution(Long studentId, String newInstitution, String newCourse) {
        if (studentId == null || newInstitution == null || newCourse == null ||
            newInstitution.isBlank() || newCourse.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error transferring student. Please provide student id, new institution name and new course name",
                    "");

        return mStudentService.transferToAnotherInstitution(studentId, newInstitution, newCourse);
    }

    @GetMapping("/listStudentsByInstitution")
    public ResponseObject listStudentsByInstitution(String institutionName, Integer page) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error retrieving students in institution. Please provide institution name",
                    "");

        page = resolvePage(page);
        if (mInstitutionService.checkInstitutionExists(institutionName))
            return mStudentService.getStudentsByInstitution(institutionName, page);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error retrieving students. Institution does not exist",
                    "");
    }

    @GetMapping("/searchStudentsInInstitution")
    public ResponseObject searchStudentsInInstitution(String keyword, String institutionName, Integer page) {
        if (institutionName == null || institutionName.isBlank())
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error searching students in institution. Please provide institution name",
                    "");

        page = resolvePage(page);
        if (mInstitutionService.checkInstitutionExists(institutionName))
            return mStudentService.searchStudentByInstitution(keyword, institutionName, page);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Error searching students in institution. Institution does not exist",
                    "");
    }

    @GetMapping("/filterStudentsListByCourse")
    public ResponseObject filterStudentsByCourse(String institutionName, String courseName, Integer page) {
        if (institutionName == null || courseName == null || institutionName.isBlank() || courseName.isBlank() )
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                    "Error filtering students by course. Please provide institution name and course name",
                    "");

        page = resolvePage(page);
        if (mCourseService.checkIfCourseExists(courseName, institutionName))
            return mStudentService.filterStudentsByInstitutionAndCourse(institutionName, courseName, page);
        else
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Filtering students by course failed. Course does not exist",
                    "");
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
