package com.learningplatform.service;

import com.learningplatform.dto.CourseDTO;
import com.learningplatform.entity.*;
import com.learningplatform.exception.BusinessLogicException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseReviewRepository courseReviewRepository;

    @InjectMocks
    private CourseService courseService;

    private User teacher;
    private Category category;
    private Course course;
    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        teacher = User.builder()
                .id(1L)
                .name("Teacher")
                .email("teacher@test.com")
                .role(Role.TEACHER)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Programming")
                .build();

        course = Course.builder()
                .id(1L)
                .title("Test Course")
                .description("Description")
                .duration(40)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .isPublished(true)
                .teacher(teacher)
                .category(category)
                .tags(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        courseDTO = CourseDTO.builder()
                .title("Test Course")
                .description("Description")
                .duration(40)
                .teacherId(1L)
                .categoryId(1L)
                .build();
    }

    @Test
    void shouldCreateCourse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(enrollmentRepository.countByCourseId(any())).thenReturn(0L);
        when(courseReviewRepository.getAverageRatingByCourseId(any())).thenReturn(null);

        CourseDTO result = courseService.createCourse(courseDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Course");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldThrowExceptionWhenTeacherNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.createCourse(courseDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotTeacher() {
        User student = User.builder()
                .id(1L)
                .name("Student")
                .role(Role.STUDENT)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> courseService.createCourse(courseDTO))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Only teachers or admins can create courses");
    }

    @Test
    void shouldGetCourseById() {
        when(courseRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.countByCourseId(1L)).thenReturn(5L);
        when(courseReviewRepository.getAverageRatingByCourseId(1L)).thenReturn(4.5);

        CourseDTO result = courseService.getCourseById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEnrollmentCount()).isEqualTo(5);
        assertThat(result.getAverageRating()).isEqualTo(4.5);
    }

    @Test
    void shouldPublishCourse() {
        course.setIsPublished(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(enrollmentRepository.countByCourseId(any())).thenReturn(0L);
        when(courseReviewRepository.getAverageRatingByCourseId(any())).thenReturn(null);

        CourseDTO result = courseService.publishCourse(1L);

        assertThat(result).isNotNull();
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldDeleteCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).delete(course);

        courseService.deleteCourse(1L);

        verify(courseRepository).delete(course);
    }
}
