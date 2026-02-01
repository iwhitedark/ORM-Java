package com.learningplatform.integration;

import com.learningplatform.dto.CourseDTO;
import com.learningplatform.dto.EnrollmentDTO;
import com.learningplatform.dto.ModuleDTO;
import com.learningplatform.dto.UserDTO;
import com.learningplatform.entity.Role;
import com.learningplatform.service.CourseService;
import com.learningplatform.service.EnrollmentService;
import com.learningplatform.service.ModuleService;
import com.learningplatform.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Test
    void shouldCreateCourseWithModulesAndEnrollStudents() {
        // Create teacher
        UserDTO teacherDTO = UserDTO.builder()
                .name("Integration Teacher")
                .email("integration.teacher@test.com")
                .role(Role.TEACHER)
                .build();
        UserDTO teacher = userService.createUser(teacherDTO);

        // Create student
        UserDTO studentDTO = UserDTO.builder()
                .name("Integration Student")
                .email("integration.student@test.com")
                .role(Role.STUDENT)
                .build();
        UserDTO student = userService.createUser(studentDTO);

        // Create course
        CourseDTO courseDTO = CourseDTO.builder()
                .title("Integration Test Course")
                .description("Course for integration testing")
                .duration(20)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .teacherId(teacher.getId())
                .isPublished(true)
                .build();
        CourseDTO course = courseService.createCourse(courseDTO);

        assertThat(course.getId()).isNotNull();
        assertThat(course.getTitle()).isEqualTo("Integration Test Course");

        // Add modules
        ModuleDTO module1DTO = ModuleDTO.builder()
                .title("Module 1")
                .description("First module")
                .courseId(course.getId())
                .build();
        ModuleDTO module1 = moduleService.createModule(module1DTO);

        ModuleDTO module2DTO = ModuleDTO.builder()
                .title("Module 2")
                .description("Second module")
                .courseId(course.getId())
                .build();
        ModuleDTO module2 = moduleService.createModule(module2DTO);

        List<ModuleDTO> modules = moduleService.getModulesByCourse(course.getId());
        assertThat(modules).hasSize(2);

        // Enroll student
        EnrollmentDTO enrollment = enrollmentService.enrollStudent(student.getId(), course.getId());

        assertThat(enrollment.getId()).isNotNull();
        assertThat(enrollment.getStudentId()).isEqualTo(student.getId());
        assertThat(enrollment.getCourseId()).isEqualTo(course.getId());

        // Verify enrollment
        boolean isEnrolled = enrollmentService.isStudentEnrolled(student.getId(), course.getId());
        assertThat(isEnrolled).isTrue();

        // Update progress
        EnrollmentDTO updatedEnrollment = enrollmentService.updateProgress(enrollment.getId(), 50);
        assertThat(updatedEnrollment.getProgress()).isEqualTo(50);

        // Complete enrollment
        EnrollmentDTO completedEnrollment = enrollmentService.completeEnrollment(enrollment.getId());
        assertThat(completedEnrollment.getProgress()).isEqualTo(100);
    }

    @Test
    void shouldSearchCourses() {
        // Create teacher
        UserDTO teacherDTO = UserDTO.builder()
                .name("Search Teacher")
                .email("search.teacher@test.com")
                .role(Role.TEACHER)
                .build();
        UserDTO teacher = userService.createUser(teacherDTO);

        // Create courses
        CourseDTO javaDTO = CourseDTO.builder()
                .title("Java Programming")
                .description("Learn Java")
                .teacherId(teacher.getId())
                .isPublished(true)
                .build();
        courseService.createCourse(javaDTO);

        CourseDTO pythonDTO = CourseDTO.builder()
                .title("Python Programming")
                .description("Learn Python")
                .teacherId(teacher.getId())
                .isPublished(true)
                .build();
        courseService.createCourse(pythonDTO);

        // Search
        List<CourseDTO> javaResults = courseService.searchCourses("Java");
        assertThat(javaResults).hasSize(1);
        assertThat(javaResults.get(0).getTitle()).contains("Java");

        List<CourseDTO> programmingResults = courseService.searchCourses("Programming");
        assertThat(programmingResults).hasSize(2);
    }
}
