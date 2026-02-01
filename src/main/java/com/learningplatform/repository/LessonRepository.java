package com.learningplatform.repository;

import com.learningplatform.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.id = :id")
    Optional<Lesson> findByIdWithAssignments(@Param("id") Long id);

    @Query("SELECT MAX(l.orderIndex) FROM Lesson l WHERE l.module.id = :moduleId")
    Integer findMaxOrderIndexByModuleId(@Param("moduleId") Long moduleId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.module.id = :moduleId")
    long countByModuleId(@Param("moduleId") Long moduleId);

    @Query("SELECT l FROM Lesson l " +
            "JOIN l.module m " +
            "WHERE m.course.id = :courseId " +
            "ORDER BY m.orderIndex, l.orderIndex")
    List<Lesson> findByCourseIdOrdered(@Param("courseId") Long courseId);
}
