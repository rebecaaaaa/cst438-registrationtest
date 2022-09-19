package com.cst438;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.ScheduleController;
import com.cst438.controller.StudentController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

import org.junit.jupiter.api.Test;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest


class JunitTestStudent {
	
	static final String URL = "http://localhost:8080";
	public static final String TEST_STUDENT_EMAIL = "unitTest@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "unitTest";
	public static final int TEST_YEAR = 2021;
	public static final int TEST_STUDENT_ID = 12345;
	public static final String TEST_SEMESTER = "Fall";
	
	@MockBean
	CourseRepository courseRepository;

	@MockBean
	StudentRepository studentRepository;

	@MockBean
	EnrollmentRepository enrollmentRepository;

	@MockBean
	GradebookService gradebookService;

	@Autowired
	private MockMvc mvc;

	@Test
	public void addStudent()  throws Exception {
		
		MockHttpServletResponse response;
		
		
		// set the Student Info
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStudent_id(TEST_STUDENT_ID);
		student.setName(TEST_STUDENT_NAME);
		
		// given  -- stubs for database repositories that return test data
		

		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		// create the DTO (data transfer object) for the admin to add student.  
		
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME; 
		
		
		// then do an http post request with body of StudentDTO as JSON
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content(asJsonString(studentDTO))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that returned data has non zero primary key
		StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertNotEquals(0  , result.student_id);
				
		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
		
		
		
	}
	
	@Test
	public void putHold()  throws Exception {
		
		MockHttpServletResponse response;
		
		
		// set the Student Info
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatus(null);
	
		// given  -- stubs for database repositories that return test data
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		// create the DTO (data transfer object) for the admin to add student.  
		
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME; 
		
		// then do an http post request with body of StudentDTO as JSON
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student/putHold/unitTest@csumb.edu")
			      .content(asJsonString(studentDTO))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
				
		// verify that repository findByEmail method was called.
		verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);
		
		// verify status code is now equal to 1
		
		boolean found = false;		
		if (student.getStatusCode() == 1) {
			found = true;
		}
		assertEquals(true, found, "Status code is now 1");
		
		
		
	}
	
	@Test
	public void releaseHold()  throws Exception {
		
		MockHttpServletResponse response;
		
		
		// set the Student Info
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatus(null);
		student.setStatusCode(1);
	
		
		// given  -- stubs for database repositories that return test data
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		// create the DTO (data transfer object) for the admin to add student.  
		
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME;
		
		
		// then do an http post request with body of StudentDTO as JSON
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student/releaseHold/unitTest@csumb.edu")
			      .content(asJsonString(studentDTO))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
				
		// verify that repository findByEmail method was called.
		verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);
		
		// verify status code is now equal to 0
		boolean found = false;		
		if (student.getStatusCode() == 0) {
			found = true;
		}
		assertEquals(true, found, "Status code is now 0");
		
		
		
	}

	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
		

}
