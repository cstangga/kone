package com.kone.ucp.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name="CANCEL")
public class Cancel extends BaseTimeEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;		// 취소 pk
	
	@ToString.Exclude 
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_fk")
	private Member member;		//member fk
	
	@Basic(optional=false)
	private String reason;		//취소 사유
	
	private String callName;	//취소한 상담사 이름
	
}