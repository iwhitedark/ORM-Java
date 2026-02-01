package com.learningplatform.repository;

import com.learningplatform.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.id = :id")
    Optional<Module> findByIdWithLessons(@Param("id") Long id);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.quiz WHERE m.id = :id")
    Optional<Module> findByIdWithQuiz(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Module m " +
            "LEFT JOIN FETCH m.lessons l " +
            "LEFT JOIN FETCH l.assignments " +
            "WHERE m.id = :id")
    Optional<Module> findByIdWithLessonsAndAssignments(@Param("id") Long id);

    @Query("SELECT MAX(m.orderIndex) FROM Module m WHERE m.course.id = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(m) FROM Module m WHERE m.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
}
