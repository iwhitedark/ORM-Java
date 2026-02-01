package com.learningplatform.repository;

import com.learningplatform.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    List<QuizSubmission> findByStudentId(Long studentId);

    List<QuizSubmission> findByQuizId(Long quizId);

    Optional<QuizSubmission> findTopByStudentIdAndQuizIdOrderByTakenAtDesc(Long studentId, Long quizId);

    List<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Long quizId);

    @Query("SELECT qs FROM QuizSubmission qs " +
            "LEFT JOIN FETCH qs.quiz q " +
            "LEFT JOIN FETCH q.module " +
            "WHERE qs.student.id = :studentId")
    List<QuizSubmission> findByStudentIdWithQuizDetails(@Param("studentId") Long studentId);

    @Query("SELECT AVG(qs.score) FROM QuizSubmission qs WHERE qs.quiz.id = :quizId")
    Double getAverageScoreByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(qs) FROM QuizSubmission qs WHERE qs.quiz.id = :quizId AND qs.passed = true")
    long countPassedByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(qs) FROM QuizSubmission qs WHERE qs.quiz.id = :quizId")
    long countByQuizId(@Param("quizId") Long quizId);
}
