package com.learningplatform.controller;

import com.learningplatform.dto.SubmissionDTO;
import com.learningplatform.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Assignment submission management API")
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/{id}")
    @Operation(summary = "Get submission by ID")
    public ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable Long id) {
        SubmissionDTO submission = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all submissions by a student")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudent(@PathVariable Long studentId) {
        List<SubmissionDTO> submissions = submissionService.getSubmissionsByStudent(studentId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending submissions")
    public ResponseEntity<List<SubmissionDTO>> getPendingSubmissions() {
        List<SubmissionDTO> submissions = submissionService.getPendingSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @PatchMapping("/{id}/grade")
    @Operation(summary = "Grade a submission")
    public ResponseEntity<SubmissionDTO> gradeSubmission(
            @PathVariable Long id,
            @RequestParam Integer score,
            @RequestParam(required = false) String feedback) {
        SubmissionDTO gradedSubmission = submissionService.gradeSubmission(id, score, feedback);
        return ResponseEntity.ok(gradedSubmission);
    }

    @PatchMapping("/{id}/accept")
    @Operation(summary = "Accept a submission")
    public ResponseEntity<SubmissionDTO> acceptSubmission(@PathVariable Long id) {
        SubmissionDTO acceptedSubmission = submissionService.acceptSubmission(id);
        return ResponseEntity.ok(acceptedSubmission);
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject a submission")
    public ResponseEntity<SubmissionDTO> rejectSubmission(
            @PathVariable Long id,
            @RequestParam(required = false) String feedback) {
        SubmissionDTO rejectedSubmission = submissionService.rejectSubmission(id, feedback);
        return ResponseEntity.ok(rejectedSubmission);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete submission")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
