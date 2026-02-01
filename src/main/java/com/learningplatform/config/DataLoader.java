package com.learningplatform.config;

import com.learningplatform.entity.*;
import com.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping data loading.");
            return;
        }

        log.info("Loading initial data...");

        // Create categories
        Category programming = createCategory("Programming", "Software development and programming courses");
        Category databases = createCategory("Databases", "Database design and management");
        Category webDev = createCategory("Web Development", "Frontend and backend web development");
        Category devOps = createCategory("DevOps", "Development operations and deployment");

        // Create tags
        Tag javaTag = createTag("Java");
        Tag springTag = createTag("Spring");
        Tag hibernateTag = createTag("Hibernate");
        Tag sqlTag = createTag("SQL");
        Tag postgresTag = createTag("PostgreSQL");
        Tag beginnerTag = createTag("Beginner");
        Tag advancedTag = createTag("Advanced");
        Tag backendTag = createTag("Backend");

        // Create users
        User admin = createUser("Admin User", "admin@learningplatform.com", Role.ADMIN);
        User teacher1 = createUser("John Smith", "john.smith@learningplatform.com", Role.TEACHER);
        User teacher2 = createUser("Jane Doe", "jane.doe@learningplatform.com", Role.TEACHER);
        User student1 = createUser("Alice Johnson", "alice@student.com", Role.STUDENT);
        User student2 = createUser("Bob Williams", "bob@student.com", Role.STUDENT);
        User student3 = createUser("Charlie Brown", "charlie@student.com", Role.STUDENT);

        // Create profiles
        createProfile(teacher1, "Experienced Java developer with 10+ years of experience", "https://example.com/john.jpg");
        createProfile(teacher2, "Database expert and SQL specialist", "https://example.com/jane.jpg");
        createProfile(student1, "Aspiring software developer", null);

        // Create courses
        Course hibernateCourse = createCourse(
                "Hibernate Fundamentals",
                "Learn the basics of Hibernate ORM framework for Java",
                40,
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                true,
                teacher1,
                databases,
                new HashSet<>(Arrays.asList(javaTag, hibernateTag, sqlTag, beginnerTag))
        );

        Course springCourse = createCourse(
                "Spring Boot Masterclass",
                "Complete guide to building enterprise applications with Spring Boot",
                60,
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                true,
                teacher1,
                programming,
                new HashSet<>(Arrays.asList(javaTag, springTag, backendTag, advancedTag))
        );

        Course sqlCourse = createCourse(
                "PostgreSQL for Developers",
                "Master PostgreSQL database from basics to advanced topics",
                30,
                LocalDate.now().plusWeeks(1),
                LocalDate.now().plusMonths(2),
                true,
                teacher2,
                databases,
                new HashSet<>(Arrays.asList(sqlTag, postgresTag, beginnerTag))
        );

        // Create modules for Hibernate course
        Module hibernateModule1 = createModule(hibernateCourse, "Introduction to ORM", "Understanding Object-Relational Mapping", 0);
        Module hibernateModule2 = createModule(hibernateCourse, "Entity Mapping", "How to map Java classes to database tables", 1);
        Module hibernateModule3 = createModule(hibernateCourse, "Relationships", "One-to-One, One-to-Many, Many-to-Many mappings", 2);

        // Create lessons for first module
        Lesson lesson1 = createLesson(hibernateModule1, "What is ORM?", "Object-Relational Mapping is a technique...", "https://video.example.com/orm-intro", 0, 30);
        Lesson lesson2 = createLesson(hibernateModule1, "Setting up Hibernate", "Step by step guide to configure Hibernate...", "https://video.example.com/hibernate-setup", 1, 45);
        Lesson lesson3 = createLesson(hibernateModule1, "First Entity", "Creating your first JPA entity...", "https://video.example.com/first-entity", 2, 40);

        // Create lessons for second module
        Lesson lesson4 = createLesson(hibernateModule2, "Basic Annotations", "Using @Entity, @Table, @Column...", null, 0, 35);
        Lesson lesson5 = createLesson(hibernateModule2, "Primary Keys", "Configuring ID generation strategies...", null, 1, 30);

        // Create lessons for third module
        Lesson lesson6 = createLesson(hibernateModule3, "One-to-One Mapping", "How to configure 1-1 relationships...", null, 0, 45);
        Lesson lesson7 = createLesson(hibernateModule3, "One-to-Many Mapping", "How to configure 1-N relationships...", null, 1, 50);
        Lesson lesson8 = createLesson(hibernateModule3, "Many-to-Many Mapping", "How to configure M-N relationships...", null, 2, 55);

        // Create assignments
        Assignment assignment1 = createAssignment(lesson3, "Create a User Entity", "Create a JPA entity class for User with appropriate annotations", LocalDateTime.now().plusWeeks(1), 100);
        Assignment assignment2 = createAssignment(lesson5, "ID Generation Strategies", "Implement different ID generation strategies and compare them", LocalDateTime.now().plusWeeks(2), 100);
        Assignment assignment3 = createAssignment(lesson8, "Design a Blog Schema", "Create entities for a blog with posts, comments, and tags", LocalDateTime.now().plusWeeks(3), 150);

        // Create quiz for first module
        Quiz quiz1 = createQuiz(hibernateModule1, "ORM Basics Quiz", "Test your understanding of ORM concepts", 15, 70);

        // Add questions to quiz
        Question q1 = createQuestion(quiz1, "What does ORM stand for?", QuestionType.SINGLE_CHOICE, 1);
        createAnswerOption(q1, "Object-Relational Mapping", true);
        createAnswerOption(q1, "Object-Resource Management", false);
        createAnswerOption(q1, "Object-Reference Model", false);
        createAnswerOption(q1, "Operational Resource Mapping", false);

        Question q2 = createQuestion(quiz1, "Which annotation marks a class as a JPA entity?", QuestionType.SINGLE_CHOICE, 1);
        createAnswerOption(q2, "@Table", false);
        createAnswerOption(q2, "@Entity", true);
        createAnswerOption(q2, "@Model", false);
        createAnswerOption(q2, "@Persistent", false);

        Question q3 = createQuestion(quiz1, "What is lazy loading?", QuestionType.SINGLE_CHOICE, 1);
        createAnswerOption(q3, "Loading data only when it is accessed", true);
        createAnswerOption(q3, "Loading all data at startup", false);
        createAnswerOption(q3, "A slow database query", false);
        createAnswerOption(q3, "Loading data in background thread", false);

        // Enroll students
        createEnrollment(student1, hibernateCourse);
        createEnrollment(student1, springCourse);
        createEnrollment(student2, hibernateCourse);
        createEnrollment(student2, sqlCourse);
        createEnrollment(student3, sqlCourse);

        log.info("Initial data loaded successfully!");
        log.info("Created {} users, {} courses, {} modules, {} lessons, {} assignments, {} quizzes",
                userRepository.count(), courseRepository.count(), moduleRepository.count(),
                lessonRepository.count(), assignmentRepository.count(), quizRepository.count());
    }

    private Category createCategory(String name, String description) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();
        return categoryRepository.save(category);
    }

    private Tag createTag(String name) {
        Tag tag = Tag.builder().name(name).build();
        return tagRepository.save(tag);
    }

    private User createUser(String name, String email, Role role) {
        User user = User.builder()
                .name(name)
                .email(email)
                .role(role)
                .build();
        return userRepository.save(user);
    }

    private Profile createProfile(User user, String bio, String avatarUrl) {
        Profile profile = Profile.builder()
                .user(user)
                .bio(bio)
                .avatarUrl(avatarUrl)
                .build();
        return profileRepository.save(profile);
    }

    private Course createCourse(String title, String description, int duration, LocalDate startDate,
                                 LocalDate endDate, boolean isPublished, User teacher,
                                 Category category, java.util.Set<Tag> tags) {
        Course course = Course.builder()
                .title(title)
                .description(description)
                .duration(duration)
                .startDate(startDate)
                .endDate(endDate)
                .isPublished(isPublished)
                .teacher(teacher)
                .category(category)
                .tags(tags)
                .build();
        return courseRepository.save(course);
    }

    private Module createModule(Course course, String title, String description, int orderIndex) {
        Module module = Module.builder()
                .course(course)
                .title(title)
                .description(description)
                .orderIndex(orderIndex)
                .build();
        return moduleRepository.save(module);
    }

    private Lesson createLesson(Module module, String title, String content, String videoUrl, int orderIndex, int duration) {
        Lesson lesson = Lesson.builder()
                .module(module)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .orderIndex(orderIndex)
                .duration(duration)
                .build();
        return lessonRepository.save(lesson);
    }

    private Assignment createAssignment(Lesson lesson, String title, String description, LocalDateTime dueDate, int maxScore) {
        Assignment assignment = Assignment.builder()
                .lesson(lesson)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .maxScore(maxScore)
                .build();
        return assignmentRepository.save(assignment);
    }

    private Quiz createQuiz(Module module, String title, String description, int timeLimit, int passingScore) {
        Quiz quiz = Quiz.builder()
                .module(module)
                .title(title)
                .description(description)
                .timeLimit(timeLimit)
                .passingScore(passingScore)
                .build();
        return quizRepository.save(quiz);
    }

    private Question createQuestion(Quiz quiz, String text, QuestionType type, int points) {
        Question question = Question.builder()
                .quiz(quiz)
                .text(text)
                .type(type)
                .points(points)
                .build();
        return questionRepository.save(question);
    }

    private AnswerOption createAnswerOption(Question question, String text, boolean isCorrect) {
        AnswerOption option = AnswerOption.builder()
                .question(question)
                .text(text)
                .isCorrect(isCorrect)
                .build();
        return answerOptionRepository.save(option);
    }

    private Enrollment createEnrollment(User student, Course course) {
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .progress(0)
                .build();
        return enrollmentRepository.save(enrollment);
    }
}
