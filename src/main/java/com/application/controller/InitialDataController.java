package com.application.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;
import com.application.repository.ProjectRepository;
import com.application.repository.UserDetailsRepository;
import com.application.repository.UserRepository;

@Controller
public class InitialDataController {
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@GetMapping("/initialize-data")
	public String initializeData() {
		return "initializeData";
	}
	
	@PostMapping("/initialize-data")
	public String postInitializeData(@RequestParam MultipartFile file) {
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			
			Sheet sheet = workbook.getSheet(workbook.getSheetName(0));
			Iterator<Row> rows = sheet.iterator();
			rows.next();
			
			DataFormatter fmt = new DataFormatter();
			
			while (rows.hasNext()) {
				Row row = rows.next();
				
				User user = new User();
				user.setUsername(row.getCell(0).getStringCellValue());
				user.setEmail(row.getCell(1).getStringCellValue());
				user.setPassword(passwordEncoder.encode(row.getCell(2).getStringCellValue()));
				user.setRole(Role.ROLE_HR);
				user = userRepository.save(user);
				
				Project project = new Project();
				project.setName(row.getCell(11).getStringCellValue());
				project.setUnit(row.getCell(12).getStringCellValue());
				project.setLocation(row.getCell(13).getStringCellValue());
				project.setCustomer(row.getCell(14).getStringCellValue());
				project = projectRepository.save(project);
				
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
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
	    }
	}
}
