package com.kone.ucp.web;

import com.kone.ucp.dto.MemberSecurityDto;
import com.kone.ucp.service.ContractService;
import com.kone.ucp.service.ImageService;
import com.kone.ucp.service.MemberService;
import com.kone.ucp.service.SignatureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/contracts")
@PreAuthorize("hasRole('ADMIN')")
public class ContractController {

	private final ContractService contractSvc;
	private final ImageService imgSvc;
	private final SignatureService signSvc;
	private final MemberService memberSvc;

	// 계약서 폼 보여주기 - 일반 사용자
	@PreAuthorize("hasAnyRole('USER', 'BRANCH')")
	@GetMapping("/create")
	public String createUserContract(@AuthenticationPrincipal MemberSecurityDto msd, Model model) {
		log.info("GET - 사용자 계약서 작성 폼");

		model.addAttribute("info", contractSvc.getMemberInfoCheck(msd.getId()));

		return "/member/contractCreate";
	}

	// 작성된 계약서 폼 저장 - 일반 사용자
	@PreAuthorize("hasAnyRole('USER', 'BRANCH')")
	@PostMapping("/create")
	public String contractCreate(@AuthenticationPrincipal MemberSecurityDto msd,
			@RequestParam("contentMode") String contentMode, @RequestParam("resinum") String residentNumber,
			@RequestParam("bankName") String bankName, @RequestParam("accountNumber") String accountNumber,
			@RequestParam("accountHolder") String accountHolder,
			@RequestParam(name = "accuntfile", required = false) MultipartFile accuntfile,
			@RequestParam(name = "cardfile", required = false) MultipartFile cardfile,
			@RequestParam(name = "signature1Data", required = false) String signature1Data,
			@RequestParam(name = "signature2Data", required = false) String signature2Data) throws IOException {
		

		if (contentMode.equals("create")) {
			imgSvc.saveImg(msd.getId(), accuntfile, cardfile);
			
			String signature1Image = signSvc.saveSignatureImage(signature1Data, "signature1.png");
			String signature2Image = signSvc.saveSignatureImage(signature2Data, "signature2.png");
			memberSvc.createMember(msd.getId(), residentNumber, bankName, accountNumber, accountHolder, signature1Image,
					signature2Image);
		} else {

			if (!accuntfile.isEmpty() && !cardfile.isEmpty()) {
				imgSvc.updateImg(msd.getId(), accuntfile, 0);
				imgSvc.updateImg(msd.getId(), cardfile, 1);
			} else if (!accuntfile.isEmpty() && cardfile.isEmpty()) {
				imgSvc.updateImg(msd.getId(), accuntfile, 0);
			} else if (accuntfile.isEmpty() && !cardfile.isEmpty()) {
				imgSvc.updateImg(msd.getId(), cardfile, 1);
			}

			String signature1Image = null;
			String signature2Image = null;

			// 서명 이미지를 Base64에서 바이너리로 디코딩하여 파일로 저장
			if ((signature1Data != null && !signature1Data.isEmpty())
					&& (signature2Data != null && !signature2Data.isEmpty())) {
				signature1Image = signSvc.updateSignatureImage(msd.getId(), signature1Data,
						msd.getUsername() + "signature1.png", 2);
				signature2Image = signSvc.updateSignatureImage(msd.getId(), signature2Data,
						msd.getUsername() + "signature2.png", 3);
			} else if ((signature1Data != null && !signature1Data.isEmpty())) {
				signature1Image = signSvc.updateSignatureImage(msd.getId(), signature1Data,
						msd.getUsername() + "signature1.png", 2);
			} else if (signature2Data != null && !signature2Data.isEmpty()) {
				signature2Image = signSvc.updateSignatureImage(msd.getId(), signature2Data,
						msd.getUsername() + "signature2.png", 3);
			}
			memberSvc.updateMember(msd.getId(), residentNumber, bankName, accountNumber, accountHolder, signature1Image,
					signature2Image);
		}

		return "redirect:/";
	}

	// 작성된 보안서약서 저장 - 지사 사용자
	@PreAuthorize("hasAnyRole('USER', 'BRANCH')")
	@PostMapping("/create_oath")
	public String oathCreate(@AuthenticationPrincipal MemberSecurityDto msd,
			@RequestParam("signature2Data") String signature2Data,
			@RequestParam("contentMode") String contentMode) throws IOException {
		log.info("POST - 지사 서약서 저장");

		String signature2Image = null;
	
		if (contentMode.equals("create")) {
			signature2Image = signSvc.saveSignatureImage(signature2Data, "signature2.png");
			memberSvc.createMember(msd.getId(), null, null, null, null, null, signature2Image);
		}else {
			signature2Image = signSvc.updateSignatureImage(msd.getId(), signature2Data,
					msd.getPassword() + "signature2.png", 3);
			memberSvc.updateMember(msd.getId(), null, null, null, null, null, signature2Image);

		}

		return "redirect:/";
	}

	/**
	 * 24/12/11 김연수 작성 계약서폼으로 이동
	 */
	@PreAuthorize("hasAnyRole('USER', 'BRANCH')")
	@GetMapping("/contract_info")
	public String contractInfo(@AuthenticationPrincipal MemberSecurityDto msd, Model model) {
		log.info("GET - 계약서 조회 폼");

		model.addAttribute("info", contractSvc.getMemberInfoCheck(msd.getId())
		// getContractCheck(msd.getId())
		);

		return "/member/contractInfo";
	}

}