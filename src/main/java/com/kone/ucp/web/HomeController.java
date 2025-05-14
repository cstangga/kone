package com.kone.ucp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {
	
	//메인 페이지로 이동
	@GetMapping("/")
	public String home() {
		log.info("home");
		return "index";
	}
}
