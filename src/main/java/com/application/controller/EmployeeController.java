package com.application.controller;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.application.entity.DailyUpdate;
import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.DailyUpdatesRepository;
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
	DailyUpdatesRepository dailyUpdatesRepository;
	
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
		Optional<User> employeeRef = userRepository.findByUsername(username);
		User employee = employeeRef.get();
		UserDetails employeeDetails = userDetailsRepository.findByUser(employee);
		Project project = employeeDetails.getProject();
		
		// Get hr details for this project
		List<UserDetails> hrDetails = userDetailsRepository.findByProjectAndRole(project, Role.ROLE_HR);
		
		
		// Check for todays update
		Optional<DailyUpdate> dailyUpdate = dailyUpdatesRepository.findByUserAndDate(
				employee,
				new Date(new java.util.Date().getTime())
		);
		
		if (dailyUpdate.isEmpty()) {
			model.addAttribute("dailyUpdate", null);
		} else {
			model.addAttribute("dailyUpdate", dailyUpdate.get());
		}
		
		
		// Give all updates
		List<DailyUpdate> dailyUpdates = dailyUpdatesRepository.findByUser(employee);
		
		model.addAttribute("dailyUpdates", dailyUpdates);
		model.addAttribute("role", employee.getRole().name());
		model.addAttribute("project", project);
		model.addAttribute("hrDetails", hrDetails.get(0));
		model.addAttribute("jwtToken", token);
		return "employee";
	}
}
