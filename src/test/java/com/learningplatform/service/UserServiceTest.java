package com.learningplatform.service;

import com.learningplatform.dto.UserDTO;
import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;
import com.learningplatform.exception.DuplicateResourceException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.ProfileRepository;
import com.learningplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .role(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userDTO = UserDTO.builder()
                .name("Test User")
                .email("test@test.com")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(userDTO))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldGetAllUsers() {
        User user2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@test.com")
                .role(Role.TEACHER)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldGetUsersByRole() {
        when(userRepository.findByRole(Role.STUDENT)).thenReturn(Arrays.asList(user));

        List<UserDTO> result = userService.getUsersByRole(Role.STUDENT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void shouldUpdateUser() {
        UserDTO updateDTO = UserDTO.builder()
                .name("Updated Name")
                .email("test@test.com")
                .role(Role.STUDENT)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
