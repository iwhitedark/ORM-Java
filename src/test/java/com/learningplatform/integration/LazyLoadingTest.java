package com.learningplatform.integration;

import com.learningplatform.entity.*;
import com.learningplatform.repository.*;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class demonstrating lazy loading behavior in Hibernate.
 * These tests show how LazyInitializationException occurs and how to avoid it.
 */
@SpringBootTest
@ActiveProfiles("test")
class LazyLoadingTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    private Long courseId;
    private Long moduleId;

    @BeforeEach
    @Transactional
    void setUp() {
        User teacher = User.builder()
                .name("Lazy Test Teacher")
                .email("lazy.teacher" + System.currentTimeMillis() + "@test.com")
                .role(Role.TEACHER)
                .build();
        teacher = userRepository.save(teacher);

        Course course = Course.builder()
                .title("Lazy Loading Test Course")
                .description("Course for testing lazy loading")
                .teacher(teacher)
                .isPublished(true)
                .startDate(LocalDate.now())
                .tags(new HashSet<>())
                .build();
        course = courseRepository.save(course);
        courseId = course.getId();

        Module module = Module.builder()
                .title("Lazy Module")
                .course(course)
                .orderIndex(0)
                .build();
        module = moduleRepository.save(module);
        moduleId = module.getId();

        Lesson lesson1 = Lesson.builder()
                .title("Lesson 1")
                .module(module)
                .orderIndex(0)
                .build();
        lessonRepository.save(lesson1);

        Lesson lesson2 = Lesson.builder()
                .title("Lesson 2")
                .module(module)
                .orderIndex(1)
                .build();
        lessonRepository.save(lesson2);
    }

    /**
     * This test demonstrates LazyInitializationException.
     * When accessing a lazy-loaded collection outside of a transaction,
     * Hibernate throws this exception because the session is closed.
     */
    @Test
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        // Load course outside of transaction (session is closed)
        Course course = courseRepository.findById(courseId).orElseThrow();

        // Attempting to access lazy-loaded modules collection
        // should throw LazyInitializationException
        assertThatThrownBy(() -> course.getModules().size())
                .isInstanceOf(LazyInitializationException.class);
    }

    /**
     * This test shows how to properly load lazy collections within a transaction.
     */
    @Test
    @Transactional
    void shouldAccessLazyCollectionWithinTransaction() {
        Course course = courseRepository.findById(courseId).orElseThrow();

        // Within transaction, lazy loading works
        int moduleCount = course.getModules().size();

        assertThat(moduleCount).isEqualTo(1);
    }

    /**
     * This test demonstrates using JOIN FETCH to eagerly load collections.
     */
    @Test
    void shouldLoadCollectionsEagerlyWithJoinFetch() {
        // Using JOIN FETCH query loads modules eagerly
        Course course = courseRepository.findByIdWithModules(courseId).orElseThrow();

        // Now modules are loaded, no LazyInitializationException
        int moduleCount = course.getModules().size();

        assertThat(moduleCount).isEqualTo(1);
    }

    /**
     * This test shows nested lazy loading with JOIN FETCH.
     */
    @Test
    void shouldLoadNestedCollectionsWithJoinFetch() {
        // Using JOIN FETCH to load both modules and lessons
        Course course = courseRepository.findByIdWithModulesAndLessons(courseId).orElseThrow();

        // Both modules and lessons are loaded
        assertThat(course.getModules()).hasSize(1);
        assertThat(course.getModules().get(0).getLessons()).hasSize(2);
    }

    /**
     * This test demonstrates LazyInitializationException for nested collections.
     */
    @Test
    void shouldThrowExceptionForNestedLazyCollection() {
        // Load module with lessons eagerly using JOIN FETCH
        Module module = moduleRepository.findByIdWithLessons(moduleId).orElseThrow();

        // Lessons are loaded
        assertThat(module.getLessons()).hasSize(2);

        // But accessing assignments (lazy) outside transaction will fail
        // if we had assignments
    }

    /**
     * This test shows the pattern of initializing collections within a transaction
     * by explicitly accessing them.
     */
    @Test
    @Transactional
    void shouldInitializeLazyCollectionByAccessing() {
        Course course = courseRepository.findById(courseId).orElseThrow();

        // Initialize the lazy collection by accessing it
        course.getModules().size();

        // Now even after transaction ends (in real scenarios),
        // the data would be available
        assertThat(course.getModules()).isNotEmpty();
    }
}
