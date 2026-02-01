package com.learningplatform.controller;

import com.learningplatform.dto.AnswerOptionDTO;
import com.learningplatform.dto.QuestionDTO;
import com.learningplatform.dto.QuizDTO;
import com.learningplatform.dto.QuizSubmissionDTO;
import com.learningplatform.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Quiz management API")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @Operation(summary = "Create a new quiz")
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(quizDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        QuizDTO quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/{id}/questions")
    @Operation(summary = "Get quiz with questions and options")
    public ResponseEntity<QuizDTO> getQuizWithQuestions(@PathVariable Long id) {
        QuizDTO quiz = quizService.getQuizWithQuestions(id);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get quiz by module")
    public ResponseEntity<QuizDTO> getQuizByModule(@PathVariable Long moduleId) {
        QuizDTO quiz = quizService.getQuizByModule(moduleId);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all quizzes for a course")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO updatedQuiz = quizService.updateQuiz(id, quizDTO);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    // Question endpoints
    @PostMapping("/{id}/questions")
    @Operation(summary = "Add a question to a quiz")
    public ResponseEntity<QuestionDTO> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionDTO questionDTO) {
        questionDTO.setQuizId(id);
        QuestionDTO createdQuestion = quizService.addQuestion(questionDTO);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @PostMapping("/questions/{questionId}/options")
    @Operation(summary = "Add an answer option to a question")
    public ResponseEntity<AnswerOptionDTO> addAnswerOption(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerOptionDTO optionDTO) {
        optionDTO.setQuestionId(questionId);
        AnswerOptionDTO createdOption = quizService.addAnswerOption(optionDTO);
        return new ResponseEntity<>(createdOption, HttpStatus.CREATED);
    }

    // Quiz submission endpoints
    @PostMapping("/{id}/take")
    @Operation(summary = "Take a quiz")
    public ResponseEntity<QuizSubmissionDTO> takeQuiz(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestBody Map<Long, Long> answers) {
        QuizSubmissionDTO submission = quizService.takeQuiz(id, studentId, answers);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/submissions")
    @Operation(summary = "Get all submissions for a quiz")
    public ResponseEntity<List<QuizSubmissionDTO>> getQuizSubmissions(@PathVariable Long id) {
        List<QuizSubmissionDTO> submissions = quizService.getQuizSubmissionsByQuiz(id);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/submissions/student/{studentId}")
    @Operation(summary = "Get all quiz submissions by a student")
    public ResponseEntity<List<QuizSubmissionDTO>> getStudentQuizSubmissions(@PathVariable Long studentId) {
        List<QuizSubmissionDTO> submissions = quizService.getQuizSubmissionsByStudent(studentId);
        return ResponseEntity.ok(submissions);
    }
}
