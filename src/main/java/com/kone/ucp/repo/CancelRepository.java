package com.kone.ucp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kone.ucp.domain.Cancel;
import com.kone.ucp.domain.Member;

public interface CancelRepository extends JpaRepository<Cancel, Long>{
	Cancel findByMember(Member m);
}
