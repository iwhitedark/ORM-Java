package com.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
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
public class AssignmentDTO {

    private Long id;

    @NotBlank(message = "Assignment title is required")
    private String title;

    private String description;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private LocalDateTime createdAt;

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    private int submissionCount;
    private Double averageScore;
}
