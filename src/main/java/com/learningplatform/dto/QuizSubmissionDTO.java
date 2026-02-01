package com.learningplatform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {

    private Long id;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;
    private String quizTitle;

    @NotNull(message = "Student ID is required")
    private Long studentId;
    private String studentName;

    private Integer score;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Boolean passed;
    private LocalDateTime takenAt;
    private Integer timeSpent;

    // For submitting answers: questionId -> selectedOptionId
    private Map<Long, Long> answers;
}
