package com.oerms.online_exam_system.exception;

/**
 * Thrown when a resource with a unique constraint (name, code, etc.)
 * already exists in the database.
 * Mapped to HTTP 409 Conflict by GlobalExceptionHandler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
