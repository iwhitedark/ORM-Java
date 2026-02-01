package com.learningplatform.repository;

import com.learningplatform.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    private User student;
    private User teacher;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        teacher = User.builder()
                .name("Teacher")
                .email("teacher@test.com")
                .role(Role.TEACHER)
                .build();
        teacher = userRepository.save(teacher);

        student = User.builder()
                .name("Student")
                .email("student@test.com")
                .role(Role.STUDENT)
                .build();
        student = userRepository.save(student);

        course = Course.builder()
                .title("Test Course")
                .description("Description")
                .teacher(teacher)
                .isPublished(true)
                .build();
        course = courseRepository.save(course);

        enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .progress(0)
                .build();
        enrollment = enrollmentRepository.save(enrollment);
    }

    @Test
    void shouldSaveEnrollment() {
        User newStudent = User.builder()
                .name("New Student")
                .email("newstudent@test.com")
                .role(Role.STUDENT)
                .build();
        newStudent = userRepository.save(newStudent);

        Enrollment newEnrollment = Enrollment.builder()
                .student(newStudent)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();

        Enrollment saved = enrollmentRepository.save(newEnrollment);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void shouldFindByStudentAndCourse() {
        Optional<Enrollment> found = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId());

        assertThat(found).isPresent();
    }

    @Test
    void shouldFindByStudent() {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        assertThat(enrollments).hasSize(1);
    }

    @Test
    void shouldFindByCourse() {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());

        assertThat(enrollments).hasSize(1);
    }

    @Test
    void shouldCheckIfEnrollmentExists() {
        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindByStatus() {
        List<Enrollment> activeEnrollments = enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE);

        assertThat(activeEnrollments).hasSize(1);
    }

    @Test
    void shouldCountByCourse() {
        long count = enrollmentRepository.countByCourseId(course.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldUpdateEnrollment() {
        enrollment.setProgress(50);
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletedAt(LocalDateTime.now());

        Enrollment updated = enrollmentRepository.save(enrollment);

        assertThat(updated.getProgress()).isEqualTo(50);
        assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    void shouldDeleteEnrollment() {
        enrollmentRepository.delete(enrollment);

        Optional<Enrollment> deleted = enrollmentRepository.findById(enrollment.getId());
        assertThat(deleted).isEmpty();
    }
}
