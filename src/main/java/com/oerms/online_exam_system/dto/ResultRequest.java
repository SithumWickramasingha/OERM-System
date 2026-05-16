package com.oerms.online_exam_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResultRequest DTO — request body for publishing or updating a result.
 *
 * Validations:
 *  studentId → required
 *  examId    → required
 *  marks     → required, must be between 0 and 100
 *  feedback  → optional
 *
 * Grade and status (PASS/FAIL) are NOT accepted from the client —
 * they are calculated automatically in ResultService.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Marks are required")
    @Min(value = 0, message = "Marks cannot be less than 0")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Integer marks;

    /** Optional teacher remarks */
    private String feedback;
}
