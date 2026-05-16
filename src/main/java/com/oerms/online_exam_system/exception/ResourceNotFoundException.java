package com.oerms.online_exam_system.exception;

/**
 * Custom exception thrown when a requested resource is not found in the database.
 * Handled by GlobalExceptionHandler, which maps it to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
