package com.learningplatform.service;

import com.learningplatform.dto.AssignmentDTO;
import com.learningplatform.dto.LessonDTO;
import com.learningplatform.entity.Assignment;
import com.learningplatform.entity.Lesson;
import com.learningplatform.entity.Module;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.LessonRepository;
import com.learningplatform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonDTO createLesson(LessonDTO lessonDTO) {
        log.info("Creating lesson: {} for module {}", lessonDTO.getTitle(), lessonDTO.getModuleId());

        Module module = moduleRepository.findById(lessonDTO.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", lessonDTO.getModuleId()));

        Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByModuleId(lessonDTO.getModuleId());
        int newOrderIndex = (maxOrderIndex != null ? maxOrderIndex : -1) + 1;

        Lesson lesson = Lesson.builder()
                .title(lessonDTO.getTitle())
                .content(lessonDTO.getContent())
                .videoUrl(lessonDTO.getVideoUrl())
                .orderIndex(lessonDTO.getOrderIndex() != null ? lessonDTO.getOrderIndex() : newOrderIndex)
                .duration(lessonDTO.getDuration())
                .module(module)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Lesson created with ID: {}", savedLesson.getId());

        return mapToDTO(savedLesson);
    }

    @Transactional(readOnly = true)
    public LessonDTO getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        return mapToDTO(lesson);
    }

    @Transactional(readOnly = true)
    public LessonDTO getLessonWithAssignments(Long id) {
        Lesson lesson = lessonRepository.findByIdWithAssignments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        return mapToDTOWithAssignments(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> getLessonsByModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrdered(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LessonDTO updateLesson(Long id, LessonDTO lessonDTO) {
        log.info("Updating lesson with ID: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        lesson.setTitle(lessonDTO.getTitle());
        lesson.setContent(lessonDTO.getContent());
        lesson.setVideoUrl(lessonDTO.getVideoUrl());
        lesson.setDuration(lessonDTO.getDuration());

        if (lessonDTO.getOrderIndex() != null) {
            lesson.setOrderIndex(lessonDTO.getOrderIndex());
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToDTO(updatedLesson);
    }

    public void deleteLesson(Long id) {
        log.info("Deleting lesson with ID: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        lessonRepository.delete(lesson);
        log.info("Lesson deleted with ID: {}", id);
    }

    private LessonDTO mapToDTO(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .orderIndex(lesson.getOrderIndex())
                .duration(lesson.getDuration())
                .moduleId(lesson.getModule().getId())
                .build();
    }

    private LessonDTO mapToDTOWithAssignments(Lesson lesson) {
        LessonDTO dto = mapToDTO(lesson);

        if (lesson.getAssignments() != null) {
            List<AssignmentDTO> assignmentDTOs = lesson.getAssignments().stream()
                    .map(this::mapAssignmentToDTO)
                    .collect(Collectors.toList());
            dto.setAssignments(assignmentDTOs);
        }

        return dto;
    }

    private AssignmentDTO mapAssignmentToDTO(Assignment assignment) {
        return AssignmentDTO.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .maxScore(assignment.getMaxScore())
                .createdAt(assignment.getCreatedAt())
                .lessonId(assignment.getLesson().getId())
                .build();
    }
}
