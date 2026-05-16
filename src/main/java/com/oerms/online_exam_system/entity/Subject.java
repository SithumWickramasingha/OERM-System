package com.oerms.online_exam_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Subject entity — mapped to the "subjects" table.
 *
 * Relationships:
 *  - @OneToMany → Exam (One Subject has many Exams)
 *    Defined on the Exam side with @ManyToOne to keep this entity lean.
 *    We do NOT include a List<Exam> here to avoid circular serialization
 *    and unnecessary eager loading.
 */
@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Subject display name e.g. "Java Programming" — must be unique */
    @Column(nullable = false, unique = true)
    private String name;

    /** Short code e.g. "CS201" — must be unique */
    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Auto-populated on insert */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
