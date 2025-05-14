package com.kone.ucp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kone.ucp.domain.School;

public interface SchoolRepository extends JpaRepository<School, Long> {
	List<School> findByNameContaining(String name);
}
