package com.oerms.online_exam_system.controller;

import com.oerms.online_exam_system.dto.ResultRequest;
import com.oerms.online_exam_system.dto.ResultResponse;
import com.oerms.online_exam_system.service.ResultService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ResultController — REST API for result management.
 *
 * Base URL: /api/results
 *
 * Authorization:
 *   POST /api/results                          → TEACHER only (publish result)
 *   GET  /api/results/student/{studentId}      → TEACHER + STUDENT
 *   GET  /api/results/exam/{examId}            → TEACHER only
 *   GET  /api/results/my-results               → STUDENT only (own results from JWT)
 *   PUT  /api/results/{id}                     → TEACHER only
 *   DELETE /api/results/{id}                   → TEACHER only
 */
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    /**
     * POST /api/results
     * Publish a result for a student's exam.
     * Grade, status, publishedAt, and teacherId are set automatically.
     * TEACHER only.
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResultResponse> publishResult(
            @Valid @RequestBody ResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultService.publishResult(request));
    }

    /**
     * GET /api/results/student/{studentId}
     * Get all results for a specific student.
     * Accessible by TEACHER (view any student) and STUDENT (can view own results by ID).
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<ResultResponse>> getStudentResults(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(resultService.getResultsByStudentId(studentId));
    }

    /**
     * GET /api/results/exam/{examId}
     * Get all results for a specific exam (class-wide view).
     * TEACHER only.
     */
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ResultResponse>> getExamResults(
            @PathVariable Long examId) {
        return ResponseEntity.ok(resultService.getResultsByExamId(examId));
    }

    /**
     * GET /api/results/my-results
     * Logged-in student views their own results.
     * Student identity resolved from JWT — no ID in the URL.
     * STUDENT only.
     */
    @GetMapping("/my-results")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ResultResponse>> getMyResults() {
        return ResponseEntity.ok(resultService.getMyResults());
    }

    /**
     * PUT /api/results/{id}
     * Update a published result (corrected marks, updated feedback).
     * Grade and status are recalculated automatically.
     * TEACHER only.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResultResponse> updateResult(
            @PathVariable Long id,
            @Valid @RequestBody ResultRequest request) {
        return ResponseEntity.ok(resultService.updateResult(id, request));
    }

    /**
     * DELETE /api/results/{id}
     * Delete a result.
     * TEACHER only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }
}
