package com.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.application.entity.Meeting;
import com.application.entity.User;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
	@Query("SELECT m FROM Meeting m INNER JOIN m.participants mp WHERE mp=?1")
	List<Meeting> findByParticipant(User participant);
}
