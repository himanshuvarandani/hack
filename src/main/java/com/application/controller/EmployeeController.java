package com.application.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.application.entity.Project;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.QualificationRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
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
	JwtUtils jwtUtils;
	
	@Autowired
	EntityManager entityManager;

	@GetMapping(value={"", "/"})
	public String employee(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current employee
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> employee = userRepository.findByUsername(username);
		UserDetails employeeDetails = userDetailsRepository.findByUser(employee.get());
		Project project = employeeDetails.getProject();
		
		model.addAttribute("project", project);
		model.addAttribute("jwtToken", token);
		return "employee";
	}
}
