package com.oerms.online_exam_system.controller;

import com.oerms.online_exam_system.dto.StudentResponse;
import com.oerms.online_exam_system.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * StudentController — teacher-facing APIs for fetching student information.
 *
 * Base URL: /api/students
 *
 * Authorization: TEACHER only (both endpoints)
 *
 * These endpoints are used by teachers to find student IDs
 * before publishing results via POST /api/results.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/students
     * Returns all registered students (role = STUDENT).
     * TEACHER only.
     */
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    /**
     * GET /api/students/{id}
     * Returns a single student by ID.
     * Returns 404 if the user doesn't exist or is not a STUDENT.
     * TEACHER only.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
}
