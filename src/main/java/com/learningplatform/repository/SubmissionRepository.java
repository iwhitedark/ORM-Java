package com.learningplatform.repository;

import com.learningplatform.entity.Submission;
import com.learningplatform.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByStudentId(Long studentId);

    List<Submission> findByAssignmentId(Long assignmentId);

    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    boolean existsByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    List<Submission> findByStatus(SubmissionStatus status);

    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.assignment a " +
            "LEFT JOIN FETCH a.lesson " +
            "WHERE s.student.id = :studentId")
    List<Submission> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("SELECT s FROM Submission s " +
            "LEFT JOIN FETCH s.student " +
            "WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId")
    long countByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.score IS NOT NULL")
    Double getAverageScoreByAssignmentId(@Param("assignmentId") Long assignmentId);
}
