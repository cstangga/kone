package com.kone.ucp.web;

import java.util.List;
import java.util.Map;

import com.kone.ucp.dto.SchoolDto;
import com.kone.ucp.service.SchoolService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kone.ucp.domain.*;
import com.kone.ucp.dto.ContractUpdateRequest;
import com.kone.ucp.dto.RegisterSubmitDto;
import com.kone.ucp.repo.SchoolRepository;
import com.kone.ucp.service.ContractService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final SchoolRepository schoolRepo;
	private final SchoolService schoolSvc;
	private final ContractService contractSvc;

	// admim/contractList.html -> 비용과 함께 계약서 폼 등록을 허용함.
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/contract/submit")
	public ResponseEntity<?> registerContract(@RequestBody RegisterSubmitDto dto) {
		log.info("@@@@ registerContract 호출 id={},pay={}", dto.getId(), dto.getPay());

		contractSvc.registerContract(dto.getId(), dto.getPay());

		return ResponseEntity.ok("등록 성공");
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/contract/update")
	public ResponseEntity<?> updateContract(@RequestBody ContractUpdateRequest msd) {
		log.info("POST -  관리자의 계약서 수정 id={}", msd);

		contractSvc
				.setContract(Member.builder().id(msd.getId()).pay(msd.getPay()).residentNumber(msd.getResidentNumber())
						.accountHolder(msd.getAccountHolder()).accountNumber(msd.getAccountNumber()).build());

		return ResponseEntity.ok("수정 완료");
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/contract/unsub/{currentId}")
	public ResponseEntity<?> unsubContract(@PathVariable("currentId")Long id) {
		log.info("POST -  관리자의 계약서 미제출로 수정");

		contractSvc.setUnsubmit(id);

		return ResponseEntity.ok("수정 완료");
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/search")
	public ResponseEntity<List<School>> searchSchools(@RequestParam(value = "keyword", required = false) String keyword) {
		log.info("GET - searchSchools / keyword = {}", keyword);
		List<School> schools;

		if (keyword != null && !keyword.trim().isEmpty()) {
			schools = schoolRepo.findByNameContainingWithMember(keyword);
		} else {
			schools = schoolRepo.findAllWithMember();
		}

		return ResponseEntity.ok(schools);
	}


	
	// admim/contractList.html ->선발된 근로자의 계약서 상태 리스트 전체를 불러옴.
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/contract/list")
	public String adminContractList(@RequestParam(name = "p", defaultValue = "0") int pageNo,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "select", defaultValue = "all") String select, Model model) {
		log.info("@@@ adminContractList(pageNo={}, keyword={})", pageNo, keyword);

		Page<Member> page = contractSvc.getAllContractList(pageNo, keyword, select, Sort.by("id").descending());
		// 페이지 데이터를 모델에 추가
		model.addAttribute("membersPage", page);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("keyword", keyword);
		log.info("page = {}", page.toList());

		return "/admin/contractList";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/apply/list")
	public String listContracts(@RequestParam(name = "p", defaultValue = "0") int pageNo,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "select", defaultValue = "all") String select, Model model) {
		log.info("GET - APPLY LIST (NO = {}, KEYWORD = {}, SELECT = {})", pageNo, keyword, select);

		Page<Member> page = contractSvc.getAllApplyList(pageNo, keyword, select, Sort.by("id").descending());

		model.addAttribute("contracts", page);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("keyword", keyword);

		return "/admin/applyList";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/apply/{id:\\d+}")
	public String viewContract(@PathVariable("id") Long id, Model model) { // 변수 이름을 명시적으로 지정
		log.info("GET - viewContract");
		model.addAttribute("contract", contractSvc.getContract(id));
		model.addAttribute("school", contractSvc.getWorkingSchool(id));
		log.info("school = {} ", contractSvc.getWorkingSchool(id));
		model.addAttribute("cancel", contractSvc.getCancel(id));
		
		return "/admin/applyDetail";
	}

	// 24/12/16 민도헌 작성 직원형태 변경 메서드
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/selection")
	public String updateSelection(@RequestParam("id") Long id, @RequestParam("empType") String empType,
			RedirectAttributes redirectAttributes) {

		try {
			log.info("Selection request received - ID: {}, EmpType: {}", id, empType);
			contractSvc.updateMemberType(id, empType);
			redirectAttributes.addFlashAttribute("message", "선발이 완료되었습니다.");

			// 상세 페이지로 리다이렉트
			return "redirect:/admin/apply/list";

		} catch (Exception e) {
			log.error("Selection process failed: ", e);
			redirectAttributes.addFlashAttribute("error", "선발 처리 중 오류가 발생했습니다.");
			return "redirect:/admin/apply/list"; // 오류 시 목록 페이지로
		}
	}

	// 근무 학교 지정
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/select-wordkingday")
	public ResponseEntity<?> selectSchool(@RequestBody Map<String, Long> request) {
		log.info("POST - 학교 선택 요청");
		Long schoolId = request.get("schoolId");
		Long memberId = request.get("memberId");

		try {
			contractSvc.addWorkingDay(schoolId, memberId);
			return ResponseEntity.ok("학교 선택 완료");
		} catch (IllegalStateException e) {
			log.warn("중복 선택된 학교입니다. {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("잘못된 요청입니다: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 근무 학교 삭제
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/delete-workingday/{id}")
	public ResponseEntity<?> deleteScgool(@PathVariable("id") Long id) {
		log.info("GET - 학교 삭제, schoolId = {}", id);

		contractSvc.deleteWorkingDay(id);

		return ResponseEntity.ok(200);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/contract/{id}")
	public String getUserDetail(@PathVariable("id") Long id, Model model) {
		log.info("GET - 유저 계약서 상세 정보, id : {}", id);

		model.addAttribute("info", contractSvc.getMemberInfoCheck(id));

		return "/admin/contractDetail";
	}

	/**
	 * 현장요원 취소 처리
	 */
	@PostMapping("/cancel")
	public String removeMemberCancel(@RequestParam("id")Long id, @RequestParam("reason")String reason, @RequestParam("callName")String callName) {
		log.info("POST - 관리자 현장요원 취소");

		contractSvc.deleteMember(id, reason, callName);

		return "redirect:/admin/apply/list";
	}

	@GetMapping("/school/list")
	@PreAuthorize("hasRole('ADMIN')")
	public String schoolList(@RequestParam(value = "area", required = false) String area,
							 @RequestParam(value = "keyword", required = false) String keyword,
							 Model model) {
		List<SchoolDto> schoolList;

		if (keyword != null && !keyword.isEmpty()) {
			schoolList = schoolSvc.searchByKeyword(keyword);
		} else if (area != null && !area.isEmpty() && !area.equals("전체")) {
			schoolList = schoolSvc.findByArea(area);
		} else {
			schoolList = schoolSvc.findAllWithMember();
		}
		log.info("schoolList = {}",schoolList);
		model.addAttribute("schoolList", schoolList);
		model.addAttribute("selectedArea", area);
		model.addAttribute("searchedKeyword", keyword);
		return "/admin/schoolList";
	}
	// 최창욱 /  학교 리스트 화면 호출

	// 최창욱 /  학교 등록 폼 화면 호출
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/schoolRegisterForm")
	public void schoolRegisterForm(){
		log.info("GET - 학교 리스트");
	}

	@PostMapping("/schoolRegister")
	public String schoolRegister(@ModelAttribute SchoolDto schoolDto){
		log.info("POST - 학교 등록");
		log.info("schoolDto = {} ",schoolDto);
		schoolSvc.save(schoolDto);
		return "redirect:/admin/school/list";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/schoolDetail/{id}")
	public String schoolDetail(@PathVariable("id") Long id, Model model) {
		log.info("GET - 학교 자세히 / id = {}", id);
		SchoolDto schoolDto = schoolSvc.findById(id);
		log.info("schoolDto = {}", schoolDto);
		model.addAttribute("schoolDto", schoolDto);
		return "admin/schoolDetail";
	}

	@PostMapping("/schoolDelete/{id}")
	public String schoolDelete(@PathVariable("id") Long id){
		log.info("POST - 학교 삭제");
		schoolSvc.delete(id);
		return "/admin/school/list";
	}

	@PostMapping("/schoolUpdate")
	public String schoolUpdate(@ModelAttribute SchoolDto schoolDto){
		log.info("POST - 학교 정보 업데이트");
		log.info("schoolDto = {}",schoolDto);
		schoolSvc.update(schoolDto);
		return "redirect:/admin/school/List";
	}

}
