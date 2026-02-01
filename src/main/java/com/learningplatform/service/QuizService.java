package com.learningplatform.service;

import com.learningplatform.dto.*;
import com.learningplatform.entity.*;
import com.learningplatform.exception.BusinessLogicException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    public QuizDTO createQuiz(QuizDTO quizDTO) {
        log.info("Creating quiz: {} for module {}", quizDTO.getTitle(), quizDTO.getModuleId());

        Module module = moduleRepository.findById(quizDTO.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", quizDTO.getModuleId()));

        // Check if module already has a quiz
        if (quizRepository.findByModuleId(quizDTO.getModuleId()).isPresent()) {
            throw new BusinessLogicException("Module already has a quiz");
        }

        Quiz quiz = Quiz.builder()
                .title(quizDTO.getTitle())
                .description(quizDTO.getDescription())
                .timeLimit(quizDTO.getTimeLimit())
                .passingScore(quizDTO.getPassingScore() != null ? quizDTO.getPassingScore() : 70)
                .module(module)
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Quiz created with ID: {}", savedQuiz.getId());

        return mapToDTO(savedQuiz);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        return mapToDTO(quiz);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizWithQuestions(Long id) {
        Quiz quiz = quizRepository.findByIdWithQuestionsAndOptions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        return mapToDTOWithQuestions(quiz);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizByModule(Long moduleId) {
        Quiz quiz = quizRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found for module " + moduleId));
        return mapToDTO(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public QuizDTO updateQuiz(Long id, QuizDTO quizDTO) {
        log.info("Updating quiz with ID: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setTimeLimit(quizDTO.getTimeLimit());

        if (quizDTO.getPassingScore() != null) {
            quiz.setPassingScore(quizDTO.getPassingScore());
        }

        Quiz updatedQuiz = quizRepository.save(quiz);
        return mapToDTO(updatedQuiz);
    }

    public void deleteQuiz(Long id) {
        log.info("Deleting quiz with ID: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        quizRepository.delete(quiz);
        log.info("Quiz deleted with ID: {}", id);
    }

    // Question operations
    public QuestionDTO addQuestion(QuestionDTO questionDTO) {
        log.info("Adding question to quiz {}", questionDTO.getQuizId());

        Quiz quiz = quizRepository.findById(questionDTO.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", questionDTO.getQuizId()));

        Question question = Question.builder()
                .text(questionDTO.getText())
                .type(questionDTO.getType() != null ? questionDTO.getType() : QuestionType.SINGLE_CHOICE)
                .points(questionDTO.getPoints() != null ? questionDTO.getPoints() : 1)
                .quiz(quiz)
                .build();

        Question savedQuestion = questionRepository.save(question);

        // Add options if provided
        if (questionDTO.getOptions() != null) {
            for (AnswerOptionDTO optionDTO : questionDTO.getOptions()) {
                AnswerOption option = AnswerOption.builder()
                        .text(optionDTO.getText())
                        .isCorrect(optionDTO.getIsCorrect() != null ? optionDTO.getIsCorrect() : false)
                        .question(savedQuestion)
                        .build();
                answerOptionRepository.save(option);
            }
        }

        log.info("Question created with ID: {}", savedQuestion.getId());
        return mapQuestionToDTO(savedQuestion);
    }

    public AnswerOptionDTO addAnswerOption(AnswerOptionDTO optionDTO) {
        log.info("Adding answer option to question {}", optionDTO.getQuestionId());

        Question question = questionRepository.findById(optionDTO.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", optionDTO.getQuestionId()));

        AnswerOption option = AnswerOption.builder()
                .text(optionDTO.getText())
                .isCorrect(optionDTO.getIsCorrect() != null ? optionDTO.getIsCorrect() : false)
                .question(question)
                .build();

        AnswerOption savedOption = answerOptionRepository.save(option);
        log.info("Answer option created with ID: {}", savedOption.getId());

        return mapOptionToDTO(savedOption);
    }

    // Quiz submission
    public QuizSubmissionDTO takeQuiz(Long quizId, Long studentId, Map<Long, Long> answers) {
        log.info("Student {} taking quiz {}", studentId, quizId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new BusinessLogicException("Only students can take quizzes");
        }

        Quiz quiz = quizRepository.findByIdWithQuestionsAndOptions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        // Calculate score
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;

        for (Question question : quiz.getQuestions()) {
            Long selectedOptionId = answers.get(question.getId());
            if (selectedOptionId != null) {
                AnswerOption selectedOption = answerOptionRepository.findById(selectedOptionId).orElse(null);
                if (selectedOption != null && selectedOption.getIsCorrect()) {
                    correctAnswers++;
                }
            }
        }

        int scorePercentage = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;
        boolean passed = scorePercentage >= quiz.getPassingScore();

        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .student(student)
                .score(scorePercentage)
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .passed(passed)
                .takenAt(LocalDateTime.now())
                .build();

        QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);
        log.info("Quiz submission created with ID: {}, score: {}%, passed: {}",
                savedSubmission.getId(), scorePercentage, passed);

        return mapSubmissionToDTO(savedSubmission);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionDTO> getQuizSubmissionsByStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentIdWithQuizDetails(studentId).stream()
                .map(this::mapSubmissionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionDTO> getQuizSubmissionsByQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId).stream()
                .map(this::mapSubmissionToDTO)
                .collect(Collectors.toList());
    }

    private QuizDTO mapToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .passingScore(quiz.getPassingScore())
                .moduleId(quiz.getModule().getId())
                .questionCount((int) questionRepository.countByQuizId(quiz.getId()))
                .build();
    }

    private QuizDTO mapToDTOWithQuestions(Quiz quiz) {
        QuizDTO dto = mapToDTO(quiz);

        if (quiz.getQuestions() != null) {
            List<QuestionDTO> questionDTOs = quiz.getQuestions().stream()
                    .map(this::mapQuestionWithOptionsToDTO)
                    .collect(Collectors.toList());
            dto.setQuestions(questionDTOs);
        }

        return dto;
    }

    private QuestionDTO mapQuestionToDTO(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .text(question.getText())
                .type(question.getType())
                .points(question.getPoints())
                .quizId(question.getQuiz().getId())
                .build();
    }

    private QuestionDTO mapQuestionWithOptionsToDTO(Question question) {
        QuestionDTO dto = mapQuestionToDTO(question);

        if (question.getOptions() != null) {
            List<AnswerOptionDTO> optionDTOs = question.getOptions().stream()
                    .map(this::mapOptionToDTO)
                    .collect(Collectors.toList());
            dto.setOptions(optionDTOs);
        }

        return dto;
    }

    private AnswerOptionDTO mapOptionToDTO(AnswerOption option) {
        return AnswerOptionDTO.builder()
                .id(option.getId())
                .text(option.getText())
                .isCorrect(option.getIsCorrect())
                .questionId(option.getQuestion().getId())
                .build();
    }

    private QuizSubmissionDTO mapSubmissionToDTO(QuizSubmission submission) {
        return QuizSubmissionDTO.builder()
                .id(submission.getId())
                .quizId(submission.getQuiz().getId())
                .quizTitle(submission.getQuiz().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getName())
                .score(submission.getScore())
                .correctAnswers(submission.getCorrectAnswers())
                .totalQuestions(submission.getTotalQuestions())
                .passed(submission.getPassed())
                .takenAt(submission.getTakenAt())
                .timeSpent(submission.getTimeSpent())
                .build();
    }
}
