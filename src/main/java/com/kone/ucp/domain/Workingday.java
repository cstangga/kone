package com.kone.ucp.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 클래스의 모든 필드를 포함하는 생성자를 생성
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동으로 생성
@Table(name="WORKINGDAY")
public class Workingday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;		//근무일 pk
	
	@ToString.Exclude 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_fk")
	private Member member;		//member fk
	
	@ToString.Exclude 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="shcool_fk")
	private School school;		//school fk
	
}
