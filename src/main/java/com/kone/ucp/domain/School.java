package com.kone.ucp.domain;

import java.time.LocalDateTime;

import com.kone.ucp.dto.SchoolDto;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name="SCHOOL")
public class School {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;		//학교 pk
	
	@Basic(optional=false)
	private String area;		//지역
	
	@Basic(optional=false)
	private String name;		//학교 이름

	@Basic(optional=false)
	private String address;		//학교 주소

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_fk")
	private Member member;		// 현장요원

	@Basic(optional=false)
	private String schoolAdminName;		//감독 선생님

	@Basic(optional=false)
	private String schoolAdminPhoneNumber;		//감독 선생님 전화번호
	
	@Basic(optional=false)
	private LocalDateTime testDate;		//시험 날짜

	public SchoolDto toDto() {
		return SchoolDto.builder()
				.schoolId(id)
				.area(area)
				.schoolName(name)
				.schoolAddress(address)
				.agent(member)
				.schoolAdminName(schoolAdminName)
				.schoolAdminPhoneNumber(schoolAdminPhoneNumber)
				.testDate(testDate)
				.build();
	}
}
