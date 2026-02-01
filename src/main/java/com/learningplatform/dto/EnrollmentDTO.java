package com.learningplatform.dto;

import com.learningplatform.entity.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;
    private String studentName;

    @NotNull(message = "Course ID is required")
    private Long courseId;
    private String courseTitle;

    private LocalDateTime enrollDate;
    private EnrollmentStatus status;
    private Integer progress;
    private LocalDateTime completedAt;
}
