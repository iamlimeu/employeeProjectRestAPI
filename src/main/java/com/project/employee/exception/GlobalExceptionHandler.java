package com.project.employee.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException e,
                                                          HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Not Found", e.getMessage(), request.getRequestURI());
        log.error("Ресурс не найден: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequestException(BadRequestException e,
                                                    HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Bad Request", e.getMessage(), request.getRequestURI());
        log.error("Неправильный запрос: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Validation Error", "Validation failed for your request", request.getRequestURI());

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", e.getMessage(), request.getRequestURI());
        log.error("Неожиданная ошибка в системе: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
