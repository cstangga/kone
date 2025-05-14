package com.kone.ucp.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
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
	private LocalDateTime testDate;		//시험 날짜

}
