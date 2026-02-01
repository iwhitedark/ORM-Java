package com.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {

    private Long id;

    @NotBlank(message = "Module title is required")
    private String title;

    private String description;
    private Integer orderIndex;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private List<LessonDTO> lessons;
    private QuizDTO quiz;
}
