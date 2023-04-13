package com.example.task2.service;

import com.example.task2.data.StudentRepository;
import com.example.task2.exception.StudentNotFoundException;
import com.example.task2.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private StudentRepository studentRepository;

    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentServiceImpl(messageSource, studentRepository);
    }

    @Test
    void create_shouldSaveStudent() {
        studentService.create(new Student(1, "firstName", "lastName", 30));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void getAll_shouldFindAllStudents() {
        studentService.getAll();
        verify(studentRepository).findAll();
    }

    @Test
    void get_shouldFindStudentById() {
        ArgumentCaptor<Long> studentIdCapture = ArgumentCaptor.forClass(Long.class);
        long studentId = 3;

        given(studentRepository.findById(studentId)).willReturn(Optional.of(new Student()));

        studentService.get(studentId);

        verify(studentRepository).findById(studentIdCapture.capture());
        assertThat(studentIdCapture.getValue()).isEqualTo(studentId);
    }

    @Test
    void get_shouldThrowExceptionIfStudentDoesNotExist() {
        long wrongStudentId = 4;
        given(studentRepository.findById(wrongStudentId)).willReturn(Optional.empty());
        given(messageSource.getMessage(any(), any(), any())).willReturn("Error message");

        assertThatThrownBy(() -> studentService.get(wrongStudentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Error message");

        verify(studentRepository).findById(anyLong());
        verify(messageSource).getMessage(any(), any(), any());
    }

    @Test
    void update_shouldUpdateValidStudent() {
        Student updatedStudent = new Student(2, "updated name", "updated surname", 30);
        Student originalStudent = new Student(2, "original name", "original surname", 30);
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);

        given(studentRepository.findById(updatedStudent.getId())).willReturn(Optional.of(originalStudent));
        given(studentRepository.save(any(Student.class))).willReturn(updatedStudent);

        studentService.update(updatedStudent);

        verify(studentRepository).findById(anyLong());
        verify(studentRepository).save(studentCaptor.capture());
        Student passedStudent = studentCaptor.getValue();
        assertThat(passedStudent.getId()).isEqualTo(originalStudent.getId());
        assertThat(passedStudent.getFirstName()).isEqualTo(updatedStudent.getFirstName());
        assertThat(passedStudent.getLastName()).isEqualTo(updatedStudent.getLastName());
    }

    @Test
    void update_shouldThrowStudentNotFoundExceptionOnInvalidStudent() {
        long wrongStudentId = 4;
        given(studentRepository.findById(wrongStudentId)).willReturn(Optional.empty());
        given(messageSource.getMessage(any(), any(), any())).willReturn("Error message");

        assertThatThrownBy(() -> studentService.update(new Student(wrongStudentId, "name", "surname", 30)))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Error message");

        verify(studentRepository).findById(anyLong());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void delete_shouldDeleteExistingStudent() {
        ArgumentCaptor<Long> studentIdCaptor = ArgumentCaptor.forClass(Long.class);
        given(studentRepository.existsById(anyLong())).willReturn(true);

        long studentId = 1L;
        studentService.delete(studentId);

        verify(studentRepository).existsById(studentIdCaptor.capture());
        verify(studentRepository).deleteById(studentIdCaptor.capture());
        List<Long> passedStudentIds = studentIdCaptor.getAllValues();

        assertThat(passedStudentIds).allMatch(id -> id == studentId);
    }

    @Test
    void delete_shouldThrowStudentNotFoundExceptionIfStudentDoesNotExist() {
        ArgumentCaptor<Long> studentIdCaptor = ArgumentCaptor.forClass(Long.class);
        given(studentRepository.existsById(anyLong())).willReturn(false);
        given(messageSource.getMessage(any(), any(), any())).willReturn("Error message");

        long studentId = 1L;

        assertThatThrownBy(() -> studentService.delete(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Error message");

        verify(studentRepository).existsById(anyLong());
        verify(messageSource).getMessage(any(), any(), any());
    }
}
