package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

/*
 * This EndToEndStudentTest shows how to use selenium testing using the web driver 
 * with Chrome browser to verify a new student can be added to the database using 
 * front-end and back-end of the registration service 
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *    Make sure that TEST_COURSE_ID is a valid course for TEST_SEMESTER.
 *    
 *    URL is the server on which Node.js is running.
 */


@SpringBootTest

public class EndToEndStudentTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/rebeca/Downloads/chromedriver";

	public static final String URL = "http://localhost:3000";
	
	public static final int TEST_USER_ID = 00000; 

	public static final String TEST_USER_EMAIL = "mytest500@csumb.edu";
	
	public static final String TEST_USER_NAME = "TEST USERNAME";
	
	public static final int TEST_COURSE_ID = 40443; 

	public static final String TEST_SEMESTER = "2021 Fall";

	public static final int SLEEP_DURATION = 1000; // 1 second.

	/*
	 * When running in @SpringBootTest environment, database repositories can be used
	 * with the actual database.
	 */
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Test
	public void addStudentTest() throws Exception {
		
		/*
		 * if student is already added, then delete the addition.
		 */
		
		Student myNewStudent = null; 
	    do {
	     myNewStudent = studentRepository.findByEmail(TEST_USER_EMAIL);
	     if (myNewStudent != null)
	    	 studentRepository.delete(myNewStudent);
	     } while (myNewStudent != null);
		
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		
		// set the driver location and start driver
				//@formatter:off
				// browser	property name 				Java Driver Class
				// edge 	webdriver.edge.driver 		EdgeDriver
				// FireFox 	webdriver.firefox.driver 	FirefoxDriver
				// IE 		webdriver.ie.driver 		InternetExplorerDriver
				//@formatter:on
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {
			
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.xpath("//a")).click();
			
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("addStudentID1")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// use xPath to find elements, input values, and add new student
			
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			driver.findElement(By.id("addStudentID2")).click();
			
			Thread.sleep(SLEEP_DURATION);
			
			// check toast message was sent 
			
			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			
			assertEquals("Student was added to database", toast_text); 
			
			// make sure that the student is now in the database as a new student
			
			Student checkStudent = studentRepository.findByEmail(TEST_USER_EMAIL);
			assertNotNull(checkStudent, "Could not find student with email " + TEST_USER_EMAIL + "in database. Please try again. ");
			
		} catch (Exception ex) {
			
			throw ex;
			
		} finally {
	
			// revert database to original format by deleting student you added 
			// this makes test self-checking & repeatable 
			Student testStudent = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (testStudent != null)
				studentRepository.delete(testStudent);
	
			driver.quit();
		}
	}
}