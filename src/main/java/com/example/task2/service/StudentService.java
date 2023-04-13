package com.example.task2.service;

import com.example.task2.model.Student;

import java.util.List;

public interface StudentService {
    Student create(Student student);

    List<Student> getAll();

    Student get(Long id);

    Student update(Student student);

    void delete(Long id);
}
