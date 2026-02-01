package com.learningplatform.dto;

import com.learningplatform.entity.SubmissionStatus;
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
public class SubmissionDTO {

    private Long id;

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Student ID is required")
    private Long studentId;
    private String studentName;

    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    private SubmissionStatus status;
    private LocalDateTime reviewedAt;

    private String assignmentTitle;
}
