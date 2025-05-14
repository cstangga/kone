package com.kone.ucp.dto;

import java.time.LocalDateTime;

import com.kone.ucp.domain.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRequestDTO {

	private String name;	//이름 -> 아이디 역할
	
	private String phoneNumber;		//연락처 -> 비밀번호 역할(암호화 비밀번호)

	private String residentNumber; // 주민번호

	private String accountNumber; // 계좌번호

	private String accountHolder; // 예금주

	private String bank; // 은행명

	private Long pay; // 비용
	
	private int oneday;
	
	private String area;		//지역
	
	private String schoolName;		//학교 이름
	
	private LocalDateTime testDate;		//시험 날짜
}
