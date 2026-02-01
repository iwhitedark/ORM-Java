package com.learningplatform.service;

import com.learningplatform.dto.CourseDTO;
import com.learningplatform.dto.ModuleDTO;
import com.learningplatform.entity.*;
import com.learningplatform.exception.BusinessLogicException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseReviewRepository courseReviewRepository;

    public CourseDTO createCourse(CourseDTO courseDTO) {
        log.info("Creating course: {}", courseDTO.getTitle());

        User teacher = userRepository.findById(courseDTO.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", courseDTO.getTeacherId()));

        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
            throw new BusinessLogicException("Only teachers or admins can create courses");
        }

        Category category = null;
        if (courseDTO.getCategoryId() != null) {
            category = categoryRepository.findById(courseDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", courseDTO.getCategoryId()));
        }

        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .duration(courseDTO.getDuration())
                .startDate(courseDTO.getStartDate())
                .endDate(courseDTO.getEndDate())
                .isPublished(courseDTO.getIsPublished() != null ? courseDTO.getIsPublished() : false)
                .teacher(teacher)
                .category(category)
                .build();

        // Add tags
        if (courseDTO.getTags() != null && !courseDTO.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : courseDTO.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                tags.add(tag);
            }
            course.setTags(tags);
        }

        Course savedCourse = courseRepository.save(course);
        log.info("Course created with ID: {}", savedCourse.getId());

        return mapToDTO(savedCourse);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return mapToDTO(course);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseWithModules(Long id) {
        Course course = courseRepository.findByIdWithModulesAndLessons(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return mapToDTOWithModules(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getPublishedCourses() {
        return courseRepository.findAllPublishedWithDetails().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> searchCourses(String keyword) {
        return courseRepository.searchByTitle(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        log.info("Updating course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setDuration(courseDTO.getDuration());
        course.setStartDate(courseDTO.getStartDate());
        course.setEndDate(courseDTO.getEndDate());

        if (courseDTO.getIsPublished() != null) {
            course.setIsPublished(courseDTO.getIsPublished());
        }

        if (courseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(courseDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", courseDTO.getCategoryId()));
            course.setCategory(category);
        }

        if (courseDTO.getTeacherId() != null) {
            User teacher = userRepository.findById(courseDTO.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", courseDTO.getTeacherId()));
            if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
                throw new BusinessLogicException("Only teachers or admins can be assigned to courses");
            }
            course.setTeacher(teacher);
        }

        // Update tags
        if (courseDTO.getTags() != null) {
            course.getTags().clear();
            for (String tagName : courseDTO.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                course.getTags().add(tag);
            }
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated with ID: {}", updatedCourse.getId());

        return mapToDTO(updatedCourse);
    }

    public CourseDTO publishCourse(Long id) {
        log.info("Publishing course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setIsPublished(true);
        Course updatedCourse = courseRepository.save(course);

        return mapToDTO(updatedCourse);
    }

    public CourseDTO unpublishCourse(Long id) {
        log.info("Unpublishing course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setIsPublished(false);
        Course updatedCourse = courseRepository.save(course);

        return mapToDTO(updatedCourse);
    }

    public void deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        courseRepository.delete(course);
        log.info("Course deleted with ID: {}", id);
    }

    private CourseDTO mapToDTO(Course course) {
        CourseDTO dto = CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .duration(course.getDuration())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .isPublished(course.getIsPublished())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();

        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }

        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
            dto.setTeacherName(course.getTeacher().getName());
        }

        // Get enrollment count
        dto.setEnrollmentCount((int) enrollmentRepository.countByCourseId(course.getId()));

        // Get average rating
        Double avgRating = courseReviewRepository.getAverageRatingByCourseId(course.getId());
        dto.setAverageRating(avgRating);

        return dto;
    }

    private CourseDTO mapToDTOWithModules(Course course) {
        CourseDTO dto = mapToDTO(course);

        if (course.getModules() != null) {
            List<ModuleDTO> moduleDTOs = course.getModules().stream()
                    .map(this::mapModuleToDTO)
                    .collect(Collectors.toList());
            dto.setModules(moduleDTOs);
        }

        if (course.getTags() != null) {
            Set<String> tagNames = course.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
            dto.setTags(tagNames);
        }

        return dto;
    }

    private ModuleDTO mapModuleToDTO(Module module) {
        return ModuleDTO.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .courseId(module.getCourse().getId())
                .build();
    }
}
