package com.oerms.online_exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ExamResponse DTO — returned to the client for all exam-related responses.
 *
 * Subject relationship is flattened into subjectId/subjectName/subjectCode
 * to keep the response clean and avoid JPA lazy-loading issues.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime examDate;
    private Integer durationMinutes;
    private Integer totalMarks;
    private LocalDateTime createdAt;

    /** Flattened Subject info */
    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    /** Flattened Teacher info */
    private Long createdById;
    private String createdByName;
    private String createdByEmail;
}
