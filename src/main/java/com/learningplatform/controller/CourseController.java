package com.learningplatform.controller;

import com.learningplatform.dto.CourseDTO;
import com.learningplatform.dto.CourseReviewDTO;
import com.learningplatform.dto.EnrollmentDTO;
import com.learningplatform.service.CourseReviewService;
import com.learningplatform.service.CourseService;
import com.learningplatform.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management API")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CourseReviewService courseReviewService;

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get course with modules and lessons")
    public ResponseEntity<CourseDTO> getCourseWithModules(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseWithModules(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/published")
    @Operation(summary = "Get all published courses")
    public ResponseEntity<List<CourseDTO>> getPublishedCourses() {
        List<CourseDTO> courses = courseService.getPublishedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get courses by teacher")
    public ResponseEntity<List<CourseDTO>> getCoursesByTeacher(@PathVariable Long teacherId) {
        List<CourseDTO> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get courses by category")
    public ResponseEntity<List<CourseDTO>> getCoursesByCategory(@PathVariable Long categoryId) {
        List<CourseDTO> courses = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses by title")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String keyword) {
        List<CourseDTO> courses = courseService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish course")
    public ResponseEntity<CourseDTO> publishCourse(@PathVariable Long id) {
        CourseDTO publishedCourse = courseService.publishCourse(id);
        return ResponseEntity.ok(publishedCourse);
    }

    @PatchMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish course")
    public ResponseEntity<CourseDTO> unpublishCourse(@PathVariable Long id) {
        CourseDTO unpublishedCourse = courseService.unpublishCourse(id);
        return ResponseEntity.ok(unpublishedCourse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // Enrollment endpoints
    @PostMapping("/{id}/enroll")
    @Operation(summary = "Enroll a student in a course")
    public ResponseEntity<EnrollmentDTO> enrollStudent(
            @PathVariable Long id,
            @RequestParam Long userId) {
        EnrollmentDTO enrollment = enrollmentService.enrollStudent(userId, id);
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/unenroll")
    @Operation(summary = "Unenroll a student from a course")
    public ResponseEntity<Void> unenrollStudent(
            @PathVariable Long id,
            @RequestParam Long userId) {
        enrollmentService.unenrollStudent(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/enrollments")
    @Operation(summary = "Get all enrollments for a course")
    public ResponseEntity<List<EnrollmentDTO>> getCourseEnrollments(@PathVariable Long id) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourse(id);
        return ResponseEntity.ok(enrollments);
    }

    // Review endpoints
    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get all reviews for a course")
    public ResponseEntity<List<CourseReviewDTO>> getCourseReviews(@PathVariable Long id) {
        List<CourseReviewDTO> reviews = courseReviewService.getReviewsByCourse(id);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{id}/reviews")
    @Operation(summary = "Add a review for a course")
    public ResponseEntity<CourseReviewDTO> addCourseReview(
            @PathVariable Long id,
            @Valid @RequestBody CourseReviewDTO reviewDTO) {
        reviewDTO.setCourseId(id);
        CourseReviewDTO createdReview = courseReviewService.createReview(reviewDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/rating")
    @Operation(summary = "Get average rating for a course")
    public ResponseEntity<Double> getCourseRating(@PathVariable Long id) {
        Double rating = courseReviewService.getAverageRating(id);
        return ResponseEntity.ok(rating);
    }
}
