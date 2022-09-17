package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;

import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@PostMapping("/student") 
	@Transactional 
	public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
		
		// FIRST: check if email exists in system already BEFORE you add a student
		
		Student studentInfo = studentRepository.findByEmail(studentDTO.email); 
		
		if (studentInfo == null) {
			
			Student student = new Student(); 
			
			
			// if student does not already exist, add email and name to student object
			student.setEmail(studentDTO.email); 
			student.setName(studentDTO.name); 
			
			// save your sets
			
			Student newStudent = studentRepository.save(student); 
			
			StudentDTO result = createStudentDTO(newStudent); 
			
			return result; 
					
			
		}
		else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "The email "+ studentDTO.email + " already exists.");
		}	
		
		
		
	}
	
	@PostMapping("/student/putHold/{email}") 
	@Transactional 
	public void putHold(@PathVariable String email) {
		
		Student student = studentRepository.findByEmail(email);
		
		if (student != null && student.getStatusCode() == 0) {
			student.setStatusCode(1);
			System.out.println("Hold on student with email " + email + " placed successfully"); 
			
		}
		else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "The hold on student with email "+ email + " unsuccessful");
			
		}
			
		
	}
	
	
	
	@PostMapping("/student/releaseHold/{email}") 
	@Transactional	
	public void releaseHold(@PathVariable String email) {
		
		Student student = studentRepository.findByEmail(email); 
		
		if (student != null && student.getStatusCode() != 0) {
			student.setStatusCode(0);
			System.out.println("Hold on student with email " + email + " released successfully"); 
			
		}
		else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "The release hold on student with email "+ email + " has been unsuccessful");
		}
	}
	
	

	
	
	private boolean emailExists(String email) {
		Student student = studentRepository.findByEmail(email); 
		if (student !=null) {
			return false; 
		}
		return true; 
	}
	
	
	private StudentDTO createStudentDTO(Student s) {
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.student_id = s.getStudent_id();
		studentDTO.email= s.getEmail();
		studentDTO.name = s.getName();
		studentDTO.status = s.getStatus();
		studentDTO.statusCode = s.getStatusCode();
		
		return studentDTO;
	}
	
	
	
	

}
