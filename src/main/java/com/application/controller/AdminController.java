package com.application.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;
import java.util.Optional;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.ProjectRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping("/initialize-data")
	public String initializeData( HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> currentUser = userRepository.findByUsername(username);
		
		model.addAttribute("role", currentUser.get().getRole().name());
		model.addAttribute("jwtToken", token);
		return "initializeData";
	}
	
	@PostMapping("/initialize-data")
	public String postInitializeData(@RequestParam MultipartFile file, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get user details from token
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> currentUser = userRepository.findByUsername(username);
		
		model.addAttribute("role", currentUser.get().getRole().name());
		model.addAttribute("jwtToken", token);
		
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			
			Sheet sheet = workbook.getSheet(workbook.getSheetName(0));
			Iterator<Row> rows = sheet.iterator();
			rows.next();
			
			DataFormatter fmt = new DataFormatter();
			
			while (rows.hasNext()) {
				Row row = rows.next();
				
				// Create User with encoded password
				User user = new User();
				user.setUsername(row.getCell(0).getStringCellValue());
				user.setEmail(row.getCell(1).getStringCellValue());
				user.setPassword(passwordEncoder.encode(row.getCell(2).getStringCellValue()));
				user.setRole(Role.ROLE_HR);
				user = userRepository.save(user);
				
				// Create project
				Project project = new Project();
				project.setName(row.getCell(11).getStringCellValue());
				project.setUnit(row.getCell(12).getStringCellValue());
				project.setLocation(row.getCell(13).getStringCellValue());
				project.setCustomer(row.getCell(14).getStringCellValue());
				project = projectRepository.save(project);
				
				// Create user details with reference to above user and project
				UserDetails userDetails = new UserDetails();
				userDetails.setEmployeedId(Integer.parseInt(fmt.formatCellValue(row.getCell(3))));
				userDetails.setName(row.getCell(4).getStringCellValue());
				userDetails.setContact(Integer.parseInt(fmt.formatCellValue(row.getCell(5))));
				userDetails.setEmergencyContact((Integer.parseInt(fmt.formatCellValue(row.getCell(6)))));
				userDetails.setDateOfBirth(new Date(row.getCell(7).getDateCellValue().getTime()));
				userDetails.setAddress(row.getCell(8).getStringCellValue());
				userDetails.setBloodGroup(row.getCell(9).getStringCellValue());
				userDetails.setJoiningDate(new Date(row.getCell(10).getDateCellValue().getTime()));
				userDetails.setUser(user);
				userDetails.setProject(project);
				userDetailsRepository.save(userDetails);
			}
			
			workbook.close();
			
			return "redirect:/";
		} catch (IOException e) {
			model.addAttribute("error", "Fail to parse Excel file: "+e.getMessage());
			return "error";
	    }
	}
}
