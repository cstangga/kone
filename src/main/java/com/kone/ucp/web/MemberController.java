package com.kone.ucp.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kone.ucp.domain.EmpType;
import com.kone.ucp.domain.Member;
import com.kone.ucp.dto.MemberSecurityDto;
import com.kone.ucp.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

	private final MemberService memberSvc;

	@GetMapping("/login")
	public String login() {
		log.info("Get-login() 호출 ");
		return "member/login";

	}

	@PostMapping("/login")
	public String loginProcess(@RequestParam("username") String name, @RequestParam("password") String phoneNumber) {

		log.info("Post-login() 호출 ");
		Member member = memberSvc.findByNameAndPhoneNumber(name, phoneNumber);

		if (member != null) {
			log.info("member name={}, member auth={}",member.getName(),member.getEmpType());
			if (member.getEmpType().contains(EmpType.ADMIN)) {
				log.info("어드민 로그인 성공");
				return "redirect:/adminHome"; // 관리자 페이지로 이동

			} else if (member.getEmpType().contains(EmpType.USER)) {
				if (member.isSubmitType()) {
					return "redirect:/contract"; // 계약서 페이지로 이동
				} else {
					log.info("사용자 로그인이 실패했습니다: SubmitType이 false입니다.");
					return "member/login"; // 경고 메시지와 함께 로그인 페이지로 반환
				}
			}
		}

		log.info("로그인 실패: 사용자 이름 또는 비밀번호 불일치.");
		return "redirect:/member/login?error=true"; // 로그인 페이지에 회원가입 모달 표시
	}

	// 회원가입 및 현장요원 지원 폼으로 이동
	@GetMapping("/apply")
	public void getApply() {
		log.info("GET : 현장요원 지원 폼");
	}

	// 회원가입 및 현장요원 정보 저장
	@PostMapping("/apply")
	public String postApply(@ModelAttribute("name") String name, @ModelAttribute("phonenumber") String phonenumber,
			@ModelAttribute("desiredArea") String desiredArea, @ModelAttribute("address") String address) {

		Member m = Member.builder().name(name).phoneNumber(phonenumber).desiredArea(desiredArea).address(address)
				.build();

		if (!memberSvc.insertMember(m)) {
			return "redirect:/apply?error";
		} else {
			return "redirect:/?success";
		}
	}
	
	@PostMapping("/applyCancel")
	public String cancelApply(@RequestParam("id")Long id, @RequestParam("reason")String reason) {
		log.info("GET - 지원 취소 {}, {}", id, reason);
		
		memberSvc.deleteMember(id, reason);
		
		return "redirect:/";
	}
	
	@GetMapping("/status")
	public void getStatus(@AuthenticationPrincipal MemberSecurityDto msd,Model model) {
		log.info("GET - 상태 창");
		
		Member m = memberSvc.getMember(msd.getId());
		
		model.addAttribute("member", m);
		
	}
}
