package com.application.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.entity.User;
import com.application.repository.UserRepository;
import com.application.request.LoginRequest;
import com.application.response.LoginResponse;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<LoginResponse> authenticateUser(
		@RequestBody LoginRequest credentials,
		HttpServletRequest request
	) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						credentials.getUsername(), credentials.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		request.getSession().setMaxInactiveInterval(3600);
		request.getSession().setAttribute("Authorization", "Bearer "+jwt);
		
		Optional<User> user = userRepository.findByUsername(credentials.getUsername());
		
		LoginResponse response = new LoginResponse();
		response.setAccessToken(jwt);
		response.setUser(user.get());
		return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/reset-password")
	public String resetPassword() {
		return "resetPassword";
	}
	

	@PostMapping("/reset-password")
	public String resetPassword(
		@RequestParam String username,
		@RequestParam String email,
		@RequestParam String password,
		HttpServletRequest request,
		Model model
	) {
		// Need to add email verification by otp 
		
		Optional<User> userRef = userRepository.findByUsernameAndEmail(username, email);
		
		if (userRef.isEmpty()) {
			model.addAttribute("error", "User not found");
			return "redirect:/error";
		}
		
		User user = userRef.get();
		
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
		
		return "redirect:/";
	}
	
	@GetMapping("/signout")
	public String signout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
}
