package com.application.entity;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Leaves {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Date startDate;
	
	private Date endDate;
	
	private String reason;
	
	private boolean approved = false;

	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="approver_id", referencedColumnName="id")
	private User approver;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean getApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}
}
