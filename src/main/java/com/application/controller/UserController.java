package com.application.controller;

import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.application.entity.DailyUpdate;
import com.application.entity.Qualification;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.DailyUpdatesRepository;
import com.application.repository.QualificationRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.request.ProfileRequest;
import com.application.request.QualificationRequest;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
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
	
	@GetMapping("/")
	public String home(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");

		if (
			StringUtils.hasText(headerAuth) &&
			headerAuth.startsWith("Bearer ")
		) {
			String token = headerAuth.substring(7, headerAuth.length());
			
			if (jwtUtils.validateJwtToken(token)) {
				// Get user from token
				String username = jwtUtils.getUserNameFromJwtToken(token);
				Optional<User> user = userRepository.findByUsername(username);
				
				model.addAttribute("role", user.get().getRole().name());
				model.addAttribute("jwtToken", headerAuth.substring(7, headerAuth.length()));
			}
		}
		return "index";
	}
	
	@GetMapping("/signin")
	public String signin(HttpSession session) {
		String headerAuth = (String) session.getAttribute("Authorization");
		
		if (
			StringUtils.hasText(headerAuth) &&
			headerAuth.startsWith("Bearer ") &&
			jwtUtils.validateJwtToken(headerAuth.substring(7, headerAuth.length()))
		)
			return "redirect:/";
		return "login";
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String profile(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		model.addAttribute("role", user.get().getRole().name());
		model.addAttribute("email", user.get().getEmail());
		model.addAttribute("userDetails", userDetails);
		model.addAttribute("jwtToken", token);
		return "profile";
	}
	
	@GetMapping("/edit-profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String editProfile(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		model.addAttribute("role", user.get().getRole().name());
		model.addAttribute("email", user.get().getEmail());
		model.addAttribute("userDetails", userDetails);
		model.addAttribute("jwtToken", token);
		return "editProfile";
	}
	
	@PostMapping("/edit-profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String postEditProfile(ProfileRequest profile, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
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
		
		return "redirect:/profile";
	}

	@PostMapping("/add-qualification")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String addQualification(QualificationRequest qualificationBody, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
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
		
		return "redirect:/profile";
	}
	
	@PostMapping("/edit-qualification/{qualificationId}")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String editQualification(
		@PathVariable("qualificationId") Integer qualiicationId,
		QualificationRequest qualificationBody,
		HttpSession session,
		Model model
	) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		model.addAttribute("role", user.get().getRole().name());
		model.addAttribute("jwtToken", token);
		
		// Fetch qualification and save after validating
		Optional<Qualification> qualificationRef = qualificationRepository.findById(qualiicationId);
		if (qualificationRef.isEmpty()) {
			model.addAttribute("error", "Wrong Qualification Id");
			return "error";
		}
		
		Qualification qualification = qualificationRef.get();
		if (qualification.getUserDetails() != userDetails) {
			model.addAttribute("error", "Forbidden, Don't have rights");
			return "error";
		}
		
		qualification.setDegree(qualificationBody.getDegree());
		qualification.setCollege(qualificationBody.getCollege());
		qualification.setStream(qualificationBody.getStream());
		qualification.setSession(qualificationBody.getSession());
		qualification.setMarks(qualificationBody.getMarks());
		qualification.setUserDetails(userDetails);
		qualification = qualificationRepository.save(qualification);
		
		return "redirect:/profile";
	}
	
	@PostMapping("/daily-update")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public String dailyUpdate(
			String description,
			Integer duration,
			HttpSession session,
			Model model
	) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		Date today = new Date(new java.util.Date().getTime());
		
		Optional<DailyUpdate> dailyUpdateRef = dailyUpdatesRepository.findByUserAndDate(
				user,
				today
		);
		
		if (dailyUpdateRef.isEmpty()) {
			DailyUpdate dailyUpdate = new DailyUpdate();
			dailyUpdate.setDate(today);
			dailyUpdate.setDescription(description);
			dailyUpdate.setDuration(duration);
			dailyUpdate.setUser(user);
			dailyUpdatesRepository.save(dailyUpdate);
		} else {
			DailyUpdate dailyUpdate = dailyUpdateRef.get();
			dailyUpdate.setDescription(description);
			dailyUpdate.setDuration(duration);
			dailyUpdatesRepository.save(dailyUpdate);
		}
		
		return "redirect:/employee";
	}
}
