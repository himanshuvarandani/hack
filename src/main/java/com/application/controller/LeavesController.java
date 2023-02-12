package com.application.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.entity.Leaves;
import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.LeavesRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.request.LeaveRequest;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/leaves")
public class LeavesController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LeavesRepository leaveRepository;
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	@Autowired
	private JwtUtils jwtUtils;

	@GetMapping(value={"", "/"})
	public String leaves(@RequestParam(name="approved", required=false) Boolean approved, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get current user
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		List<Leaves> leaves;
		if (approved == null) {
			leaves = leaveRepository.findByUser(user);
		} else {
			leaves = leaveRepository.findByUserAndApproved(user, approved);
		}
		
		model.addAttribute("leaves", leaves);
		return "leaves";
	}
	
	@GetMapping("/toApprove")
	public String leavesToApprove(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get current user
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> approverRef = userRepository.findByUsername(username);
		User approver = approverRef.get();
		
		List<Leaves> leaves = leaveRepository.findByApproverAndApproved(approver, false);
		
		model.addAttribute("leaves", leaves);
		return "approveLeaves";
	}
	
	@PostMapping("/apply")
	public String applyLeave(LeaveRequest leaveData, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get current user
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> userRef = userRepository.findByUsername(username);
		User user = userRef.get();
		
		// Get approver for this leave
		User approver;
		if (user.getRole() == Role.ROLE_EMPLOYEE) {
			UserDetails userDetails = userDetailsRepository.findByUser(user);
			Project project = userDetails.getProject();
			
			// Get hr details for this project
			List<UserDetails> hrDetails = userDetailsRepository.findByProjectAndRole(project, Role.ROLE_HR);
			approver = hrDetails.get(0).getUser();
		} else {
			approver = userRepository.findByRole(Role.ROLE_ADMIN).get(0);
		}
		
		Leaves leave = new Leaves();
		leave.setStartDate(leaveData.getStartDate());
		leave.setEndDate(leaveData.getEndDate());
		leave.setReason(leaveData.getReason());
		leave.setUser(user);
		leave.setApprover(approver);
		leaveRepository.save(leave);
		
		return "redirect:/leaves/toApprove";
	}
	
	@SuppressWarnings("deprecation")
	@PostMapping("/approve/{leaveId}")
	public String approveLeave(@PathVariable("leaveId") Integer leaveId, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get current user
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> approverRef = userRepository.findByUsername(username);
		User approver = approverRef.get();
		
		Leaves leave = leaveRepository.getById(leaveId);
		if (leave.getApprover() != approver)
			return "redirect:/error";
		
		leave.setApproved(true);
		leaveRepository.save(leave);
		
		return "redirect:/leaves/toApprove";
	}
}
