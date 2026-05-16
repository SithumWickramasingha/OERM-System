package com.oerms.online_exam_system.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ExamRequest DTO — used as the request body for CREATE and UPDATE exam endpoints.
 *
 * subjectId links the exam to an existing Subject.
 * The plain "subject" string field from Step 3 has been replaced by a proper FK reference.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    /** FK reference to the Subject entity */
    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Exam date is required")
    @Future(message = "Exam date must be in the future")
    private LocalDateTime examDate;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be a positive number of minutes")
    private Integer durationMinutes;

    @NotNull(message = "Total marks is required")
    @Positive(message = "Total marks must be a positive number")
    private Integer totalMarks;
}
