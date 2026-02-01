package com.learningplatform.controller;

import com.learningplatform.dto.LessonDTO;
import com.learningplatform.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Lesson management API")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create a new lesson")
    public ResponseEntity<LessonDTO> createLesson(@Valid @RequestBody LessonDTO lessonDTO) {
        LessonDTO createdLesson = lessonService.createLesson(lessonDTO);
        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lesson by ID")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long id) {
        LessonDTO lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/{id}/assignments")
    @Operation(summary = "Get lesson with assignments")
    public ResponseEntity<LessonDTO> getLessonWithAssignments(@PathVariable Long id) {
        LessonDTO lesson = lessonService.getLessonWithAssignments(id);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get all lessons for a module")
    public ResponseEntity<List<LessonDTO>> getLessonsByModule(@PathVariable Long moduleId) {
        List<LessonDTO> lessons = lessonService.getLessonsByModule(moduleId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all lessons for a course")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable Long courseId) {
        List<LessonDTO> lessons = lessonService.getLessonsByCourse(courseId);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lesson")
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonDTO lessonDTO) {
        LessonDTO updatedLesson = lessonService.updateLesson(id, lessonDTO);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lesson")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
