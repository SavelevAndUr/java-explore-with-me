package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestControllerAdvice("ru.practicum")
public class ErrorHandler {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(final BadRequestException e) {
        return new ResponseEntity<>((Map.of(
                "error", e.getMessage(),
                "reason", e.getReason(),
                "timestamp", e.getTimeStamp().format(FORMAT),
                "status", e.getStatus())),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleConflictException(final ConflictException e) {
        return new ResponseEntity<>((Map.of(
                "error", e.getMessage(),
                "reason", e.getReason(),
                "timestamp", e.getTimeStamp().format(FORMAT),
                "status", e.getStatus())),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>((Map.of(
                "error", e.getMessage(),
                "reason", e.getReason(),
                "timestamp", e.getTimeStamp().format(FORMAT),
                "status", e.getStatus())),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException e) {
        return new ResponseEntity<>((Map.of(
                "status", "400",
                "reason", e.getMessage())),
                HttpStatus.BAD_REQUEST);
    }
}
