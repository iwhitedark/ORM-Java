package com.learningplatform.repository;

import com.learningplatform.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByStudentId(Long studentId);

    List<Certificate> findByCourseId(Long courseId);

    Optional<Certificate> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Certificate> findByCertificateNumber(String certificateNumber);

    @Query("SELECT c FROM Certificate c " +
            "LEFT JOIN FETCH c.course " +
            "WHERE c.student.id = :studentId")
    List<Certificate> findByStudentIdWithCourse(@Param("studentId") Long studentId);
}
