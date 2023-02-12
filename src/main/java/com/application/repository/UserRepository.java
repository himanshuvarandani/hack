package com.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.entity.Role;
import com.application.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);
	
	Optional<User> findByUsernameAndEmail(String username, String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
	
	List<User> findByRole(Role role);
}
