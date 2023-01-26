package com.application.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
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

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasRole('HR')")
public class HRController {
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping(value={"", "/"})
	public String hr(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();
		
		model.addAttribute("project", project);
		model.addAttribute("jwtToken", token);
		return "hr";
	}
	
	@GetMapping("/edit-project")
	public String editProject(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		model.addAttribute("project", project);
		model.addAttribute("jwtToken", token);
		return "editProject";
	}

	@PostMapping("/edit-project")
	public String postEditProject(String location, HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();
		
		project.setLocation(location);
		projectRepository.save(project);
		
		return "redirect:/hr?authorization=Bearer%20"+token;
	}
	
	@GetMapping("/project/add-employees")
	public String addEmployees(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");

		model.addAttribute("jwtToken", headerAuth.substring(7, headerAuth.length()));
		return "addEmployees";
	}
	
	@PostMapping("/project/add-employees")
	public String postAddEmployees(@RequestParam MultipartFile file, HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			
			Sheet sheet = workbook.getSheet(workbook.getSheetName(0));
			Iterator<Row> rows = sheet.iterator();
			rows.next();
			
			DataFormatter fmt = new DataFormatter();
			
			// Get Project for current hr
			String username = jwtUtils.getUserNameFromJwtToken(token);
			Optional<User> hr = userRepository.findByUsername(username);
			UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
			Project project = hrDetails.getProject();
			
			while (rows.hasNext()) {
				Row row = rows.next();
				
				// Create user account with password encoding
				User user = new User();
				user.setUsername(row.getCell(0).getStringCellValue());
				user.setEmail(row.getCell(1).getStringCellValue());
				user.setPassword(passwordEncoder.encode(row.getCell(2).getStringCellValue()));
				user.setRole(Role.ROLE_EMPLOYEE);
				user = userRepository.save(user);
				
				// Create user details with above user and hr project
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
			
			model.addAttribute("jwtToken", token);
			return "redirect:/hr?authorization=Bearer%20"+token;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
	    }
	}
	
	@GetMapping("/project/employees")
	public String employees(HttpServletRequest request, Model model) {
		String headerAuth = request.getParameter("authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();
		
		// Get all employees in this project
		List<UserDetails> employees = userDetailsRepository.findByProject(project);
		
		model.addAttribute("employees", employees);
		model.addAttribute("jwtToken", token);
		return "projectEmployees";
	}
}
