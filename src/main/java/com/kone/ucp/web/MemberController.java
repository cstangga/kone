package com.kone.ucp.web;

import com.kone.ucp.dto.MemberRequestDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kone.ucp.domain.EmpType;
import com.kone.ucp.domain.Member;
import com.kone.ucp.dto.MemberSecurityDto;
import com.kone.ucp.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

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

	// 20250516 은비 코드 수정
	@PostMapping("/login")
	public String loginProcess(@RequestParam("username") String name,
							   @RequestParam("jumin1") String jumin1,
							   @RequestParam("jumin2") String jumin2) {

		log.info("Post-login() 호출");
		log.info("입력된 이름: {}", name);
		log.info("입력된 주민등록번호 앞자리: {}", jumin1);
		log.info("입력된 주민등록번호 뒷자리: {}", jumin2);

		String residentNumber = jumin1 + "-" + jumin2;

		Member member = memberSvc.findByNameAndResidentNumber(name, residentNumber);

		if (member != null) {
			log.info("로그인 성공: {}", member.getName());

			if (member.getEmpType().contains(EmpType.ADMIN)) {
				return "redirect:/adminHome";
			} else if (member.getEmpType().contains(EmpType.USER)) {
				return member.isSubmitType() ? "redirect:/contract" : "member/login";
			}
		}

		log.info("로그인 실패: 이름 또는 주민등록번호 불일치");
		return "redirect:/member/login?error=true";
	}

	// 20250515 은비 코드 수정
	// 회원가입 및 현장요원 지원 폼으로 이동
	@GetMapping("/apply")
	public String getApply(Model model) {
		log.info("GET : 현장요원 지원 폼");
		model.addAttribute("memberRequestDTO", new MemberRequestDTO());
		return "member/apply";
	}

	// 20250515 은비 코드 수정
	// 회원가입 및 현장요원 정보 저장
	@PostMapping("/apply")
	public String postApply(@ModelAttribute MemberRequestDTO dto) {

		// 주민등록번호 앞6자리 + 뒤7자리 조합
		String fullJumin = dto.getJumin1() + "-" + dto.getJumin2();

		Member m = Member.builder()
				.name(dto.getName())
				.phoneNumber(dto.getPhoneNumber())
				.desiredArea(dto.getDesiredArea())
				.address(dto.getAddress())
				.residentNumber(fullJumin)  // 조합된 주민번호 사용
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

	// 주민번호 중복 체크
	@ResponseBody
	@GetMapping("/checkRegidentNumberDuplicate")
	public Map<String, Boolean> checkResidentNumberDuplicate(@RequestParam String jumin1,
															 @RequestParam String jumin2) {
		log.info("GET - jumin1 = {}, jumin2= {}",jumin1,jumin2);
		String fullResidentNumber = jumin1 +"-"+ jumin2;
		boolean exists = memberSvc.existsByResidentNumber(fullResidentNumber);

		Map<String, Boolean> response = new HashMap<>();
		response.put("duplicate", exists);
		return response;
	}

	// 전화번호 중복 체크
	@ResponseBody
	@GetMapping("/checkPhoneNumberDuplicate")
	public Map<String, Boolean> checkPhoneNumberDuplicate(@RequestParam String phoneNumber) {
		log.info("GET = phoneNumber = {}",phoneNumber);

		boolean exists = memberSvc.existsByPhoneNumber(phoneNumber);

		Map<String, Boolean> response = new HashMap<>();
		response.put("duplicate", exists);
		return response;
	}

}
