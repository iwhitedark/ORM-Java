package com.learningplatform.service;

import com.learningplatform.dto.CourseReviewDTO;
import com.learningplatform.entity.*;
import com.learningplatform.exception.BusinessLogicException;
import com.learningplatform.exception.DuplicateResourceException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.CourseReviewRepository;
import com.learningplatform.repository.EnrollmentRepository;
import com.learningplatform.repository.UserRepository;
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
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseReviewDTO createReview(CourseReviewDTO reviewDTO) {
        log.info("Creating review for course {} by student {}", reviewDTO.getCourseId(), reviewDTO.getStudentId());

        User student = userRepository.findById(reviewDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", reviewDTO.getStudentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new BusinessLogicException("Only students can review courses");
        }

        Course course = courseRepository.findById(reviewDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", reviewDTO.getCourseId()));

        // Check if student is enrolled in the course
        if (!enrollmentRepository.existsByStudentIdAndCourseId(reviewDTO.getStudentId(), reviewDTO.getCourseId())) {
            throw new BusinessLogicException("Student must be enrolled in the course to leave a review");
        }

        // Check if student already reviewed
        if (courseReviewRepository.existsByStudentIdAndCourseId(reviewDTO.getStudentId(), reviewDTO.getCourseId())) {
            throw new DuplicateResourceException("Review already exists for this student and course");
        }

        CourseReview review = CourseReview.builder()
                .course(course)
                .student(student)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();

        CourseReview savedReview = courseReviewRepository.save(review);
        log.info("Review created with ID: {}", savedReview.getId());

        return mapToDTO(savedReview);
    }

    @Transactional(readOnly = true)
    public CourseReviewDTO getReviewById(Long id) {
        CourseReview review = courseReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseReview", "id", id));
        return mapToDTO(review);
    }

    @Transactional(readOnly = true)
    public List<CourseReviewDTO> getReviewsByCourse(Long courseId) {
        return courseReviewRepository.findByCourseIdWithStudentOrderByCreatedAtDesc(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseReviewDTO> getReviewsByStudent(Long studentId) {
        return courseReviewRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long courseId) {
        return courseReviewRepository.getAverageRatingByCourseId(courseId);
    }

    public CourseReviewDTO updateReview(Long id, CourseReviewDTO reviewDTO) {
        log.info("Updating review with ID: {}", id);

        CourseReview review = courseReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseReview", "id", id));

        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        CourseReview updatedReview = courseReviewRepository.save(review);
        return mapToDTO(updatedReview);
    }

    public void deleteReview(Long id) {
        log.info("Deleting review with ID: {}", id);

        CourseReview review = courseReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseReview", "id", id));

        courseReviewRepository.delete(review);
        log.info("Review deleted with ID: {}", id);
    }

    private CourseReviewDTO mapToDTO(CourseReview review) {
        return CourseReviewDTO.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .courseTitle(review.getCourse().getTitle())
                .studentId(review.getStudent().getId())
                .studentName(review.getStudent().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
