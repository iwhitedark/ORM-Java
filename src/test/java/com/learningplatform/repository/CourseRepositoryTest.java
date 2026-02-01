package com.learningplatform.repository;

import com.learningplatform.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    private User teacher;
    private Category category;
    private Course course;
    private Tag javaTag;

    @BeforeEach
    void setUp() {
        teacher = User.builder()
                .name("Teacher")
                .email("teacher@test.com")
                .role(Role.TEACHER)
                .build();
        teacher = userRepository.save(teacher);

        category = Category.builder()
                .name("Programming")
                .description("Programming courses")
                .build();
        category = categoryRepository.save(category);

        javaTag = Tag.builder().name("Java").build();
        javaTag = tagRepository.save(javaTag);

        course = Course.builder()
                .title("Java Basics")
                .description("Learn Java programming")
                .duration(40)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .isPublished(true)
                .teacher(teacher)
                .category(category)
                .tags(new HashSet<>())
                .build();
        course.getTags().add(javaTag);
        course = courseRepository.save(course);
    }

    @Test
    void shouldSaveCourse() {
        Course newCourse = Course.builder()
                .title("New Course")
                .description("Description")
                .teacher(teacher)
                .category(category)
                .isPublished(false)
                .build();

        Course saved = courseRepository.save(newCourse);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("New Course");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindCoursesByTeacher() {
        List<Course> courses = courseRepository.findByTeacherId(teacher.getId());

        assertThat(courses).hasSize(1);
        assertThat(courses.get(0).getTitle()).isEqualTo("Java Basics");
    }

    @Test
    void shouldFindCoursesByCategory() {
        List<Course> courses = courseRepository.findByCategoryId(category.getId());

        assertThat(courses).hasSize(1);
    }

    @Test
    void shouldFindPublishedCourses() {
        List<Course> publishedCourses = courseRepository.findByIsPublishedTrue();

        assertThat(publishedCourses).hasSize(1);
    }

    @Test
    void shouldSearchByTitle() {
        List<Course> found = courseRepository.searchByTitle("Java");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).contains("Java");
    }

    @Test
    void shouldFindCourseWithModules() {
        Module module = Module.builder()
                .title("Module 1")
                .course(course)
                .orderIndex(0)
                .build();
        moduleRepository.save(module);

        Optional<Course> found = courseRepository.findByIdWithModules(course.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getModules()).hasSize(1);
    }

    @Test
    void shouldFindCourseWithDetails() {
        Optional<Course> found = courseRepository.findByIdWithDetails(course.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTeacher()).isNotNull();
        assertThat(found.get().getCategory()).isNotNull();
    }

    @Test
    void shouldUpdateCourse() {
        course.setTitle("Updated Title");
        course.setDescription("Updated Description");

        Course updated = courseRepository.save(course);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteCourse() {
        courseRepository.delete(course);

        Optional<Course> deleted = courseRepository.findById(course.getId());
        assertThat(deleted).isEmpty();
    }
}
