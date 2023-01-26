package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.entity.Qualification;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Integer> {
}
