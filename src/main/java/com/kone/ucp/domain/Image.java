package com.kone.ucp.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name="IMAGE")
public class Image {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;		//이미지 pk
	
	@ToString.Exclude 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_fk")
	private Member member;		//member_fk
	

	private String accoutImg;
	

	private String idCardImg;
	

	private String sign1Img;
	

	private String sign2Img;
    
    
	public Image updateAccoutImg(String accoutImg) {
		this.accoutImg = accoutImg;
		return this;
	}
	
	public Image updateIdCardImg(String idCardImg) {
		this.idCardImg = idCardImg;
		return this;
	}
	
	public Image updateSign1Img(String sign1Img) {
		this.sign1Img = sign1Img;
		return this;
	}
	
	public Image updateSign2Img(String sign2Img) {
		this.sign2Img = sign2Img;
		return this;
	}
	
	public List<String> getAllUrlName(){
		
		List<String> names = new ArrayList<>();
		names.add(this.accoutImg);
		names.add(this.idCardImg);
		names.add(this.sign1Img);
		names.add(this.sign2Img);
		
		return names;
	}
	
}
