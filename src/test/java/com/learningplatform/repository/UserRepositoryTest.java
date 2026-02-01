package com.learningplatform.repository;

import com.learningplatform.entity.Profile;
import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private User teacher;
    private User student;

    @BeforeEach
    void setUp() {
        teacher = User.builder()
                .name("John Teacher")
                .email("john@test.com")
                .role(Role.TEACHER)
                .build();
        teacher = userRepository.save(teacher);

        student = User.builder()
                .name("Jane Student")
                .email("jane@test.com")
                .role(Role.STUDENT)
                .build();
        student = userRepository.save(student);
    }

    @Test
    void shouldSaveUser() {
        User newUser = User.builder()
                .name("New User")
                .email("new@test.com")
                .role(Role.STUDENT)
                .build();

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New User");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindUserByEmail() {
        Optional<User> found = userRepository.findByEmail("john@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Teacher");
    }

    @Test
    void shouldFindUsersByRole() {
        List<User> teachers = userRepository.findByRole(Role.TEACHER);
        List<User> students = userRepository.findByRole(Role.STUDENT);

        assertThat(teachers).hasSize(1);
        assertThat(students).hasSize(1);
    }

    @Test
    void shouldFindAllTeachers() {
        List<User> teachers = userRepository.findAllTeachers();

        assertThat(teachers).hasSize(1);
        assertThat(teachers.get(0).getRole()).isEqualTo(Role.TEACHER);
    }

    @Test
    void shouldFindAllStudents() {
        List<User> students = userRepository.findAllStudents();

        assertThat(students).hasSize(1);
        assertThat(students.get(0).getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void shouldCheckIfEmailExists() {
        boolean exists = userRepository.existsByEmail("john@test.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@test.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldUpdateUser() {
        teacher.setName("Updated Name");
        User updated = userRepository.save(teacher);

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldDeleteUser() {
        userRepository.delete(student);

        Optional<User> deleted = userRepository.findById(student.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void shouldFindUserWithProfile() {
        Profile profile = Profile.builder()
                .user(teacher)
                .bio("Test bio")
                .build();
        profileRepository.save(profile);

        Optional<User> found = userRepository.findByIdWithProfile(teacher.getId());

        assertThat(found).isPresent();
    }
}
