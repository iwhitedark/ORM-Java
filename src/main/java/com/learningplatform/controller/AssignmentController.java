package com.learningplatform.controller;

import com.learningplatform.dto.AssignmentDTO;
import com.learningplatform.dto.SubmissionDTO;
import com.learningplatform.service.AssignmentService;
import com.learningplatform.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignments", description = "Assignment management API")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    @PostMapping
    @Operation(summary = "Create a new assignment")
    public ResponseEntity<AssignmentDTO> createAssignment(@Valid @RequestBody AssignmentDTO assignmentDTO) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(assignmentDTO);
        return new ResponseEntity<>(createdAssignment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        AssignmentDTO assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get all assignments for a lesson")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByLesson(@PathVariable Long lessonId) {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsByLesson(lessonId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all assignments for a course")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByCourse(@PathVariable Long courseId) {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsByCourse(courseId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get assignments for a student (based on enrollments)")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsForStudent(@PathVariable Long studentId) {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsForStudent(studentId);
        return ResponseEntity.ok(assignments);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update assignment")
    public ResponseEntity<AssignmentDTO> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentDTO assignmentDTO) {
        AssignmentDTO updatedAssignment = assignmentService.updateAssignment(id, assignmentDTO);
        return ResponseEntity.ok(updatedAssignment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete assignment")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    // Submission endpoints
    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit assignment")
    public ResponseEntity<SubmissionDTO> submitAssignment(
            @PathVariable Long id,
            @Valid @RequestBody SubmissionDTO submissionDTO) {
        submissionDTO.setAssignmentId(id);
        SubmissionDTO submission = submissionService.submitAssignment(submissionDTO);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/submissions")
    @Operation(summary = "Get all submissions for an assignment")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByAssignment(@PathVariable Long id) {
        List<SubmissionDTO> submissions = submissionService.getSubmissionsByAssignment(id);
        return ResponseEntity.ok(submissions);
    }
}
