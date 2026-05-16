package com.oerms.online_exam_system.repository;

import com.oerms.online_exam_system.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ResultRepository — CRUD for Result entity.
 *
 * Spring Data JPA generates queries from method names automatically.
 */
public interface ResultRepository extends JpaRepository<Result, Long> {

    /**
     * Get all results for a specific student.
     * Used by:
     *  - TEACHER viewing any student's results  (GET /api/results/student/{studentId})
     *  - STUDENT viewing their own results       (GET /api/results/my-results)
     */
    List<Result> findByStudentId(Long studentId);

    /**
     * Get all results for a specific exam.
     * Used by TEACHER to see all students' marks for an exam.
     * (GET /api/results/exam/{examId})
     */
    List<Result> findByExamId(Long examId);

    /**
     * Check whether a result already exists for a student-exam pair.
     * Prevents duplicate publishing (also enforced at DB level via unique constraint).
     */
    boolean existsByStudentIdAndExamId(Long studentId, Long examId);
}
