package com.kone.ucp.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.kone.ucp.dto.MemberSecurityDto;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//241214 선희 코드 수정: submitType 변수 타입 boolean -> Boolean으로 변경 

/*
 * Spring Security의 로그인 성공 시
 * Spring Security는 기본적으로 로그인 성공 시 "/"홈 경로로 리다이렉트함.
 * 이 핸들러를 이용하여 로그인 성공 후의 동작을 커스텀마이징할 수 있음.
 */
@Slf4j // log 객체를 자동으로 생성하여 로그 메시지를 출력
@Component // 이 클래스가 자동으로 빈으로 등록됨.

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
	/*
	 * HttpServletRequest request : 현재 로그인 요청의 HTTP 객체(요청에서 쿼리 파라미터(리다이렉트 URL)를 가져오는
	 * 데 사용됨.) HttpServletResponse response : 현재 로그인 요청의 HTTP 응답 객체(로그인 성공 후 응답을
	 * 처리하는 데 사용됨.) Authentication authentication : 인증 객체, 로그인을 성공한 사용자 정보와 권한이 포함됨.
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		log.info("success handle()");

		// 사용자가 가지고 있는 권한 목록 가져오기
		List<String> roleNames = new ArrayList<>();

		authentication.getAuthorities().forEach((auth) -> {
			roleNames.add(auth.getAuthority());
		});

		// Authentication에서 MemberSecurityDto를 가져옴
		MemberSecurityDto member = (MemberSecurityDto) authentication.getPrincipal();
		Boolean submitType = member.isSubmitType(); // 계약서 제출 여부 확인

		// log.info("User Role: {}, Submit Type: {}", roleNames, submitType);

		// 사용자 역할에 따라 리다이렉트
		if (roleNames.contains("ROLE_ADMIN")) {
			log.info("관리자 로그인 성공!");
//            response.sendRedirect("/admin/contract/list"); // 241214 선희 코드 수정 관리자 계약서 리스트 페이지로 리다이렉트
			response.sendRedirect("/admin/apply/list"); // 관리자 페이지로 리다이렉트

		} else {
			// 241224 코드 수정 - 연수
			response.sendRedirect("/member/status");
		}
	}

}
