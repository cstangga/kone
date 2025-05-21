package com.kone.ucp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kone.ucp.domain.Workingday;

public interface WorkingRepo extends JpaRepository<Workingday, Long> {
    List<Workingday> findByMemberId(Long memberId);
    

	// 멤버 ID로 Workingday와 School 정보를 함께 조회
	@Query("SELECT w FROM Workingday w JOIN FETCH w.school WHERE w.member.id = :memberId")
	List<Workingday> findWorkingInfoByMemberId(@Param("memberId") Long memberId);

	void deleteBySchoolId(long id);

	Workingday findByMemberIdAndSchoolId(long memberId, long schoolId);

	Boolean existsByMemberIdAndSchoolId(long memberId, long schoolId);

	boolean existsBySchoolId(long schoolId);

}