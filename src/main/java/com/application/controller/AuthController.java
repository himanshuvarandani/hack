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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.application.entity.User;
import com.application.repository.UserRepository;
import com.application.request.LoginRequest;
import com.application.request.ResetPassRequest;
import com.application.response.LoginResponse;
import com.application.security.jwt.JwtUtils;

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
		@RequestBody LoginRequest credentials) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						credentials.getUsername(), credentials.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		Optional<User> user = userRepository.findByUsername(credentials.getUsername());
		
		LoginResponse response = new LoginResponse();
		response.setAccessToken(jwt);
		response.setUser(user.get());
		return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<Object> resetPassword(
		@RequestBody ResetPassRequest credentials) {
		// Need to add email verification by otp 
		
		Optional<User> userRef = userRepository.findByUsernameAndEmail(
				credentials.getUsername(), credentials.getEmail());
		
		if (userRef.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		User user = userRef.get();
		
		user.setPassword(passwordEncoder.encode(credentials.getPassword()));
		userRepository.save(user);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
