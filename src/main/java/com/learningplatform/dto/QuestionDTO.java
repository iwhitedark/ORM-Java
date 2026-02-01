package com.learningplatform.dto;

import com.learningplatform.entity.QuestionType;
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
public class QuestionDTO {

    private Long id;

    @NotBlank(message = "Question text is required")
    private String text;

    private QuestionType type;
    private Integer points;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private List<AnswerOptionDTO> options;
}
