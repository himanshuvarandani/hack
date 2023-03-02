package com.application.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.application.response.ProfileResponse;
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
	DailyUpdatesRepository dailyUpdatesRepository;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/edit-project")
	public ResponseEntity<Object> editProject(@RequestBody String location, HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		project.setLocation(location);
		projectRepository.save(project);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/project/add-employees")
	public ResponseEntity<Object> addEmployees(@RequestParam MultipartFile file, HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

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
			return new ResponseEntity<Object>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/project/employees")
	public ResponseEntity<List<ProfileResponse>> employees(HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		// Get all employees in this project
		List<UserDetails> employees = userDetailsRepository.findByProjectAndRole(project, Role.ROLE_EMPLOYEE);

		List<ProfileResponse> employeesList = new ArrayList<ProfileResponse>();
		employees.forEach((e) -> {
			ProfileResponse employee = new ProfileResponse();
			employee.setAddress(e.getAddress());
			employee.setBloodGroup(e.getBloodGroup());
			employee.setContact(e.getContact());
			employee.setDateOfBirth(e.getDateOfBirth());
			employee.setEmergencyContact(e.getEmergencyContact());
			employee.setEmployeedId(e.getEmployeedId());
			employee.setId(e.getId());
			employee.setJoiningDate(e.getJoiningDate());
			employee.setName(e.getName());
			employee.setUser(e.getUser());

			employeesList.add(employee);
		});

		return new ResponseEntity<List<ProfileResponse>>(employeesList, HttpStatus.OK);
	}

	@GetMapping("/project/employee/{employeeId}")
	public ResponseEntity<User> employeeDetails(@PathVariable("employeeId") Integer employeeId,
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		// Fetch employee updates
		Optional<User> employeeRef = userRepository.findById(employeeId);
		if (employeeRef.isEmpty())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		User employee = employeeRef.get();
		UserDetails employeeDetails = userDetailsRepository.findByUser(employee);
		if (employeeDetails.getProject() != project)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<User>(employee, HttpStatus.OK);
	}

	@GetMapping("/project/employee/{employeeId}/daily-updates")
	public ResponseEntity<List<DailyUpdate>> employeeDailyUpdates(@PathVariable("employeeId") Integer employeeId,
			HttpServletRequest request) {
		String headerAuth = (String) request.getHeader("Authorization");
		String token = headerAuth.substring(7, headerAuth.length());

		// Get Project for current hr
		String username = jwtUtils.getUserNameFromJwtToken(token);
		Optional<User> hr = userRepository.findByUsername(username);
		UserDetails hrDetails = userDetailsRepository.findByUser(hr.get());
		Project project = hrDetails.getProject();

		// Fetch employee updates
		Optional<User> employeeRef = userRepository.findById(employeeId);
		if (employeeRef.isEmpty())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		User employee = employeeRef.get();
		UserDetails employeeDetails = userDetailsRepository.findByUser(employee);
		if (employeeDetails.getProject() != project)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		List<DailyUpdate> employeeUpdates = dailyUpdatesRepository.findByUser(employee);

		return new ResponseEntity<List<DailyUpdate>>(employeeUpdates, HttpStatus.OK);
	}
}
