package com.kone.ucp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kone.ucp.domain.Image;
import com.kone.ucp.domain.Member;

public interface ImageRepo extends JpaRepository<Image, Long>{
	
	Image findByMember(Member m);
	int deleteByMember(Member m);
}
