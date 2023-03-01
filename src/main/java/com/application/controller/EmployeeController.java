package com.application.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.DailyUpdatesRepository;
import com.application.repository.QualificationRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.response.ProfileResponse;
import com.application.security.jwt.JwtUtils;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	QualificationRepository qualificationRepository;
	
	@Autowired
	DailyUpdatesRepository dailyUpdatesRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	EntityManager entityManager;

	@GetMapping("/hr-details")
	public ResponseEntity<ProfileResponse> hrDetails(
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current employee
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> employeeRef = userRepository.findByUsername(username);
		User employee = employeeRef.get();
		UserDetails employeeDetails =
				userDetailsRepository.findByUser(employee);
		Project project = employeeDetails.getProject();
		
		// Get hr details for this project
		UserDetails hrDetails = userDetailsRepository.findByProjectAndRole(
				project, Role.ROLE_HR).get(0);
		
		ProfileResponse profile = new ProfileResponse();
		profile.setAddress(hrDetails.getAddress());
		profile.setBloodGroup(hrDetails.getBloodGroup());
		profile.setContact(hrDetails.getContact());
		profile.setDateOfBirth(hrDetails.getDateOfBirth());
		profile.setEmergencyContact(hrDetails.getEmergencyContact());
		profile.setEmployeedId(hrDetails.getEmployeedId());
		profile.setId(hrDetails.getId());
		profile.setJoiningDate(hrDetails.getJoiningDate());
		profile.setName(hrDetails.getName());
		profile.setUser(hrDetails.getUser());

		return new ResponseEntity<ProfileResponse>(profile, HttpStatus.OK);
	}
}
