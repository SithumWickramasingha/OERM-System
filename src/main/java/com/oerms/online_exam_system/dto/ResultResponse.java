package com.oerms.online_exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ResultResponse DTO — returned to the client for all result-related responses.
 *
 * All three relationships (student, exam, teacher) are flattened into
 * scalar fields to:
 *  - Avoid nested entity serialization
 *  - Prevent LazyInitializationException
 *  - Keep the response payload clean and frontend-friendly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultResponse {

    private Long id;
    private Integer marks;
    private String grade;
    private String status;       // "PASS" or "FAIL"
    private String feedback;
    private LocalDateTime publishedAt;

    // ─── Flattened Student info ───────────────────────────────────────────────
    private Long studentId;
    private String studentName;
    private String studentEmail;

    // ─── Flattened Exam info ──────────────────────────────────────────────────
    private Long examId;
    private String examTitle;
    private String examSubject;  // subject name from the nested Subject entity
    private Integer examTotalMarks;

    // ─── Flattened Teacher info ───────────────────────────────────────────────
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
}
