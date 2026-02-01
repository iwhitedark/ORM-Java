package com.learningplatform.service;

import com.learningplatform.dto.EnrollmentDTO;
import com.learningplatform.entity.*;
import com.learningplatform.exception.BusinessLogicException;
import com.learningplatform.exception.DuplicateResourceException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.EnrollmentRepository;
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
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        log.info("Enrolling student {} to course {}", studentId, courseId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new BusinessLogicException("Only students can enroll in courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getIsPublished()) {
            throw new BusinessLogicException("Cannot enroll in unpublished course");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("Enrollment already exists for this student and course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .progress(0)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment created with ID: {}", savedEnrollment.getId());

        return mapToDTO(savedEnrollment);
    }

    @Transactional(readOnly = true)
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
        return mapToDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public EnrollmentDTO getEnrollmentByStudentAndCourse(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));
        return mapToDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdWithCourseDetails(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getActiveEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EnrollmentDTO updateProgress(Long enrollmentId, Integer progress) {
        log.info("Updating progress for enrollment {}: {}%", enrollmentId, progress);

        if (progress < 0 || progress > 100) {
            throw new BusinessLogicException("Progress must be between 0 and 100");
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        enrollment.setProgress(progress);

        if (progress == 100 && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            log.info("Enrollment {} marked as completed", enrollmentId);
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(updatedEnrollment);
    }

    public EnrollmentDTO completeEnrollment(Long enrollmentId) {
        log.info("Completing enrollment {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setProgress(100);
        enrollment.setCompletedAt(LocalDateTime.now());

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(updatedEnrollment);
    }

    public EnrollmentDTO dropEnrollment(Long enrollmentId) {
        log.info("Dropping enrollment {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        enrollment.setStatus(EnrollmentStatus.DROPPED);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(updatedEnrollment);
    }

    public void unenrollStudent(Long studentId, Long courseId) {
        log.info("Unenrolling student {} from course {}", studentId, courseId);

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));

        enrollmentRepository.delete(enrollment);
        log.info("Enrollment deleted");
    }

    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    private EnrollmentDTO mapToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getName())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .enrollDate(enrollment.getEnrollDate())
                .status(enrollment.getStatus())
                .progress(enrollment.getProgress())
                .completedAt(enrollment.getCompletedAt())
                .build();

        return dto;
    }
}
