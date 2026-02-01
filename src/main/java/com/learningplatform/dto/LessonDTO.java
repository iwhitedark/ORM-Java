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
public class LessonDTO {

    private Long id;

    @NotBlank(message = "Lesson title is required")
    private String title;

    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Integer duration;

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    private List<AssignmentDTO> assignments;
}
