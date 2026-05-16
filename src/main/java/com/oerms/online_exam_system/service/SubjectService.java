package com.oerms.online_exam_system.service;

import com.oerms.online_exam_system.dto.SubjectRequest;
import com.oerms.online_exam_system.dto.SubjectResponse;
import com.oerms.online_exam_system.entity.Subject;
import com.oerms.online_exam_system.exception.DuplicateResourceException;
import com.oerms.online_exam_system.exception.ResourceNotFoundException;
import com.oerms.online_exam_system.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SubjectService — business logic for subject management.
 *
 * Enforces:
 *  - Unique subject name (throws DuplicateResourceException → 409 Conflict)
 *  - Unique subject code (throws DuplicateResourceException → 409 Conflict)
 *  - 404 for missing subjects
 */
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjectRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Subject with name '" + request.getName() + "' already exists");
        }
        if (subjectRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                    "Subject with code '" + request.getCode() + "' already exists");
        }

        Subject subject = Subject.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())   // normalize code to uppercase
                .description(request.getDescription())
                .build();

        return mapToResponse(subjectRepository.save(subject));
    }

    // ─── READ ────────────────────────────────────────────────────────────────

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with id: " + id));
        return mapToResponse(subject);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with id: " + id));

        // If name changed, check it doesn't conflict with another subject
        if (!subject.getName().equalsIgnoreCase(request.getName())
                && subjectRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Subject with name '" + request.getName() + "' already exists");
        }
        // If code changed, check it doesn't conflict
        if (!subject.getCode().equalsIgnoreCase(request.getCode())
                && subjectRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                    "Subject with code '" + request.getCode() + "' already exists");
        }

        subject.setName(request.getName());
        subject.setCode(request.getCode().toUpperCase());
        subject.setDescription(request.getDescription());

        return mapToResponse(subjectRepository.save(subject));
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .description(subject.getDescription())
                .createdAt(subject.getCreatedAt())
                .build();
    }
}
