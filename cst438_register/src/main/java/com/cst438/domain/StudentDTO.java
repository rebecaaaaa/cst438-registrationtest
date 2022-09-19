package com.cst438.domain;

public class StudentDTO {
	
	public int student_id;
	public String email;
	public String name; 
	public int statusCode;
	public String status; 
	
	
	public StudentDTO() {
		this.student_id = 0;
		this.email = null;
		this.name = null;
		this.statusCode = 0;
		this.status = null;
		
	}



	public StudentDTO(int student_id, String email, String name, int statusCode, String status) {
		super();
		this.student_id = student_id;
		this.email = email;
		this.name = name;
		this.statusCode = statusCode;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "StudentDTO [student_id=" + student_id + ", email=" + email + ", name=" + name + ", statusCode="
				+ statusCode + ", status=" + status + "]";
	}
	

}
