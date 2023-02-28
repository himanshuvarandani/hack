package com.application.response;

import com.application.entity.User;

public class LoginResponse {
	private String accessToken;
	
	private User user;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}