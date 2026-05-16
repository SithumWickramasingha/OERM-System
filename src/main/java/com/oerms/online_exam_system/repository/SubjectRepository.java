package com.oerms.online_exam_system.repository;

import com.oerms.online_exam_system.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * SubjectRepository — CRUD for Subject entity.
 * Includes a lookup by unique code for duplicate-check in the service layer.
 */
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /** Check whether a subject with the given code already exists */
    boolean existsByCode(String code);

    /** Check whether a subject with the given name already exists */
    boolean existsByName(String name);

    Optional<Subject> findByCode(String code);
}
