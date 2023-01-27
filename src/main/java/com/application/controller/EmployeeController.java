package com.application.controller;

import java.util.List;
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
import jakarta.servlet.http.HttpSession;

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
	public String employee(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current employee
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> employee = userRepository.findByUsername(username);
		UserDetails employeeDetails = userDetailsRepository.findByUser(employee.get());
		Project project = employeeDetails.getProject();
		
		// Get hr details for this project
		List<UserDetails> hrDetails = userDetailsRepository.findByProject(project);
		
		model.addAttribute("role", employee.get().getRole().name());
		model.addAttribute("project", project);
		model.addAttribute("hrDetails", hrDetails.get(0));
		model.addAttribute("jwtToken", token);
		return "employee";
	}
}
