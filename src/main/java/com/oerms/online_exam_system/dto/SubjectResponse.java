package com.oerms.online_exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SubjectResponse DTO — returned to the client for all subject-related responses.
 * The Subject entity is never exposed directly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createdAt;
}
