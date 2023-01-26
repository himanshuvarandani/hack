package com.application.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping("/")
	public String home(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
			model.addAttribute("jwtToken", headerAuth.substring(7, headerAuth.length()));
		return "index";
	}
	
	@GetMapping("/signin")
	public String signin(HttpServletRequest request) {
		return "login";
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String profile(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		model.addAttribute("email", user.get().getEmail());
		model.addAttribute("userDetails", userDetails);
		model.addAttribute("jwtToken", token);
		return "profile";
	}
	
	@GetMapping("/employee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String employee(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");

		model.addAttribute("jwtToken", headerAuth.substring(7, headerAuth.length()));
		return "employee";
	}
}
