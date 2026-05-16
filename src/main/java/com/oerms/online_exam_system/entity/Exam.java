package com.oerms.online_exam_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Exam entity — mapped to the "exams" table.
 *
 * Relationships:
 *  - @ManyToOne → Subject  (One Subject has many Exams; one Exam belongs to one Subject)
 *  - @ManyToOne → User     (The TEACHER who created this exam)
 *  - @OneToMany → Result   (One Exam can have many Results — defined on Result side)
 *
 * createdAt is populated automatically by Hibernate on insert.
 */
@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Scheduled date/time of the exam */
    private LocalDateTime examDate;

    /** How long the exam runs, in minutes */
    @Column(nullable = false)
    private Integer durationMinutes;

    /** Maximum marks for this exam */
    @Column(nullable = false)
    private Integer totalMarks;

    /** Auto-set to the timestamp when the record is first persisted */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Subject this exam belongs to.
     * Many Exams → One Subject.
     * nullable = true so existing exams without a subject remain valid during migration.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    /**
     * The teacher (User with role TEACHER) who created this exam.
     * LAZY loading — only fetched from DB when explicitly accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}
