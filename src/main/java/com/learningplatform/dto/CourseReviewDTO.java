package com.learningplatform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class CourseReviewDTO {

    private Long id;

    @NotNull(message = "Course ID is required")
    private Long courseId;
    private String courseTitle;

    @NotNull(message = "Student ID is required")
    private Long studentId;
    private String studentName;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
