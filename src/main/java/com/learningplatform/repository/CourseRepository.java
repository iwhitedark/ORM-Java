package com.learningplatform.repository;

import com.learningplatform.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByCategoryId(Long categoryId);

    List<Course> findByIsPublishedTrue();

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Optional<Course> findByIdWithModules(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.modules m " +
            "LEFT JOIN FETCH m.lessons " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithModulesAndLessons(@Param("id") Long id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.enrollments WHERE c.id = :id")
    Optional<Course> findByIdWithEnrollments(@Param("id") Long id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.tags WHERE c.id = :id")
    Optional<Course> findByIdWithTags(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.teacher " +
            "LEFT JOIN FETCH c.category " +
            "WHERE c.isPublished = true")
    List<Course> findAllPublishedWithDetails();

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.teacher " +
            "LEFT JOIN FETCH c.category " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c " +
            "JOIN c.tags t " +
            "WHERE t.name IN :tagNames")
    List<Course> findByTagNames(@Param("tagNames") List<String> tagNames);

    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByTitle(@Param("keyword") String keyword);
}
