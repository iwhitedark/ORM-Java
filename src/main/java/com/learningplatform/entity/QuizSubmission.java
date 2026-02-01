package com.learningplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One: many submissions for one quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Many-to-One: many submissions by one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Score percentage (0-100)
    @Column(nullable = false)
    private Integer score;

    // Number of correct answers
    @Column(name = "correct_answers")
    private Integer correctAnswers;

    // Total number of questions
    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "passed")
    @Builder.Default
    private Boolean passed = false;

    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;

    // Time spent in seconds
    @Column(name = "time_spent")
    private Integer timeSpent;

    @PrePersist
    protected void onCreate() {
        if (takenAt == null) {
            takenAt = LocalDateTime.now();
        }
    }
}
