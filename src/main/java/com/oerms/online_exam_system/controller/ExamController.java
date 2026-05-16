package com.oerms.online_exam_system.controller;

import com.oerms.online_exam_system.dto.ExamRequest;
import com.oerms.online_exam_system.dto.ExamResponse;
import com.oerms.online_exam_system.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ExamController — REST API controller for exam management.
 *
 * Base URL: /api/exams
 *
 * Authorization strategy (uses @PreAuthorize with method-level security,
 * enabled via @EnableMethodSecurity in SecurityConfig):
 *
 *   POST   /api/exams       → TEACHER only   (@PreAuthorize("hasRole('TEACHER')"))
 *   GET    /api/exams       → TEACHER + STUDENT (any authenticated user)
 *   GET    /api/exams/{id}  → TEACHER + STUDENT (any authenticated user)
 *   PUT    /api/exams/{id}  → TEACHER only
 *   DELETE /api/exams/{id}  → TEACHER only
 *
 * The /api/exams/** path falls under anyRequest().authenticated() in SecurityConfig,
 * so any request without a valid JWT is rejected before reaching this controller.
 * Fine-grained role enforcement is done here via @PreAuthorize.
 *
 * @Valid triggers Bean Validation on ExamRequest fields (title, subject, etc.).
 */
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    // ─── POST /api/exams ─────────────────────────────────────────────────────

    /**
     * Create a new exam.
     * Requires TEACHER role. Teacher identity is read from JWT inside ExamService.
     * Returns 201 Created with the saved exam details.
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
        ExamResponse response = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── GET /api/exams ──────────────────────────────────────────────────────

    /**
     * Get all available exams.
     * Accessible by both TEACHER and STUDENT (any authenticated user).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<ExamResponse>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    // ─── GET /api/exams/{id} ─────────────────────────────────────────────────

    /**
     * Get a single exam by its ID.
     * Accessible by both TEACHER and STUDENT.
     * Returns 404 if not found (handled by GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    // ─── PUT /api/exams/{id} ─────────────────────────────────────────────────

    /**
     * Update an existing exam.
     * Requires TEACHER role.
     * Returns 200 OK with the updated exam details.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id,
                                                   @Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(examService.updateExam(id, request));
    }

    // ─── DELETE /api/exams/{id} ──────────────────────────────────────────────

    /**
     * Delete an exam by its ID.
     * Requires TEACHER role.
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
