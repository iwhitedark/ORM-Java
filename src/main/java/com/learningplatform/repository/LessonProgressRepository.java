package com.learningplatform.repository;

import com.learningplatform.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByStudentId(Long studentId);

    List<LessonProgress> findByLessonId(Long lessonId);

    Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    boolean existsByStudentIdAndLessonId(Long studentId, Long lessonId);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp " +
            "WHERE lp.student.id = :studentId " +
            "AND lp.lesson.module.course.id = :courseId " +
            "AND lp.isCompleted = true")
    long countCompletedLessonsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("SELECT lp FROM LessonProgress lp " +
            "JOIN lp.lesson l " +
            "JOIN l.module m " +
            "WHERE lp.student.id = :studentId AND m.course.id = :courseId")
    List<LessonProgress> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
