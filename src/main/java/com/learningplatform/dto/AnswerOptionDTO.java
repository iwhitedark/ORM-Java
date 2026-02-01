package com.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerOptionDTO {

    private Long id;

    @NotBlank(message = "Answer text is required")
    private String text;

    private Boolean isCorrect;

    @NotNull(message = "Question ID is required")
    private Long questionId;
}
