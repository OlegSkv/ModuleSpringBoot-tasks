package com.example.task2.service;

import com.example.task2.data.StudentRepository;
import com.example.task2.exception.StudentNotFoundException;
import com.example.task2.model.Student;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class StudentServiceImpl implements StudentService {

    public static final String WRONG_STUDENT_ID = "error.message.student.wrongId";

    private final MessageSource messageSource;
    private final StudentRepository studentRepository;

    public StudentServiceImpl(MessageSource messageSource, StudentRepository studentRepository) {
        this.messageSource = messageSource;
        this.studentRepository = studentRepository;
    }

    @Override
    public Student create(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student get(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> getStudentNotFoundException(id));
    }

    @Override
    public Student update(Student student) {
        long studentId = student.getId();
        return studentRepository.findById(studentId)
                .map(updatedStudent -> {
                    updatedStudent.setFirstName(student.getFirstName());
                    updatedStudent.setLastName(student.getLastName());
                    return studentRepository.save(updatedStudent);
                })
                .orElseThrow(() -> getStudentNotFoundException(studentId));
    }

    @Override
    public void delete(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        } else {
            throw getStudentNotFoundException(id);
        }
    }

    private StudentNotFoundException getStudentNotFoundException(Long id) {
        String message = messageSource.getMessage(WRONG_STUDENT_ID, new Object[]{id}, Locale.getDefault());
        return new StudentNotFoundException(message);
    }
}
