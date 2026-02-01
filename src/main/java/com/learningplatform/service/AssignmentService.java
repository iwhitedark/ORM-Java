package com.learningplatform.service;

import com.learningplatform.dto.AssignmentDTO;
import com.learningplatform.entity.Assignment;
import com.learningplatform.entity.Lesson;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.AssignmentRepository;
import com.learningplatform.repository.LessonRepository;
import com.learningplatform.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;

    public AssignmentDTO createAssignment(AssignmentDTO assignmentDTO) {
        log.info("Creating assignment: {} for lesson {}", assignmentDTO.getTitle(), assignmentDTO.getLessonId());

        Lesson lesson = lessonRepository.findById(assignmentDTO.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", assignmentDTO.getLessonId()));

        Assignment assignment = Assignment.builder()
                .title(assignmentDTO.getTitle())
                .description(assignmentDTO.getDescription())
                .dueDate(assignmentDTO.getDueDate())
                .maxScore(assignmentDTO.getMaxScore() != null ? assignmentDTO.getMaxScore() : 100)
                .lesson(lesson)
                .build();

        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment created with ID: {}", savedAssignment.getId());

        return mapToDTO(savedAssignment);
    }

    @Transactional(readOnly = true)
    public AssignmentDTO getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
        return mapToDTO(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsByLesson(Long lessonId) {
        return assignmentRepository.findByLessonId(lessonId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsForStudent(Long studentId) {
        return assignmentRepository.findByStudentEnrollment(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AssignmentDTO updateAssignment(Long id, AssignmentDTO assignmentDTO) {
        log.info("Updating assignment with ID: {}", id);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setDescription(assignmentDTO.getDescription());
        assignment.setDueDate(assignmentDTO.getDueDate());

        if (assignmentDTO.getMaxScore() != null) {
            assignment.setMaxScore(assignmentDTO.getMaxScore());
        }

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return mapToDTO(updatedAssignment);
    }

    public void deleteAssignment(Long id) {
        log.info("Deleting assignment with ID: {}", id);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        assignmentRepository.delete(assignment);
        log.info("Assignment deleted with ID: {}", id);
    }

    private AssignmentDTO mapToDTO(Assignment assignment) {
        AssignmentDTO dto = AssignmentDTO.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .maxScore(assignment.getMaxScore())
                .createdAt(assignment.getCreatedAt())
                .lessonId(assignment.getLesson().getId())
                .build();

        // Get submission statistics
        dto.setSubmissionCount((int) submissionRepository.countByAssignmentId(assignment.getId()));
        dto.setAverageScore(submissionRepository.getAverageScoreByAssignmentId(assignment.getId()));

        return dto;
    }
}
