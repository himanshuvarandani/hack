package com.application.entity;

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
	  private Integer JoiningDate;
	  private Integer ProjectId;
	  private Integer DateOfBirth;
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
	public Integer getJoiningDate() {
		return JoiningDate;
	}
	public void setJoiningDate(Integer joiningDate) {
		JoiningDate = joiningDate;
	}
	public Integer getProjectId() {
		return ProjectId;
	}
	public void setProjectId(Integer projectId) {
		ProjectId = projectId;
	}
	public Integer getDateOfBirth() {
		return DateOfBirth;
	}
	public void setDateOfBirth(Integer dateOfBirth) {
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
