package com.application.request;

import java.sql.Date;

public class ProfileRequest {
	private Integer contact;
	
	private Integer emergencyContact;
	
	private Date dateOfBirth;
	
	private String bloodGroup;
	
	private String address;
	
	private Date joiningDate;

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

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}
}
