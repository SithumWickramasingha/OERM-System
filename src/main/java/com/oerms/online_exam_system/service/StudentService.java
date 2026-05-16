package com.oerms.online_exam_system.service;

import com.oerms.online_exam_system.dto.StudentResponse;
import com.oerms.online_exam_system.entity.Role;
import com.oerms.online_exam_system.entity.User;
import com.oerms.online_exam_system.exception.ResourceNotFoundException;
import com.oerms.online_exam_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StudentService — teacher-facing APIs for fetching student information.
 *
 * Only returns users with role = STUDENT.
 * Teachers use this to look up student IDs before publishing results.
 */
@Service
public class StudentService {

    private final UserRepository userRepository;

    public StudentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Returns all registered students (role = STUDENT). */
    public List<StudentResponse> getAllStudents() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns a single student by ID.
     * Throws 404 if not found, or if the user exists but is not a STUDENT.
     */
    public StudentResponse getStudentById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + id));

        if (user.getRole() != Role.STUDENT) {
            throw new ResourceNotFoundException(
                    "User with id " + id + " is not a student");
        }

        return mapToResponse(user);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private StudentResponse mapToResponse(User user) {
        return StudentResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
