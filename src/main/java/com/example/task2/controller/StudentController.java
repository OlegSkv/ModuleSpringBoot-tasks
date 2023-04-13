package com.example.task2.controller;

import com.example.task2.exception.StudentNotFoundException;
import com.example.task2.model.Student;
import com.example.task2.service.StudentService;
import com.example.task2.utils.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // curl -v -H "Content-Type: application/json" -X POST -d "{\"firstName\": \"Ivan\", \"lastName\": \"Orlov\"}" http://localhost:8080/api/students
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Student newStudent(@RequestBody @Valid Student newStudent) {
        return studentService.create(newStudent);
    }

    // curl -v localhost:8080/api/students | jq
    // jq is Windows utility for pretty print JSON https://stedolan.github.io/jq/
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAll();
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.get(id);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody @Valid Student updatedStudent) {
        updatedStudent.setId(id);
        return studentService.update(updatedStudent);
    }

    @DeleteMapping("/{id}")
    void deleteEmployee(@PathVariable Long id) {
        studentService.delete(id);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponseBody studentNotFoundHandler(StudentNotFoundException ex, HttpServletRequest request) {
        return new ErrorResponseBody(ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleValidationExceptions(MethodArgumentNotValidException ex,
                                                        HttpServletRequest request) {
        return new ErrorResponseBody(getValidationErrors(ex), request.getRequestURI());
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
