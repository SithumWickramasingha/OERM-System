package com.oerms.online_exam_system.service;

import com.oerms.online_exam_system.dto.ResultRequest;
import com.oerms.online_exam_system.dto.ResultResponse;
import com.oerms.online_exam_system.entity.*;
import com.oerms.online_exam_system.exception.DuplicateResourceException;
import com.oerms.online_exam_system.exception.ResourceNotFoundException;
import com.oerms.online_exam_system.repository.ExamRepository;
import com.oerms.online_exam_system.repository.ResultRepository;
import com.oerms.online_exam_system.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ResultService — business logic for result management.
 *
 * Key responsibilities:
 *  1. GRADE CALCULATION  : Derives letter grade from raw marks automatically.
 *  2. STATUS CALCULATION : PASS (≥50) or FAIL (<50) derived from marks.
 *  3. TEACHER IDENTITY   : Resolved from JWT SecurityContext — never sent by client.
 *  4. DUPLICATE GUARD    : Prevents publishing a result twice for the same student + exam.
 *  5. STUDENT SCOPING    : getMyResults() scopes to the currently logged-in student only.
 *
 * Grade scale:
 *   marks ≥ 90  → A
 *   marks ≥ 75  → B
 *   marks ≥ 65  → C
 *   marks ≥ 50  → D
 *   marks <  50 → F
 */
@Service
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    public ResultService(ResultRepository resultRepository,
                         UserRepository userRepository,
                         ExamRepository examRepository) {
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    // ─── PUBLISH RESULT ──────────────────────────────────────────────────────

    /**
     * Publishes a result for a student's exam.
     * Teacher identity comes from the JWT — the client does NOT send teacherId.
     */
    public ResultResponse publishResult(ResultRequest request) {
        // Prevent duplicate result for same student + exam
        if (resultRepository.existsByStudentIdAndExamId(
                request.getStudentId(), request.getExamId())) {
            throw new DuplicateResourceException(
                    "Result already published for student ID " + request.getStudentId()
                    + " and exam ID " + request.getExamId());
        }

        User student = findUserById(request.getStudentId(), "Student");
        Exam exam    = findExamById(request.getExamId());
        User teacher = getAuthenticatedUser();

        int marks = request.getMarks();

        Result result = Result.builder()
                .marks(marks)
                .grade(calculateGrade(marks))
                .status(marks >= 50 ? ResultStatus.PASS : ResultStatus.FAIL)
                .feedback(request.getFeedback())
                .publishedAt(LocalDateTime.now())
                .student(student)
                .exam(exam)
                .teacher(teacher)
                .build();

        return mapToResponse(resultRepository.save(result));
    }

    // ─── GET RESULTS ─────────────────────────────────────────────────────────

    /** Returns all results for a given student (used by TEACHER or the STUDENT themselves). */
    public List<ResultResponse> getResultsByStudentId(Long studentId) {
        // Verify student exists
        findUserById(studentId, "Student");
        return resultRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Returns all results for a given exam (TEACHER only). */
    public List<ResultResponse> getResultsByExamId(Long examId) {
        findExamById(examId);
        return resultRepository.findByExamId(examId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns the results for the currently authenticated student.
     * Student identity is resolved from the JWT — no studentId in the URL.
     */
    public List<ResultResponse> getMyResults() {
        User student = getAuthenticatedUser();
        return resultRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── UPDATE RESULT ────────────────────────────────────────────────────────

    /**
     * Updates an existing result (e.g., corrected marks after re-checking).
     * Grade and status are recalculated automatically from the new marks.
     */
    public ResultResponse updateResult(Long id, ResultRequest request) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Result not found with id: " + id));

        int marks = request.getMarks();
        result.setMarks(marks);
        result.setGrade(calculateGrade(marks));
        result.setStatus(marks >= 50 ? ResultStatus.PASS : ResultStatus.FAIL);
        result.setFeedback(request.getFeedback());
        // publishedAt and teacher remain unchanged

        return mapToResponse(resultRepository.save(result));
    }

    // ─── DELETE RESULT ────────────────────────────────────────────────────────

    public void deleteResult(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Result not found with id: " + id);
        }
        resultRepository.deleteById(id);
    }

    // ─── Grade Calculation ────────────────────────────────────────────────────

    /**
     * Calculates letter grade from raw marks.
     *
     * Scale:
     *   90–100 → A
     *   75–89  → B
     *   65–74  → C
     *   50–64  → D
     *   0–49   → F
     */
    private String calculateGrade(int marks) {
        if (marks >= 90) return "A";
        if (marks >= 75) return "B";
        if (marks >= 65) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Resolves the authenticated user's email from the JWT SecurityContext. */
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + email));
    }

    private User findUserById(Long id, String label) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        label + " not found with id: " + id));
    }

    private Exam findExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam not found with id: " + id));
    }

    /**
     * Maps a Result entity to a fully flattened ResultResponse DTO.
     * All three relationships are flattened to scalar fields.
     */
    private ResultResponse mapToResponse(Result result) {
        Exam exam    = result.getExam();
        User student = result.getStudent();
        User teacher = result.getTeacher();

        // Get subject name safely (subject is optional on Exam)
        String subjectName = (exam.getSubject() != null)
                ? exam.getSubject().getName()
                : "N/A";

        return ResultResponse.builder()
                .id(result.getId())
                .marks(result.getMarks())
                .grade(result.getGrade())
                .status(result.getStatus().name())
                .feedback(result.getFeedback())
                .publishedAt(result.getPublishedAt())
                // Student
                .studentId(student.getId())
                .studentName(student.getFullName())
                .studentEmail(student.getEmail())
                // Exam
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examSubject(subjectName)
                .examTotalMarks(exam.getTotalMarks())
                // Teacher
                .teacherId(teacher.getId())
                .teacherName(teacher.getFullName())
                .teacherEmail(teacher.getEmail())
                .build();
    }
}
