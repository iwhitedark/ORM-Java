package com.learningplatform.controller;

import com.learningplatform.dto.ModuleDTO;
import com.learningplatform.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Module management API")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    @Operation(summary = "Create a new module")
    public ResponseEntity<ModuleDTO> createModule(@Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO createdModule = moduleService.createModule(moduleDTO);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get module by ID")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Long id) {
        ModuleDTO module = moduleService.getModuleById(id);
        return ResponseEntity.ok(module);
    }

    @GetMapping("/{id}/lessons")
    @Operation(summary = "Get module with lessons")
    public ResponseEntity<ModuleDTO> getModuleWithLessons(@PathVariable Long id) {
        ModuleDTO module = moduleService.getModuleWithLessons(id);
        return ResponseEntity.ok(module);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all modules for a course")
    public ResponseEntity<List<ModuleDTO>> getModulesByCourse(@PathVariable Long courseId) {
        List<ModuleDTO> modules = moduleService.getModulesByCourse(courseId);
        return ResponseEntity.ok(modules);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update module")
    public ResponseEntity<ModuleDTO> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO updatedModule = moduleService.updateModule(id, moduleDTO);
        return ResponseEntity.ok(updatedModule);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete module")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}
