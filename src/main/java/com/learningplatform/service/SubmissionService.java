package com.learningplatform.service;

import com.learningplatform.dto.SubmissionDTO;
import com.learningplatform.entity.*;
import com.learningplatform.exception.BusinessLogicException;
import com.learningplatform.exception.DuplicateResourceException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.AssignmentRepository;
import com.learningplatform.repository.SubmissionRepository;
import com.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public SubmissionDTO submitAssignment(SubmissionDTO submissionDTO) {
        log.info("Student {} submitting assignment {}", submissionDTO.getStudentId(), submissionDTO.getAssignmentId());

        User student = userRepository.findById(submissionDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", submissionDTO.getStudentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new BusinessLogicException("Only students can submit assignments");
        }

        Assignment assignment = assignmentRepository.findById(submissionDTO.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", submissionDTO.getAssignmentId()));

        // Check if student already submitted
        if (submissionRepository.existsByStudentIdAndAssignmentId(submissionDTO.getStudentId(), submissionDTO.getAssignmentId())) {
            throw new DuplicateResourceException("Submission already exists for this student and assignment");
        }

        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .content(submissionDTO.getContent())
                .fileUrl(submissionDTO.getFileUrl())
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.SUBMITTED)
                .build();

        Submission savedSubmission = submissionRepository.save(submission);
        log.info("Submission created with ID: {}", savedSubmission.getId());

        return mapToDTO(savedSubmission);
    }

    @Transactional(readOnly = true)
    public SubmissionDTO getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
        return mapToDTO(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentIdWithDetails(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdWithStudent(assignmentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> getPendingSubmissions() {
        return submissionRepository.findByStatus(SubmissionStatus.SUBMITTED).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SubmissionDTO gradeSubmission(Long submissionId, Integer score, String feedback) {
        log.info("Grading submission {}: score={}", submissionId, score);

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        if (score < 0 || score > submission.getAssignment().getMaxScore()) {
            throw new BusinessLogicException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.REVIEWED);
        submission.setReviewedAt(LocalDateTime.now());

        Submission gradedSubmission = submissionRepository.save(submission);
        log.info("Submission graded with ID: {}", gradedSubmission.getId());

        return mapToDTO(gradedSubmission);
    }

    public SubmissionDTO acceptSubmission(Long submissionId) {
        log.info("Accepting submission {}", submissionId);

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        submission.setStatus(SubmissionStatus.ACCEPTED);

        Submission acceptedSubmission = submissionRepository.save(submission);
        return mapToDTO(acceptedSubmission);
    }

    public SubmissionDTO rejectSubmission(Long submissionId, String feedback) {
        log.info("Rejecting submission {}", submissionId);

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        submission.setStatus(SubmissionStatus.REJECTED);
        submission.setFeedback(feedback);
        submission.setReviewedAt(LocalDateTime.now());

        Submission rejectedSubmission = submissionRepository.save(submission);
        return mapToDTO(rejectedSubmission);
    }

    public void deleteSubmission(Long id) {
        log.info("Deleting submission with ID: {}", id);

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));

        submissionRepository.delete(submission);
        log.info("Submission deleted with ID: {}", id);
    }

    private SubmissionDTO mapToDTO(Submission submission) {
        return SubmissionDTO.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getName())
                .content(submission.getContent())
                .fileUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .status(submission.getStatus())
                .reviewedAt(submission.getReviewedAt())
                .build();
    }
}
