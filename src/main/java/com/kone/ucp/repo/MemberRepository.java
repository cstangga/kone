package com.kone.ucp.repo;

import com.kone.ucp.domain.EmpType;
import com.kone.ucp.domain.Member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	// 1. 기본 목록 조회 (최신순 정렬)
	@Override
	@Query("SELECT m FROM Member m ORDER BY m.createdTime DESC")
	List<Member> findAll();

	// 2. 제출/미제출 필터링 (최신순 정렬)
	@Query("SELECT m FROM Member m WHERE m.submitType = :submitType ORDER BY m.createdTime DESC")
	List<Member> findBySubmitType(@Param("submitType") boolean submitType);

	// 3. 선발구분별 필터링 (최신순 정렬)
	@Query("SELECT m FROM Member m WHERE m.selectionType = :selectionType ORDER BY m.createdTime DESC")
	List<Member> findBySelectionType(@Param("selectionType") String selectionType);

	// 4. 검색 기능 (이름, 전화번호, 희망근무지역으로 검색 + 최신순 정렬)
	@Query("SELECT m FROM Member m WHERE " + "(:name is null or m.name LIKE %:name%) AND "
			+ "(:phoneNumber is null or m.phoneNumber LIKE %:phoneNumber%) AND "
			+ "(:desiredArea is null or m.desiredArea LIKE %:desiredArea%) " + "ORDER BY m.createdTime DESC")
	List<Member> searchMembers(@Param("name") String name, @Param("phoneNumber") String phoneNumber,
			@Param("desiredArea") String desiredArea);

	// 관리자 계약서 리스트 페이지 데이터 가져오기(일반, 지사)
	@EntityGraph(attributePaths = "empType")
	Page<Member> findByEmpTypeIn(List<EmpType> empType, Pageable pageable);

	//이름과 연락처, 희망학교로 검색
	@EntityGraph(attributePaths = "empType")
	Page<Member> findByNameContainingOrPhoneNumberContainsOrDesiredAreaContaining(String name, String phoneNumber,
			String desiredArea, Pageable pageable);
	
	//이름과 연락처로 검색
	@EntityGraph(attributePaths = "empType")
	Page<Member> findByNameContainingOrPhoneNumberContaining(String name, String phoneNumber, Pageable pageable);

	// 관리자 계약서 리스트 페이지 데이터 가져오기(일반, 지사)
	@EntityGraph(attributePaths = "empType")
	Page<Member> findByEmpTypeInAndSelectionType(List<EmpType> empType, String selectionType, Pageable pageable);

	@EntityGraph(attributePaths = "empType")
	Optional<Member> findByName(String name);

	// 20250516 은비 코드 추가
	@EntityGraph(attributePaths = "empType")
	Optional<Member> findByResidentNumber(String residentNumber);

	// 20250515 은비 코드 추가
	Optional<Member> findByNameAndResidentNumber(String name, String residentNumber);
	// 주민등록번호로 중복 조회
	boolean existsByResidentNumber(String residentNumber);


	@Query("select m from Member m where m.selectionType like :select and ("
			+ "m.name like %:name% or m.phoneNumber like %:phoneNumber% or m.desiredArea like %:desiredArea% "
			+ ") order by m.createdTime DESC")
	Page<Member> findBySelAndKey(@Param("select") String select, @Param("name") String name,
			@Param("phoneNumber") String phoneNumber, @Param("desiredArea") String desiredArea, Pageable pageable);
	
	//계약서 등록 여부 확인 - true
	@Query("select m from Member m where m.submitType = TRUE and ("
			+ "m.name like %:name% or m.phoneNumber like %:phoneNumber%"
			+ ") order by m.createdTime DESC")
	Page<Member> findBySelAndTrue(@Param("name") String name,
			@Param("phoneNumber") String phoneNumber, Pageable pageable);

	//계약서 등록 여부 확인 - false
	@Query("select m from Member m where m.submitType = FALSE and ("
			+ "m.name like %:name% or m.phoneNumber like %:phoneNumber%"
			+ ") order by m.createdTime DESC")
	Page<Member> findBySelAndFalse(@Param("name") String name,
			@Param("phoneNumber") String phoneNumber, Pageable pageable);
	
	//계약서 등록 여부 확인 - null
	@Query("select m from Member m where m.submitType IS NULL and m.selectionType != '취소' and ("
			+ "m.name like %:name% or m.phoneNumber like %:phoneNumber%"
			+ ") order by m.createdTime DESC")
	Page<Member> findBySelAndNull(@Param("name") String name,
			@Param("phoneNumber") String phoneNumber, Pageable pageable);
}