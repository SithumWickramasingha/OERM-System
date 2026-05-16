package com.oerms.online_exam_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SubjectRequest DTO — request body for creating and updating a Subject.
 *
 * Validations:
 *  name → required, must not be blank
 *  code → required, must not be blank
 *  description → optional
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {

    @NotBlank(message = "Subject name is required")
    private String name;

    @NotBlank(message = "Subject code is required")
    private String code;

    private String description;
}
