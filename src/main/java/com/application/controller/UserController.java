package com.application.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.application.entity.Qualification;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.QualificationRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.request.ProfileRequest;
import com.application.request.QualificationRequest;
import com.application.security.jwt.JwtUtils;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {
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
	
	@GetMapping("/edit-profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String editProfile(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		model.addAttribute("email", user.get().getEmail());
		model.addAttribute("userDetails", userDetails);
		model.addAttribute("jwtToken", token);
		return "editProfile";
	}
	
	@PostMapping("/edit-profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String postEditProfile(ProfileRequest profile, HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		userDetails.setContact(profile.getContact());
		userDetails.setEmergencyContact(profile.getEmergencyContact());
		userDetails.setDateOfBirth(profile.getDateOfBirth());
		userDetails.setBloodGroup(profile.getBloodGroup());
		userDetails.setAddress(profile.getAddress());
		userDetails.setJoiningDate(profile.getJoiningDate());
		userDetailsRepository.save(userDetails);
		
		return "redirect:/profile?authorization=Bearer%20"+token;
	}

	@PostMapping("/add-qualification")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String addQualification(QualificationRequest qualificationBody, HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		// Create new qualification and save with reference to userDetails
		Qualification qualification = new Qualification();
		qualification.setDegree(qualificationBody.getDegree());
		qualification.setCollege(qualificationBody.getCollege());
		qualification.setStream(qualificationBody.getStream());
		qualification.setSession(qualificationBody.getSession());
		qualification.setMarks(qualificationBody.getMarks());
		qualification.setUserDetails(userDetails);
		qualification = qualificationRepository.save(qualification);
		
		return "redirect:/profile?authorization=Bearer%20"+token;
	}
	
	@PostMapping("/edit-qualification/{qualificationId}")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String editQualification(@PathVariable("qualificationId") Integer qualiicationId, QualificationRequest qualificationBody, HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		// Fetch qualification and save after validating
		Optional<Qualification> qualificationRef = qualificationRepository.findById(qualiicationId);
		if (qualificationRef.isEmpty()) {
			return "redirect:/error";
		}
		
		Qualification qualification = qualificationRef.get();
		if (qualification.getUserDetails() != userDetails) {
			return "redirect:/error";
		}
		
		qualification.setDegree(qualificationBody.getDegree());
		qualification.setCollege(qualificationBody.getCollege());
		qualification.setStream(qualificationBody.getStream());
		qualification.setSession(qualificationBody.getSession());
		qualification.setMarks(qualificationBody.getMarks());
		qualification.setUserDetails(userDetails);
		qualification = qualificationRepository.save(qualification);
		
		return "redirect:/profile?authorization=Bearer%20"+token;
	}
}
