package com.learningplatform.integration;

import com.learningplatform.dto.*;
import com.learningplatform.entity.QuestionType;
import com.learningplatform.entity.Role;
import com.learningplatform.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private EnrollmentService enrollmentService;

    private UserDTO teacher;
    private UserDTO student;
    private CourseDTO course;
    private ModuleDTO module;

    @BeforeEach
    void setUp() {
        // Create teacher
        UserDTO teacherDTO = UserDTO.builder()
                .name("Quiz Teacher")
                .email("quiz.teacher" + System.currentTimeMillis() + "@test.com")
                .role(Role.TEACHER)
                .build();
        teacher = userService.createUser(teacherDTO);

        // Create student
        UserDTO studentDTO = UserDTO.builder()
                .name("Quiz Student")
                .email("quiz.student" + System.currentTimeMillis() + "@test.com")
                .role(Role.STUDENT)
                .build();
        student = userService.createUser(studentDTO);

        // Create course
        CourseDTO courseDTO = CourseDTO.builder()
                .title("Quiz Test Course")
                .description("Course for quiz testing")
                .teacherId(teacher.getId())
                .isPublished(true)
                .build();
        course = courseService.createCourse(courseDTO);

        // Create module
        ModuleDTO moduleDTO = ModuleDTO.builder()
                .title("Quiz Module")
                .description("Module with quiz")
                .courseId(course.getId())
                .build();
        module = moduleService.createModule(moduleDTO);

        // Enroll student
        enrollmentService.enrollStudent(student.getId(), course.getId());
    }

    @Test
    void shouldCreateQuizWithQuestionsAndTakeIt() {
        // Create quiz
        QuizDTO quizDTO = QuizDTO.builder()
                .title("Test Quiz")
                .description("A test quiz")
                .timeLimit(10)
                .passingScore(60)
                .moduleId(module.getId())
                .build();
        QuizDTO quiz = quizService.createQuiz(quizDTO);

        assertThat(quiz.getId()).isNotNull();

        // Add question 1
        QuestionDTO question1DTO = QuestionDTO.builder()
                .text("What is 2 + 2?")
                .type(QuestionType.SINGLE_CHOICE)
                .points(1)
                .quizId(quiz.getId())
                .build();
        QuestionDTO question1 = quizService.addQuestion(question1DTO);

        // Add options for question 1
        AnswerOptionDTO option1a = AnswerOptionDTO.builder()
                .text("3")
                .isCorrect(false)
                .questionId(question1.getId())
                .build();
        quizService.addAnswerOption(option1a);

        AnswerOptionDTO option1b = AnswerOptionDTO.builder()
                .text("4")
                .isCorrect(true)
                .questionId(question1.getId())
                .build();
        AnswerOptionDTO correctOption = quizService.addAnswerOption(option1b);

        AnswerOptionDTO option1c = AnswerOptionDTO.builder()
                .text("5")
                .isCorrect(false)
                .questionId(question1.getId())
                .build();
        quizService.addAnswerOption(option1c);

        // Get quiz with questions
        QuizDTO quizWithQuestions = quizService.getQuizWithQuestions(quiz.getId());
        assertThat(quizWithQuestions.getQuestions()).hasSize(1);
        assertThat(quizWithQuestions.getQuestions().get(0).getOptions()).hasSize(3);

        // Take quiz
        Map<Long, Long> answers = new HashMap<>();
        answers.put(question1.getId(), correctOption.getId());

        QuizSubmissionDTO submission = quizService.takeQuiz(quiz.getId(), student.getId(), answers);

        assertThat(submission.getId()).isNotNull();
        assertThat(submission.getScore()).isEqualTo(100);
        assertThat(submission.getCorrectAnswers()).isEqualTo(1);
        assertThat(submission.getTotalQuestions()).isEqualTo(1);
        assertThat(submission.getPassed()).isTrue();

        // Get student's quiz submissions
        List<QuizSubmissionDTO> studentSubmissions = quizService.getQuizSubmissionsByStudent(student.getId());
        assertThat(studentSubmissions).hasSize(1);
    }

    @Test
    void shouldFailQuizWithWrongAnswers() {
        // Create quiz
        QuizDTO quizDTO = QuizDTO.builder()
                .title("Fail Test Quiz")
                .description("Quiz to test failing")
                .passingScore(70)
                .moduleId(module.getId())
                .build();
        QuizDTO quiz = quizService.createQuiz(quizDTO);

        // Add question
        QuestionDTO questionDTO = QuestionDTO.builder()
                .text("What is the capital of France?")
                .type(QuestionType.SINGLE_CHOICE)
                .quizId(quiz.getId())
                .build();
        QuestionDTO question = quizService.addQuestion(questionDTO);

        AnswerOptionDTO wrongOption = quizService.addAnswerOption(AnswerOptionDTO.builder()
                .text("London")
                .isCorrect(false)
                .questionId(question.getId())
                .build());

        quizService.addAnswerOption(AnswerOptionDTO.builder()
                .text("Paris")
                .isCorrect(true)
                .questionId(question.getId())
                .build());

        // Take quiz with wrong answer
        Map<Long, Long> answers = new HashMap<>();
        answers.put(question.getId(), wrongOption.getId());

        QuizSubmissionDTO submission = quizService.takeQuiz(quiz.getId(), student.getId(), answers);

        assertThat(submission.getScore()).isEqualTo(0);
        assertThat(submission.getPassed()).isFalse();
    }
}
