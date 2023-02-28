package com.application.response;

import java.sql.Date;

import com.application.entity.User;

public class ProfileResponse {
	private Integer id;
	
	private Integer employeedId;
	
	private String name;
	
	private Integer contact;
	
	private Integer emergencyContact;
	
	private Date dateOfBirth;
	
	private String address;
	
	private String bloodGroup;
	
	private Date joiningDate;
	
	private User user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmployeedId() {
		return employeedId;
	}

	public void setEmployeedId(Integer employeedId) {
		this.employeedId = employeedId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getContact() {
		return contact;
	}

	public void setContact(Integer contact) {
		this.contact = contact;
	}

	public Integer getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(Integer emergencyContact) {
		this.emergencyContact = emergencyContact;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public Date getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
