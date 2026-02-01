package com.learningplatform.service;

import com.learningplatform.dto.LessonDTO;
import com.learningplatform.dto.ModuleDTO;
import com.learningplatform.entity.Course;
import com.learningplatform.entity.Lesson;
import com.learningplatform.entity.Module;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.CourseRepository;
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
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleDTO createModule(ModuleDTO moduleDTO) {
        log.info("Creating module: {} for course {}", moduleDTO.getTitle(), moduleDTO.getCourseId());

        Course course = courseRepository.findById(moduleDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", moduleDTO.getCourseId()));

        Integer maxOrderIndex = moduleRepository.findMaxOrderIndexByCourseId(moduleDTO.getCourseId());
        int newOrderIndex = (maxOrderIndex != null ? maxOrderIndex : -1) + 1;

        Module module = Module.builder()
                .title(moduleDTO.getTitle())
                .description(moduleDTO.getDescription())
                .orderIndex(moduleDTO.getOrderIndex() != null ? moduleDTO.getOrderIndex() : newOrderIndex)
                .course(course)
                .build();

        Module savedModule = moduleRepository.save(module);
        log.info("Module created with ID: {}", savedModule.getId());

        return mapToDTO(savedModule);
    }

    @Transactional(readOnly = true)
    public ModuleDTO getModuleById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));
        return mapToDTO(module);
    }

    @Transactional(readOnly = true)
    public ModuleDTO getModuleWithLessons(Long id) {
        Module module = moduleRepository.findByIdWithLessons(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));
        return mapToDTOWithLessons(module);
    }

    @Transactional(readOnly = true)
    public List<ModuleDTO> getModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ModuleDTO updateModule(Long id, ModuleDTO moduleDTO) {
        log.info("Updating module with ID: {}", id);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));

        module.setTitle(moduleDTO.getTitle());
        module.setDescription(moduleDTO.getDescription());

        if (moduleDTO.getOrderIndex() != null) {
            module.setOrderIndex(moduleDTO.getOrderIndex());
        }

        Module updatedModule = moduleRepository.save(module);
        return mapToDTO(updatedModule);
    }

    public void deleteModule(Long id) {
        log.info("Deleting module with ID: {}", id);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));

        moduleRepository.delete(module);
        log.info("Module deleted with ID: {}", id);
    }

    private ModuleDTO mapToDTO(Module module) {
        return ModuleDTO.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .courseId(module.getCourse().getId())
                .build();
    }

    private ModuleDTO mapToDTOWithLessons(Module module) {
        ModuleDTO dto = mapToDTO(module);

        if (module.getLessons() != null) {
            List<LessonDTO> lessonDTOs = module.getLessons().stream()
                    .map(this::mapLessonToDTO)
                    .collect(Collectors.toList());
            dto.setLessons(lessonDTOs);
        }

        return dto;
    }

    private LessonDTO mapLessonToDTO(Lesson lesson) {
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
}
