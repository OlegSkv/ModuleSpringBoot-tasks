package com.example.task2.controller;

import com.example.task2.exception.StudentNotFoundException;
import com.example.task2.model.Student;
import com.example.task2.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false) // disable security
class StudentControllerTest {

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ERROR_MESSAGE = "error";

    @MockBean
    MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void newStudent_shouldReturnCreatedStudent() throws Exception {
        given(studentService.create(any(Student.class))).willReturn(new Student(1, "name", "surname", 30));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .content(asJsonString(new Student(0, "name", "surname", 30)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath(ID).value(1))
                .andExpect(jsonPath(FIRST_NAME).value("name"))
                .andExpect(jsonPath(LAST_NAME).value("surname"));
    }

    @Test
    void newStudent_shouldReturnBadRequestWithMessageWhenMandatoryFieldIsNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .content("{ \"firstName\" : null, \"lastName\" : \"surname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE).exists());

        verify(messageSource, times(1)).getMessage(any(), any(), any(), any());
    }

    @Test
    void newStudent_shouldReturnBadRequestWithMessageWhenMandatoryFieldIsAbsent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .content("{ \"lastName\" : \"surname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE).exists());
    }

    @Test
    void newStudent_shouldReturnBadRequestWithMessageWhenMandatoryFieldIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .content("{ \"firstName\" : \"\", \"lastName\" : \"surname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE).exists());
    }

    @Test
    void newStudent_shouldReturnBadRequestWithMessageWhenTwoValidationErrorsExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .content("{ \"firstName\" : \"\", \"wrongFieldName\" : \"surname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE).exists());

        verify(messageSource, times(2)).getMessage(any(), any(), any(), any());
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() throws Exception {
        given(studentService.getAll()).willReturn(List.of(
                new Student(2, "name2", "surname2", 30),
                new Student(1, "name1", "surname1", 30)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*]." + ID, containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*]." + FIRST_NAME, containsInAnyOrder("name1", "name2")))
                .andExpect(jsonPath("$[*]." + LAST_NAME, containsInAnyOrder("surname1", "surname2")));
    }

    @Test
    void getAllStudents_shouldReturnEmptyCollectionWhenNoStudents() throws Exception {
        given(studentService.getAll()).willReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getStudent_shouldReturnStudentById() throws Exception {
        long studentId = 1;
        given(studentService.get(studentId)).willReturn(new Student(studentId, "name", "surname", 30));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath(ID).value(studentId))
                .andExpect(jsonPath(FIRST_NAME).value("name"))
                .andExpect(jsonPath(LAST_NAME).value("surname"));
    }

    @Test
    void getStudent_shouldReturnBadRequestWithMessageWhenStudentDoesNotExist() throws Exception {
        long wrongStudentId = 1;
        given(studentService.get(wrongStudentId)).willThrow(new StudentNotFoundException("Error message"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/" + wrongStudentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath(ERROR_MESSAGE).value("Error message"));
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        long studentId = 1;
        given(studentService.update(any(Student.class)))
                .willReturn(new Student(studentId, "updated name", "updated surname", 30));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/students/" + studentId)
                        .content(asJsonString(new Student(studentId, "updated name", "updated surname", 30)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath(ID).value(studentId))
                .andExpect(jsonPath(FIRST_NAME).value("updated name"))
                .andExpect(jsonPath(LAST_NAME).value("updated surname"));
    }

    @Test
    void updateStudent_shouldReturnBadRequestWithBodyWhenStudentDoesNotExist() throws Exception {
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        long wrongStudentId = 1;
        given(studentService.update(any(Student.class)))
                .willThrow(new StudentNotFoundException("Error message"));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/students/" + wrongStudentId)
                        .content(asJsonString(new Student(0, "updated name", "updated surname", 30)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath(ERROR_MESSAGE).value("Error message"));

        verify(studentService).update(studentCaptor.capture());
        Student passedStudent = studentCaptor.getValue();
        assertThat(passedStudent.getId()).isEqualTo(wrongStudentId);
    }

    @Test
    void updateStudent_shouldReturnBadRequestWithBodyWhenValidationErrorsExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/students/1")
                        .content("{ \"firstName\" : \"\", \"lastName\" : \"surname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath(ERROR_MESSAGE).exists());
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
