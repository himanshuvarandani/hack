package com.application.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.entity.Role;
import com.application.repository.UserRepository;
import com.application.security.jwt.JwtUtils;
import com.application.security.services.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public String authenticateUser(
		@RequestParam String username,
		@RequestParam String password,
		HttpServletRequest request,
		Model model
	) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		request.getSession().setMaxInactiveInterval(3600);
		request.getSession().setAttribute("Authorization", "Bearer "+jwt);
		
		model.addAttribute("jwtToken", jwt);
		if (roles.get(0)==Role.ROLE_HR.name())
			return "redirect:/hr";
		if (roles.get(0)==Role.ROLE_EMPLOYEE.name())
			return "redirect:/employee";
		return "redirect:/";
	}
	
	@GetMapping("/signout")
	public String signout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
}
