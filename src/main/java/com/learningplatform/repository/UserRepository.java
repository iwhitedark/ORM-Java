package com.learningplatform.repository;

import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.enrollments WHERE u.id = :id")
    Optional<User> findByIdWithEnrollments(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.enrollments e " +
            "LEFT JOIN FETCH e.course " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithEnrolledCourses(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' ORDER BY u.name")
    List<User> findAllTeachers();

    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' ORDER BY u.name")
    List<User> findAllStudents();
}
