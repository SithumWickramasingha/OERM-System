package com.oerms.online_exam_system.repository;

import com.oerms.online_exam_system.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ExamRepository — Spring Data JPA repository for the Exam entity.
 *
 * Inherits: save, findById, findAll, deleteById, existsById, etc.
 *
 * Custom query: find all exams created by a specific teacher (by User ID).
 */
public interface ExamRepository extends JpaRepository<Exam, Long> {

    /**
     * Finds all exams created by a teacher, identified by their User ID.
     * Spring Data JPA automatically generates the query from the method name:
     * SELECT * FROM exams WHERE created_by_id = :teacherId
     */
    List<Exam> findByCreatedById(Long teacherId);
}
