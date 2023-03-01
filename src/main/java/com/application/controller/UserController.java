package com.application.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.application.entity.DailyUpdate;
import com.application.entity.Project;
import com.application.entity.Qualification;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.DailyUpdatesRepository;
import com.application.repository.QualificationRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.request.ProfileRequest;
import com.application.request.QualificationRequest;
import com.application.response.ProfileResponse;
import com.application.response.QualificationResponse;
import com.application.security.jwt.JwtUtils;

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
	DailyUpdatesRepository dailyUpdatesRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping("/profile")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<ProfileResponse> profile(HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		ProfileResponse profile = new ProfileResponse();
		profile.setAddress(userDetails.getAddress());
		profile.setBloodGroup(userDetails.getBloodGroup());
		profile.setContact(userDetails.getContact());
		profile.setDateOfBirth(userDetails.getDateOfBirth());
		profile.setEmergencyContact(userDetails.getEmergencyContact());
		profile.setEmployeedId(userDetails.getEmployeedId());
		profile.setId(userDetails.getId());
		profile.setJoiningDate(userDetails.getJoiningDate());
		profile.setName(userDetails.getName());
		profile.setUser(userDetails.getUser());

		return new ResponseEntity<ProfileResponse>(profile, HttpStatus.OK);
	}
	
	@GetMapping("/qualifications")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<List<QualificationResponse>> qualifications(
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		List<QualificationResponse> qualifications = new ArrayList<QualificationResponse>();
		userDetails.getQualifications().forEach((q) -> {
			QualificationResponse qr = new QualificationResponse();
			qr.setCollege(q.getCollege());
			qr.setDegree(q.getDegree());
			qr.setId(q.getId());
			qr.setMarks(q.getMarks());
			qr.setSession(q.getSession());
			qr.setStream(q.getStream());
			
			qualifications.add(qr);
		});

		return new ResponseEntity<List<QualificationResponse>>(
				qualifications, HttpStatus.OK);
	}
	
	@PostMapping("/profile/edit")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<Object> postEditProfile(
			@RequestBody ProfileRequest profile,
			HttpServletRequest request
	) {
		String headerAuth = (String) request.getHeader("Authorization");
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
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/qualification/new")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<Object> addQualification(
			@RequestBody QualificationRequest qualificationBody,
			HttpServletRequest request
	) {
		String headerAuth = (String) request.getHeader("Authorization");
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
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/qualification/{qualificationId}/edit")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<Object> editQualification(
			@PathVariable("qualificationId") Integer qualiicationId,
			@RequestBody QualificationRequest qualificationBody,
			HttpServletRequest request
	) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> user = userRepository.findByUsername(username);
		UserDetails userDetails = userDetailsRepository.findByUser(user.get());
		
		// Fetch qualification and save after validating
		Optional<Qualification> qualificationRef =
				qualificationRepository.findById(qualiicationId);
		if (qualificationRef.isEmpty()) {
			// Wrong Qualification Id
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Qualification qualification = qualificationRef.get();
		if (qualification.getUserDetails() != userDetails) {
			// Forbidden, Don't have rights
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		qualification.setDegree(qualificationBody.getDegree());
		qualification.setCollege(qualificationBody.getCollege());
		qualification.setStream(qualificationBody.getStream());
		qualification.setSession(qualificationBody.getSession());
		qualification.setMarks(qualificationBody.getMarks());
		qualification.setUserDetails(userDetails);
		qualification = qualificationRepository.save(qualification);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/project")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<Project> project(
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		UserDetails userDetails =
				userDetailsRepository.findByUser(userRef.get());
		Project project = userDetails.getProject();
		
		return new ResponseEntity<Project>(project, HttpStatus.OK);
	}
	
	@GetMapping("/daily-updates")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<List<DailyUpdate>> dailyUpdates(
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		List<DailyUpdate> dailyUpdates = 
				dailyUpdatesRepository.findByUser(user);
		
		return new ResponseEntity<List<DailyUpdate>>(
				dailyUpdates, HttpStatus.OK);
	}
	
	@GetMapping("/daily-updates/today")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<DailyUpdate> todaysDailyUpdate(
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		Optional<DailyUpdate> dailyUpdate = 
				dailyUpdatesRepository.findByUserAndDate(
						user, new Date(new java.util.Date().getTime()));
		
		if (dailyUpdate.isEmpty())
			return new ResponseEntity<>(HttpStatus.OK);
		return new ResponseEntity<DailyUpdate>(
				dailyUpdate.get(), HttpStatus.OK);
	}
	
	@PostMapping("/daily-update")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
	public ResponseEntity<Object> postDailyUpdate(
			@RequestBody String description,
			@RequestBody Integer duration,
			HttpServletRequest request
	) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		Date today = new Date(new java.util.Date().getTime());
		
		Optional<DailyUpdate> dailyUpdateRef = 
				dailyUpdatesRepository.findByUserAndDate(user, today);
		
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
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
