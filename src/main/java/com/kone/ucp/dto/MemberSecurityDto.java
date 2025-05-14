package com.kone.ucp.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.kone.ucp.domain.Member;

//241214 선희 코드 수정: submitType 변수 타입 boolean -> Boolean으로 변경 
public class MemberSecurityDto implements UserDetails {

	private Long id;	// 241212 연수 코드 수정
    private String username; // 아이디 역할(휴대폰 번호)
    private String password; // 비밀번호 역할(이름)
    private Boolean submitType; // 계약서 제출 여부
    private Collection<? extends GrantedAuthority> authorities;
    
    // 생성자
    public MemberSecurityDto(Long id, String username, String password, Boolean submitType, Collection<? extends GrantedAuthority> authorities) {
    	this.id = id; // 241212 연수 코드 수정
    	this.username = username;// 휴대폰 번호
        this.password = password;// 이름
        this.submitType = submitType;
        this.authorities = authorities;
    }

    // Entity로부터 DTO 생성
    public static MemberSecurityDto fromEntity(Member member) {
        List<SimpleGrantedAuthority> authorities = member.getEmpType().stream()
                .map(empType -> new SimpleGrantedAuthority(empType.getAuthority()))
                .collect(Collectors.toList());

        return new MemberSecurityDto(
        		member.getId(),	// 241212 연수 코드 수정
        		//241214 선희 코드 수정 
        		member.getPhoneNumber(),//전화번호를 username으로 설정
                member.getName(), //이름을 password로 설정
                member.isSubmitType(),
                authorities
        );
    }

    // UserDetails 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠겨있지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화됨
    }
    // 추가적인 Getter
    public Boolean isSubmitType() {
        return submitType;
    }
    
    //241212 연수 코드 수정
    public Long getId() {
    	return id;
    }
 }
