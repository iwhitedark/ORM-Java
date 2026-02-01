package com.learningplatform.controller;

import com.learningplatform.dto.EnrollmentDTO;
import com.learningplatform.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Enrollment management API")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment by ID")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(@PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all enrollments for a student")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/student/{studentId}/active")
    @Operation(summary = "Get active enrollments for a student")
    public ResponseEntity<List<EnrollmentDTO>> getActiveEnrollmentsByStudent(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getActiveEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @PatchMapping("/{id}/progress")
    @Operation(summary = "Update enrollment progress")
    public ResponseEntity<EnrollmentDTO> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progress) {
        EnrollmentDTO updatedEnrollment = enrollmentService.updateProgress(id, progress);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark enrollment as completed")
    public ResponseEntity<EnrollmentDTO> completeEnrollment(@PathVariable Long id) {
        EnrollmentDTO completedEnrollment = enrollmentService.completeEnrollment(id);
        return ResponseEntity.ok(completedEnrollment);
    }

    @PatchMapping("/{id}/drop")
    @Operation(summary = "Drop enrollment")
    public ResponseEntity<EnrollmentDTO> dropEnrollment(@PathVariable Long id) {
        EnrollmentDTO droppedEnrollment = enrollmentService.dropEnrollment(id);
        return ResponseEntity.ok(droppedEnrollment);
    }

    @GetMapping("/check")
    @Operation(summary = "Check if student is enrolled in a course")
    public ResponseEntity<Boolean> isStudentEnrolled(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        boolean isEnrolled = enrollmentService.isStudentEnrolled(studentId, courseId);
        return ResponseEntity.ok(isEnrolled);
    }
}
