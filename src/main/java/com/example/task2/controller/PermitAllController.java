package com.example.task2.controller;

import com.example.task2.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/count")
public class PermitAllController {

    StudentService studentService;

    public PermitAllController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public int getNumberOfStudents() {
        return studentService.getAll().size();
    }
}
