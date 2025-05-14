package com.kone.ucp.dto;

import java.util.List;

import com.kone.ucp.domain.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCheckDTO {
	private String accountNumber; // 계좌번호

	private String accountHolder; // 예금주

	private String bank; // 은행명
	
	private List<Image> img;	//이미지 아이디
}
