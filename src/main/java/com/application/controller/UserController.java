package com.application.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {
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
	
	@GetMapping("/employee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String employee(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");

		model.addAttribute("jwtToken", headerAuth.substring(7, headerAuth.length()));
		return "employee";
	}
	
	@GetMapping("/hr")
	@PreAuthorize("hasRole('HR')")
	public String hr(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");

		model.addAttribute("jwtToken", headerAuth.substring(7, headerAuth.length()));
		return "hr";
	}
}
