package com.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.application.entity.Project;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.entity.UserDetails;

public interface UserDetailsRepository extends CrudRepository<UserDetails, Integer> {
	UserDetails findByUser(User user);
	
	List<UserDetails> findByProject(Project project);
	
	@Query("Select ud from UserDetails ud where ud.project=?1 and ud.user in (Select u from com.application.entity.User u where u.role=?2)")
	List<UserDetails> findByProjectAndRole(Project project, Role role);
}
