package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
	Project findByName(String name);
}
