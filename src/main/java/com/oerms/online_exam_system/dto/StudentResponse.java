package com.oerms.online_exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StudentResponse DTO — returned when a teacher fetches student lists.
 *
 * Intentionally excludes the password field.
 * Only users with role STUDENT are returned by StudentService.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
}
