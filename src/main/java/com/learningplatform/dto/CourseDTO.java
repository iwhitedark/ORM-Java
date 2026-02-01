package com.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long id;

    @NotBlank(message = "Course title is required")
    private String title;

    private String description;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;

    private Set<String> tags;
    private List<ModuleDTO> modules;

    private int enrollmentCount;
    private Double averageRating;
}
