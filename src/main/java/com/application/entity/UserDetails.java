package com.application.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer Id;
	private Integer EmployeedId;
	private Date JoiningDate;
	private Integer ProjectId;
	private Date DateOfBirth;
	private Integer ContactNumber;
	private Integer EmergencyContact;
	private String Name;
	private String Address;
	private String Bloodgroup;
	
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		this.Id = id;
	}
	public Integer getEmployeedId() {
		return EmployeedId;
	}
	public void setEmployeedId(Integer employeedId) {
		EmployeedId = employeedId;
	}
	public Date getJoiningDate() {
		return JoiningDate;
	}
	public void setJoiningDate(Date joiningDate) {
		JoiningDate = joiningDate;
	}
	public Integer getProjectId() {
		return ProjectId;
	}
	public void setProjectId(Integer projectId) {
		ProjectId = projectId;
	}
	public Date getDateOfBirth() {
		return DateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		DateOfBirth = dateOfBirth;
	}
	public Integer getContactNumber() {
		return ContactNumber;
	}
	public void setContactNumber(Integer contactNumber) {
		ContactNumber = contactNumber;
	}
	public Integer getEmergencyContact() {
		return EmergencyContact;
	}
	public void setEmergencyContact(Integer emergencyContact) {
		EmergencyContact = emergencyContact;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getBloodgroup() {
		return Bloodgroup;
	}
	public void setBloodgroup(String bloodgroup) {
		Bloodgroup = bloodgroup;
	}
}
