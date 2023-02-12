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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.application.entity.DailyUpdate;
import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.DailyUpdatesRepository;
import com.application.repository.ProjectRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;
import com.application.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpSession;

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
	DailyUpdatesRepository dailyUpdatesRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping(value={"", "/"})
	public String hr(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hrRef = userRepository.findByUsername(username);
		User hr = hrRef.get();
		UserDetails hrDetails = userDetailsRepository.findByUser(hr);
		Project project = hrDetails.getProject();
		
		// Check for todays update
		Optional<DailyUpdate> dailyUpdate = dailyUpdatesRepository.findByUserAndDate(
				hr,
				new Date(new java.util.Date().getTime())
		);
		
		if (dailyUpdate.isEmpty()) {
			model.addAttribute("dailyUpdate", null);
		} else {
			model.addAttribute("dailyUpdate", dailyUpdate.get());
		}
		
		
		// Give all updates
		List<DailyUpdate> dailyUpdates = dailyUpdatesRepository.findByUser(hr);
		
		model.addAttribute("dailyUpdates", dailyUpdates);
		model.addAttribute("role", hr.getRole().name());
		model.addAttribute("project", project);
		model.addAttribute("jwtToken", token);
		return "hr";
	}
	
	@GetMapping("/edit-project")
	public String editProject(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		model.addAttribute("role", hr.get().getRole().name());
		model.addAttribute("project", project);
		model.addAttribute("jwtToken", token);
		return "editProject";
	}

	@PostMapping("/edit-project")
	public String postEditProject(String location, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();
		
		project.setLocation(location);
		projectRepository.save(project);
		
		return "redirect:/hr";
	}
	
	@GetMapping("/project/add-employees")
	public String addEmployees(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get current hr details
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);

		model.addAttribute("role", hr.get().getRole().name());
		model.addAttribute("jwtToken", token);
		return "addEmployees";
	}
	
	@PostMapping("/project/add-employees")
	public String postAddEmployees(@RequestParam MultipartFile file, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());
		
		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		model.addAttribute("role", hr.get().getRole().name());
		model.addAttribute("jwtToken", token);
		
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			
			Sheet sheet = workbook.getSheet(workbook.getSheetName(0));
			Iterator<Row> rows = sheet.iterator();
			rows.next();
			
			DataFormatter fmt = new DataFormatter();
			
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
			return "redirect:/hr";
		} catch (IOException e) {
			model.addAttribute("error", "Fail to parse Excel file: "+e.getMessage());
			return "error";
	    }
	}
	
	@GetMapping("/project/employees")
	public String employees(HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();
		
		// Get all employees in this project
		List<UserDetails> employees = userDetailsRepository.findByProjectAndRole(project, Role.ROLE_EMPLOYEE);
		
		model.addAttribute("role", hr.get().getRole().name());
		model.addAttribute("employees", employees);
		model.addAttribute("jwtToken", token);
		return "projectEmployees";
	}
	
	@SuppressWarnings("deprecation")
	@GetMapping("/project/employee/{employeeId}")
	public String employeeUpdates(@PathVariable("employeeId") Integer employeeId, HttpSession session, Model model) {
		String headerAuth = (String) session.getAttribute("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		
		// Fetch employee updates
		User employee = userRepository.getById(employeeId);
		List<DailyUpdate> employeeUpdates = dailyUpdatesRepository.findByUser(employee);
		
		model.addAttribute("role", hr.get().getRole().name());
		model.addAttribute("employee", employee);
		model.addAttribute("employeeUpdates", employeeUpdates);
		model.addAttribute("jwtToken", token);
		return "employeeUpdates";
	}
}
