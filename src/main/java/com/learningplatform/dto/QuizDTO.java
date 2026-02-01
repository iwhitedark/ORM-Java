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
public class QuizDTO {

    private Long id;

    @NotBlank(message = "Quiz title is required")
    private String title;

    private String description;
    private Integer timeLimit;
    private Integer passingScore;

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    private List<QuestionDTO> questions;
    private int questionCount;
}
