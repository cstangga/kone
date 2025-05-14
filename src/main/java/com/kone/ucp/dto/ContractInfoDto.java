package com.kone.ucp.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.kone.ucp.domain.EmpType;
import com.kone.ucp.domain.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractInfoDto {
	private String eType;
	
	private Long id; // 아이디
	private String name; // 이름
	private String phoneNumber; // 휴대폰번호
	private LocalDateTime creationDate; // 계약체결일
	private Long pay; // 금액
	private String residentNumber; // 주민번호
	private String address; // 주소
	
	private String accountNumber; // 계좌번호
	private String accountHolder; // 예금주
	private String bank; // 은행명
	
	private List<LocalDateTime> workingDays; // 근무일
	private Image img;
}
