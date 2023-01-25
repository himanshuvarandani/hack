package com.application.repository;

import org.springframework.data.repository.CrudRepository;

import com.application.entity.User;
import com.application.entity.UserDetails;

public interface UserDetailsRepository extends CrudRepository<UserDetails, Integer> {
	UserDetails findByUser(User user);
}
