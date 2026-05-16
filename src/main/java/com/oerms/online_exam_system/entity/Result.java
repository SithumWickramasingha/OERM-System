package com.oerms.online_exam_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result entity — mapped to the "results" table.
 *
 * Relationships:
 *  - @ManyToOne → User (student)  : One Student has many Results
 *  - @ManyToOne → Exam            : One Exam has many Results
 *  - @ManyToOne → User (teacher)  : One Teacher publishes many Results
 *
 * Grade and status are calculated automatically in ResultService
 * based on the marks value — never set manually by the client.
 */
@Entity
@Table(
    name = "results",
    uniqueConstraints = {
        // One result per student per exam — prevents duplicate publishing
        @UniqueConstraint(
            name = "uq_student_exam",
            columnNames = {"student_id", "exam_id"}
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Marks obtained by the student — must be between 0 and 100 */
    @Column(nullable = false)
    private Integer marks;

    /**
     * Letter grade — auto-calculated from marks:
     * 90+ → A | 75+ → B | 65+ → C | 50+ → D | below 50 → F
     */
    @Column(nullable = false)
    private String grade;

    /** PASS (marks ≥ 50) or FAIL (marks < 50) — auto-calculated */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status;

    /** Optional teacher feedback/remarks */
    @Column(columnDefinition = "TEXT")
    private String feedback;

    /** Timestamp set when the result is first published */
    @Column(nullable = false)
    private LocalDateTime publishedAt;

    // ─── Relationships ────────────────────────────────────────────────────────

    /** The student who sat this exam */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** The exam this result belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    /** The teacher who published this result */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;
}
