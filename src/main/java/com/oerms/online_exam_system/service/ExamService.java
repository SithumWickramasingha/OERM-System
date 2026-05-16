package com.oerms.online_exam_system.service;

import com.oerms.online_exam_system.dto.ExamRequest;
import com.oerms.online_exam_system.dto.ExamResponse;
import com.oerms.online_exam_system.entity.Exam;
import com.oerms.online_exam_system.entity.Subject;
import com.oerms.online_exam_system.entity.User;
import com.oerms.online_exam_system.exception.ResourceNotFoundException;
import com.oerms.online_exam_system.repository.ExamRepository;
import com.oerms.online_exam_system.repository.SubjectRepository;
import com.oerms.online_exam_system.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ExamService — business logic for exam management.
 *
 * Updated in Step 4 to:
 *  - Accept subjectId in ExamRequest instead of a plain string
 *  - Resolve the Subject entity from SubjectRepository
 *  - Flatten subject details into ExamResponse
 */
@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public ExamService(ExamRepository examRepository,
                       UserRepository userRepository,
                       SubjectRepository subjectRepository) {
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public ExamResponse createExam(ExamRequest request) {
        User teacher   = getAuthenticatedUser();
        Subject subject = findSubjectById(request.getSubjectId());

        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .examDate(request.getExamDate())
                .durationMinutes(request.getDurationMinutes())
                .totalMarks(request.getTotalMarks())
                .createdBy(teacher)
                .build();

        return mapToResponse(examRepository.save(exam));
    }

    // ─── READ ────────────────────────────────────────────────────────────────

    public List<ExamResponse> getAllExams() {
        return examRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam not found with id: " + id));
        return mapToResponse(exam);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam      = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam not found with id: " + id));
        Subject subject = findSubjectById(request.getSubjectId());

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setSubject(subject);
        exam.setExamDate(request.getExamDate());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setTotalMarks(request.getTotalMarks());

        return mapToResponse(examRepository.save(exam));
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + email));
    }

    private Subject findSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with id: " + subjectId));
    }

    private ExamResponse mapToResponse(Exam exam) {
        Subject subject = exam.getSubject();
        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .examDate(exam.getExamDate())
                .durationMinutes(exam.getDurationMinutes())
                .totalMarks(exam.getTotalMarks())
                .createdAt(exam.getCreatedAt())
                // Subject (nullable during migration)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectName(subject != null ? subject.getName() : null)
                .subjectCode(subject != null ? subject.getCode() : null)
                // Teacher
                .createdById(exam.getCreatedBy().getId())
                .createdByName(exam.getCreatedBy().getFullName())
                .createdByEmail(exam.getCreatedBy().getEmail())
                .build();
    }
}
