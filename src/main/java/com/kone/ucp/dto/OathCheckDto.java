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
public class OathCheckDto {
	
	private String name;
	
	private LocalDateTime creationDate;
	
	private Image img;	//이미지 아이디

}
