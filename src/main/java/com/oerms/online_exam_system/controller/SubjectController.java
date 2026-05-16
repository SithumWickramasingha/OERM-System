package com.oerms.online_exam_system.controller;

import com.oerms.online_exam_system.dto.SubjectRequest;
import com.oerms.online_exam_system.dto.SubjectResponse;
import com.oerms.online_exam_system.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SubjectController — REST API for subject management.
 *
 * Base URL: /api/subjects
 *
 * Authorization:
 *   POST / PUT / DELETE → TEACHER only
 *   GET                 → TEACHER + STUDENT (any authenticated user)
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /** POST /api/subjects — Create a new subject (TEACHER only) */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subjectService.createSubject(request));
    }

    /** GET /api/subjects — Get all subjects (TEACHER + STUDENT) */
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    /** GET /api/subjects/{id} — Get subject by ID (TEACHER + STUDENT) */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    /** PUT /api/subjects/{id} — Update subject (TEACHER only) */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(subjectService.updateSubject(id, request));
    }

    /** DELETE /api/subjects/{id} — Delete subject (TEACHER only) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
