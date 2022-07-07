package com.said.assesment.services;

import com.said.assesment.models.Course;
import com.said.assesment.models.Institution;
import com.said.assesment.models.ResponseObject;
import com.said.assesment.repositories.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository mCourseRepository;

    public CourseService(CourseRepository courseRepository) {
        mCourseRepository = courseRepository;
    }

    public ResponseObject getAllCoursesByInstitution(String institutionName) {
        List<Course> courses =
                mCourseRepository.getCoursesByInstitution(institutionName);
        return new ResponseObject(HttpStatus.OK.value(),
                institutionName + " courses retrieved successfully.",
                courses);
    }

    public ResponseObject searchCoursesByInstitution(String keyword, String institutionName) {
        return new ResponseObject(HttpStatus.OK.value(),
                "Searching courses was successful",
                mCourseRepository.search(keyword, institutionName)
        );
    }

    public ResponseObject sortCoursesByName(String institutionName, Sort.Direction direction) {
        return new ResponseObject(HttpStatus.OK.value(),
                "Sorting courses was successful",
                mCourseRepository.getCoursesByInstitution(institutionName, Sort.by(direction, "name"))
        );
    }

    public ResponseObject addCourseToInstitution(String courseName, String institutionName) {
        if (mCourseRepository.courseExistsInInstitution(courseName, institutionName))
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Adding course to institution failed. A course with the same name already exists in the " +
                            "institution.",
                    ""
            );

        Course newCourse = new Course(courseName, new Institution(institutionName));
        mCourseRepository.save(newCourse);
        return new ResponseObject(HttpStatus.CREATED.value(),
                "Course was successfully added to institution",
                ""
        );
    }

    @Transactional
    public ResponseObject updateCourseName(String institutionName, String newName, String oldName) {

        if (!mCourseRepository.courseExistsInInstitution(oldName, institutionName)) {
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Editing course failed. The course you are editing does not exist in the institution.",
                    ""
            );
        }

        if (mCourseRepository.courseExistsInInstitution(newName, institutionName)) {
            return new ResponseObject(HttpStatus.CONFLICT.value(),
                    "Editing course failed. A course with the same name already exists in the institution.",
                    ""
            );
        }

        mCourseRepository.updateCourseName(institutionName, newName, oldName);
        return new ResponseObject(HttpStatus.OK.value(),
                "Course name was successfully edited",
                ""
        );

    }

    public ResponseObject deleteCourse(String courseName, String institutionName) {
        Course course = mCourseRepository.getCourseByNameAndInstitutionName(courseName, institutionName);

        if (course == null)
            return new ResponseObject(HttpStatus.NOT_FOUND.value(),
                    "Deleting course failed.  The course you are trying to delete does not exist in the " +
                            "system.",
                    "");

        mCourseRepository.delete(course);
        return new ResponseObject(HttpStatus.OK.value(),
                "Course was successfully deleted.",
                "");
    }

    public boolean institutionHasNoCourses(String institutionName) {
        return mCourseRepository.getCoursesByInstitution(institutionName).isEmpty();
    }

    public boolean checkIfCourseExists(String courseName, String institutionName) {
        return mCourseRepository.courseExistsInInstitution(courseName, institutionName);
    }
}
