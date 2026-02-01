package com.learningplatform.repository;

import com.learningplatform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByModuleId(Long moduleId);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);

    @Query("SELECT DISTINCT q FROM Quiz q " +
            "LEFT JOIN FETCH q.questions quest " +
            "LEFT JOIN FETCH quest.options " +
            "WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestionsAndOptions(@Param("id") Long id);

    @Query("SELECT q FROM Quiz q " +
            "JOIN q.module m " +
            "WHERE m.course.id = :courseId")
    List<Quiz> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.submissions WHERE q.id = :id")
    Optional<Quiz> findByIdWithSubmissions(@Param("id") Long id);
}
