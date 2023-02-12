package com.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.entity.Leaves;
import com.application.entity.User;

@Repository
public interface LeavesRepository extends JpaRepository<Leaves, Integer> {
	List<Leaves> findByUser(User user);
	
	List<Leaves> findByUserAndApproved(User user, boolean approved);
	
	List<Leaves> findByApproverAndApproved(User approver, boolean approved);
}
