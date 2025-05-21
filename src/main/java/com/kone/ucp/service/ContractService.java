package com.kone.ucp.service;

import com.kone.ucp.domain.*;
import com.kone.ucp.dto.*;
import com.kone.ucp.repo.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {

	private final MemberRepository memberRepository;
	private final WorkingRepo workingRepository;
	private final ImageService imageService;
	private final SchoolRepository schoolRepository;
	private final CancelRepository cancelRepository;


	// 현장요원 지원 리스트 가져옴
	public Page<Member> getAllApplyList(int pageNo, String keyword, String select, Sort sort) {
		log.info("p = {}, key = {}, select = {}", pageNo, keyword, select);

		Pageable pageable = PageRequest.of(pageNo, 10, sort);
		// 일반, 지사 사용자 둘다 가져와야 됨
		List<EmpType> empTypes = List.of(EmpType.USER, EmpType.BRANCH);

		if (keyword.equals("") && select.equals("all")) {
			return memberRepository.findByEmpTypeIn(empTypes, pageable);
		} else if (!keyword.equals("") && select.equals("all")) {
			return memberRepository.findByNameContainingOrPhoneNumberContainsOrDesiredAreaContaining(keyword, keyword, keyword,
					pageable);
		} else {
			switch (select) {
			case "sele":
				select = "선발";
				break;
			case "unse":
				select = "미선발";
				break;
			case "canc":
				select = "취소";
				break;
			}

			return memberRepository.findBySelAndKey(select, keyword, keyword, keyword, pageable);
		}

	}

	// 선발된 유저들의 전체 계약서 리스트 조회
	public Page<Member> getAllContractList(int pageNo, String keyword, String select, Sort sort) {
		log.info("@@@ findUserContractList(pageNo={},sort={})", pageNo, sort);
		Pageable pageable = PageRequest.of(pageNo, 10, sort);

		// 일반, 지사 사용자 둘다 가져와야 됨
		List<EmpType> empTypes = List.of(EmpType.USER, EmpType.BRANCH);

		if (keyword.equals("") && select.equals("all")) {
			return memberRepository.findByEmpTypeInAndSelectionType(empTypes, "선발", pageable);
		} else if (!keyword.equals("") && select.equals("all")) {
			return memberRepository.findByNameContainingOrPhoneNumberContaining(keyword, keyword, pageable);
		} else {
			Page<Member> m = null;
			switch (select) {
			case "yes":
				m = memberRepository.findBySelAndTrue(keyword, keyword, pageable);
				break;
			case "no":
				m = memberRepository.findBySelAndFalse(keyword, keyword, pageable);
				break;
			case "not":
				m = memberRepository.findBySelAndNull(keyword, keyword, pageable);
				break;
			}
			log.info("m = {}",m);
			return m;
		}
	}

	public ContractDTO getContract(Long id) {
		return memberRepository.findById(id).map(this::convertToDTO).orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
	}

	public Cancel getCancel(Long id) {
		Member m = memberRepository.findById(id).orElse(null);

		Cancel c = cancelRepository.findByMember(m);
		return c;
	}

	public List<ContractDTO> getContractsBySubmitType(boolean submitType) {
		return memberRepository.findBySubmitType(submitType).stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private ContractDTO convertToDTO(Member member) {

		// 근무일 정보 조회
		List<LocalDateTime> workingDays = workingRepository.findByMemberId(member.getId()).stream()
				.map(workingday -> workingday.getSchool().getTestDate()).collect(Collectors.toList());

		ContractDTO dto = new ContractDTO();
		dto.setWorkingDays(workingDays); // 근무일 리스트 설정

		return ContractDTO.builder().id(member.getId()).name(member.getName()).phoneNumber(member.getPhoneNumber())
				.empType(member.getEmpType()).address(member.getAddress()).desiredArea(member.getDesiredArea())
				.selectionType(member.getSelectionType()).submitType(member.isSubmitType())
				.residentNumber(member.getResidentNumber()).accountNumber(member.getAccountNumber())
				.accountHolder(member.getAccountHolder()).bank(member.getBank()).creationDate(member.getCreation_date())
				.pay(member.getPay()).createdTime(member.getCreatedTime()).workingDays(workingDays).build();

	}

	// 검색 메서드
	public List<ContractDTO> searchContracts(String name, String phoneNumber, String desiredArea) {
		return memberRepository.searchMembers(name, phoneNumber, desiredArea).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public void updateToSelected(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 ID의 현장요원을 찾을 수 없습니다."));
		member.setSelectionType("선발");
		memberRepository.save(member);
	}

	// selectionType별 필터링 메서드 수정
	public List<ContractDTO> getContractsBySelectionType(String selectionType) {
		return memberRepository.findBySelectionType(selectionType).stream()
				.filter(member -> !member.getEmpType().contains(EmpType.ADMIN)) // ADMIN 제외
				.map(this::convertToDTO).collect(Collectors.toList());
	}

	/**
	 * 24/12/11 김연수 작성 계약서 폼 데이터 가져오는 서비스 구현
	 */
	public ContractCheckDTO getContractCheck(Long id) {
		Member m = memberRepository.findById(id).orElse(null);

		// 근무일 정보 조회
		List<LocalDateTime> workingDays = workingRepository.findByMemberId(m.getId()).stream()
				.map(workingday -> workingday.getSchool().getTestDate()).collect(Collectors.toList());

		// 이미지 가져오기
		Image i = imageService.getAllImg(m);

		return ContractCheckDTO.builder().id(m.getId()).name(m.getName()).phoneNumber(m.getPhoneNumber())
				.creationDate(m.getCreation_date()).pay(m.getPay()).workingDays(workingDays)
				.residentNumber(m.getResidentNumber()).address(m.getAddress()).img(i).build();
	}

	public ContractCheckDTO getMemberInfo(Long id) {
		Member m = memberRepository.findById(id).orElse(null);

		// 근무일 정보 조회
		List<LocalDateTime> workingDays = workingRepository.findByMemberId(m.getId()).stream()
				.map(workingday -> workingday.getSchool().getTestDate()).collect(Collectors.toList());

		return ContractCheckDTO.builder().id(m.getId()).name(m.getName()).phoneNumber(m.getPhoneNumber())
				.pay(m.getPay()).workingDays(workingDays).address(m.getAddress()).eType(m.getEmpTypeKor()).build();
	}

	// aaa
	public ContractInfoDto getMemberInfoCheck(Long id) {
		Member m = memberRepository.findById(id).orElse(null);

		// 근무일 정보 조회
		List<LocalDateTime> workingDays = workingRepository.findByMemberId(m.getId()).stream()
				.map(workingday -> workingday.getSchool().getTestDate()).collect(Collectors.toList());

		// 이미지 전체 검색
		Image i = imageService.getAllImg(m);

		return ContractInfoDto.builder().eType(m.getEmpTypeKor()).id(id).name(m.getName())
				.phoneNumber(m.getPhoneNumber()).creationDate(m.getCreation_date()).pay(m.getPay())
				.residentNumber(m.getResidentNumber()).address(m.getAddress()).accountNumber(m.getAccountNumber())
				.accountHolder(m.getAccountHolder()).bank(m.getBank()).workingDays(workingDays).img(i).build();
	}

	// 24/12/16 민도헌 작성 직원형태 변경 메서드

	@Transactional
	public void updateMemberType(Long id, String empType) {
		try {
			// 1. 멤버 조회
			Member member = memberRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

			log.info("Before update - Member ID: {}, Current EmpType: {}, SelectionType: {}", id, member.getEmpType(),
					member.getSelectionType());

			// 2. 권한 설정
			Set<EmpType> empTypes = new HashSet<>();
			if ("USER".equals(empType)) {
				empTypes.add(EmpType.USER); // 기본 사용자 권한 추가
			} else if ("BRANCH".equals(empType)) {
				empTypes.add(EmpType.BRANCH); // 지사 권한 추가
			}

			// 3. 멤버 정보 업데이트
			member.setEmpType(empTypes);
			member.setSelectionType("선발");

			// 4. 변경사항 저장
			memberRepository.saveAndFlush(member); // 즉시 DB에 반영

			// 5. 저장 후 다시 조회하여 확인
			Member updatedMember = memberRepository.findById(id).orElseThrow();
			log.info("After update - Member ID: {}, New EmpType: {}, SelectionType: {}", id, updatedMember.getEmpType(),
					updatedMember.getSelectionType());

		} catch (Exception e) {
			log.error("Error updating member: {}", e.getMessage());
			e.printStackTrace(); // 상세 에러 로그
			throw e;
		}
	}

	public void addWorkingDay(Long schoolId, Long memberId) {
		log.info("ContractService / AddWorkingDay");

		Member m = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
		School s = schoolRepository.findById(schoolId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학교입니다."));

		// 중복 확인
		boolean schoolAssigned = workingRepository.existsBySchoolId(schoolId);
		if (schoolAssigned) {
			throw new IllegalStateException("이미 다른 요원에게 배정된 학교입니다.");
		}

		// 학교에 요원 배정
		s.setMember(m);
		schoolRepository.save(s);

		// Workingday 저장
		Workingday w = Workingday.builder().member(m).school(s).build();
		workingRepository.save(w);
	}

	public List<Map<String, Object>> getWorkingSchool(Long mid) {
		List<Workingday> workingDays = workingRepository.findWorkingInfoByMemberId(mid);

		List<Map<String, Object>> result = workingDays.stream().map(w -> {
			Map<String, Object> map = new HashMap<>();
			map.put("wid", w.getId());
			map.put("sid", w.getSchool().getId());
			map.put("area", w.getSchool().getArea());
			map.put("schoolName", w.getSchool().getName());
			map.put("workingDate", w.getSchool().getTestDate());
			return map;
		}).collect(Collectors.toList());
		return result;
	}

	@Transactional
	public void deleteWorkingDay(Long id) {
		log.info("delete Service");
		log.info("id = {}",id);
		School school=schoolRepository.findById(id).orElseThrow();
		school.setMember(null);
		schoolRepository.save(school);
		workingRepository.deleteBySchoolId(id);
	}

	@Transactional
	// 계약서 폼 등록 시(submitType-> false / pay 등록)
	public void registerContract(Long id, Long pay) {
		Member member = memberRepository.findById(id).orElseThrow();

		member.setSubmitType(false);
		member.setPay(pay);

		memberRepository.save(member);
	}

	// 관리자 계약서 폼 수정
	@Transactional
	public void setContract(Member m) {
		Member member = memberRepository.findById(m.getId()).orElse(null);
		member.setPay(m.getPay());
		member.setResidentNumber(m.getResidentNumber());
		member.setAccountNumber(m.getAccountNumber());
		member.setAccountHolder(m.getAccountHolder());

		memberRepository.save(member);
	}

	// 제출을 미제출로 수정
	@Transactional
	public void setUnsubmit(Long id) {

		Member m = memberRepository.findById(id).orElse(null);
		m.setSubmitType(false);
		memberRepository.save(m);

	}

	@Transactional
	// 관리자측에서 현장요원 취소 처리
	public void deleteMember(Long id, String reason, String callName) {
		Member m = memberRepository.findById(id).orElse(null);

		// 회원정보 초기화(지원 시의 기본정보 빼고)
		m.updateSelectionType("취소");
		m.setSubmitType(null);
		m.setCreation_date(null);
		m.setPay(null);
		m.setAccountHolder(null);
		m.setAccountNumber(null);
		m.setBank(null);
		m.setResidentNumber(null);

		memberRepository.save(m);

		// 이미지 삭제
		imageService.delAllImg(id);

		Cancel c = Cancel.builder().member(m).reason(reason).callName(callName).build();
		cancelRepository.save(c);
	}

	public List<ExcelRequestDTO> getExcelInfo() {
		List<ExcelRequestDTO> e = new ArrayList<>();
		List<Member> members = memberRepository.findAll();
		
		for (Member m : members) {

			if (m.getEmpTypeKor().equals("일반")
					&& m.getSelectionType().equals("선발") && m.getSubmitType() == true) {

				List<Workingday> workingdays = workingRepository.findByMemberId(m.getId());

				for (Workingday w : workingdays) {

					e.add(ExcelRequestDTO.builder().name(m.getName()).phoneNumber(m.getPhoneNumber()).pay(m.getPay())
							.residentNumber(m.getResidentNumber()).accountHolder(m.getAccountHolder()).bank(m.getBank())
							.accountNumber(m.getAccountNumber()).schoolName(w.getSchool().getName()).oneday(1)
							.area(w.getSchool().getArea()).testDate(w.getSchool().getTestDate()).build());
				}
			}
		}
		return e;
	}
}