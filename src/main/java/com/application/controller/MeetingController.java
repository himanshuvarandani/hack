package com.application.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.application.entity.Meeting;
import com.application.entity.User;
import com.application.repository.MeetingRepository;
import com.application.repository.UserRepository;
import com.application.request.MeetingRequest;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/meetings")
public class MeetingController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MeetingRepository meetingRepository;
	@Autowired
	private JwtUtils jwtUtils;
	

	@GetMapping(value={"", "/"})
	public ResponseEntity<List<Meeting>> meetings(HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get current user
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		List<Meeting> meetings = meetingRepository.findByParticipant(user);
		
		return new ResponseEntity<List<Meeting>>(meetings, HttpStatus.OK);
	}
		
	@PostMapping("/create")
	public ResponseEntity<Object> createMeetings(@RequestBody MeetingRequest meetingData, HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get current user
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		List<User> participants = new ArrayList<User>();
		List<String> participantsId = Arrays.asList(meetingData.getParticipants().split(","));
		participants.add(user);
		participantsId.forEach(participantName -> {
			Optional<User> participant = userRepository.findByUsername(participantName);
			if (participant.isPresent())
				participants.add(participant.get());
		});
		
		Meeting meeting = new Meeting();
		meeting.setStartTime(meetingData.getStartTime());
		meeting.setEndTime(meetingData.getEndTime());
		meeting.setTitle(meetingData.getTitle());
		meeting.setParticipants(participants);
		meetingRepository.save(meeting);
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
}
