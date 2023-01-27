package com.application.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.application.entity.DailyUpdate;
import com.application.entity.User;

public interface DailyUpdatesRepository extends CrudRepository<DailyUpdate, Integer> {
	Optional<DailyUpdate> findByUserAndDate(User user, Date date);
	
	List<DailyUpdate> findByUser(User user);
}
