package com.kone.ucp.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 이 클래스는 Spring의 설정 클래스임을 나타냄.
@EnableMethodSecurity
public class SecurityConfig {

	/*
	 * 비밀번호를 암호화된 형태로 저장
	 * 
	 * @Bean : 스피링 IoC가 관리하는 객체
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();// BCryptPasswordEncoder();
	}

	/*
	 * 스프링 시큐리티 필터 체인
	 * 
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());

		http.formLogin(form -> form
			.loginPage("/member/login")
			.defaultSuccessUrl("/")
			.successHandler(new CustomLoginSuccessHandler())
		);

		http.logout(logout -> logout
			.logoutSuccessUrl("/")
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
		);

		return http.build();
	}
}
