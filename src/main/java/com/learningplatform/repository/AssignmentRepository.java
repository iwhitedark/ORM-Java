package com.learningplatform.repository;

import com.learningplatform.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByLessonId(Long lessonId);

    @Query("SELECT a FROM Assignment a LEFT JOIN FETCH a.submissions WHERE a.id = :id")
    Optional<Assignment> findByIdWithSubmissions(@Param("id") Long id);

    @Query("SELECT a FROM Assignment a " +
            "JOIN a.lesson l " +
            "JOIN l.module m " +
            "WHERE m.course.id = :courseId")
    List<Assignment> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :now AND a.dueDate IS NOT NULL")
    List<Assignment> findOverdueAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Assignment a " +
            "JOIN a.lesson l " +
            "JOIN l.module m " +
            "JOIN m.course c " +
            "JOIN c.enrollments e " +
            "WHERE e.student.id = :studentId")
    List<Assignment> findByStudentEnrollment(@Param("studentId") Long studentId);
}
