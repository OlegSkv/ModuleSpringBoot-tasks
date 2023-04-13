package com.example.task2.exception;

import com.example.task2.controller.StudentController;
import com.example.task2.utils.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// ^1 uncomment the annotation to use global error handling for all controllers in the package of StudentController
// (com.example.task2.controller) and all child packages (for ex.: com.example.task2.controller.special)
// comment all @ExceptionHandler in StudentController, otherwise all handlers in StudentController will have precedence
//@ControllerAdvice(basePackageClasses = {StudentController.class})
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<ErrorResponseBody> studentNotFoundHandler(StudentNotFoundException ex, HttpServletRequest request) {
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(ex.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseBody);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(getValidationErrors(ex), uri);
        return ResponseEntity
                .status(status.value())
                .body(errorResponseBody);
    }

    private String getValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors.toString();
    }
}
