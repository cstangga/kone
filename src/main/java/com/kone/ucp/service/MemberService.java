package com.kone.ucp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kone.ucp.domain.Member;
import com.kone.ucp.dto.MemberSecurityDto;
import com.kone.ucp.repo.CancelRepository;
import com.kone.ucp.repo.ImageRepo;
import com.kone.ucp.repo.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.kone.ucp.domain.Cancel;
import com.kone.ucp.domain.EmpType;
import com.kone.ucp.domain.Image;

import lombok.AllArgsConstructor;

@Slf4j
@AllArgsConstructor
@Service
public class MemberService implements UserDetailsService {

	private final MemberRepository memberDao;
	private final ImageRepo imgDao;
	private final CancelRepository cancelDao;

	// 20250516 은비 코드 수정
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// MemberRepository의 메서드를 호출해서 username(주민등록번호)이 일치하는 사용자 정보가 있는 지를 리턴. 만약 사용자
		// 정보가 없으면
		// exception을 던짐.

		log.info("loadUserByUsername(username={})", username);

		// 주민등록번호로 사용자 검색
		Optional<Member> opt = memberDao.findByResidentNumber(username);

		if (opt.isPresent()) {
			Member member = opt.get();
			// log.info("Found member: {}, phoneNumber={},submitType={}", member.getName(),
			// member.getPhoneNumber(),member.isSubmitType());
			return MemberSecurityDto.fromEntity(opt.get());
		} else {
			throw new UsernameNotFoundException(username + " 를 찾을 수 없음");
		}

	}
	
	// 20250515 은비 코드 수정
	public Member findByNameAndResidentNumber(String name, String residentNumber) {
		return memberDao.findByNameAndResidentNumber(name, residentNumber).orElse(null);
	}


	/**
	 * Member 정보 저장
	 * 
	 * @param m : Member 타입
	 * @return true/false 반환
	 */
	public boolean insertMember(Member m) {
		boolean result = false;
		if (!memberDao.existsByResidentNumber(m.getResidentNumber())) {
			result = true;
			m.addRole(EmpType.USER);
			memberDao.save(m);
		}
		return result;
	}

	/**
	 * Member 정보 업데이트
	 * 
	 * @param id              : 아이디
	 * @param residentNumber  : 주민번호
	 * @param bankName        : 은행명
	 * @param accountNumber   : 계좌번호
	 * @param accountHolder   : 예금주
	 * @param signature1Image : 서명1
	 * @param signature2Image : 서명2
	 */
	@Transactional
	public void createMember(Long id, String residentNumber, String bankName, String accountNumber,
			String accountHolder, String signature1Image, String signature2Image) {
		log.info("MemberCreate()");

		Member m = memberDao.findById(id).orElse(null);

		m.setResidentNumber(residentNumber);
		m.setBank(bankName);
		m.setAccountNumber(accountNumber);
		m.setAccountHolder(accountHolder);
		m.setCreation_date(LocalDateTime.now());
		m.setSubmitType(true);
		
		Image img = imgDao.findByMember(m);
		
		if(img == null) {
			img = Image.builder().member(m).sign1Img(signature1Image).sign2Img(signature2Image).build();
			imgDao.save(img);			
			return;
		}

		if (signature1Image != null) {
			img.updateSign1Img(signature1Image);
		}
		
		if (signature2Image != null) {
			img.updateSign2Img(signature2Image);
		}

		memberDao.save(m);
	}

	@Transactional
	public void updateMember(Long id, String residentNumber, String bankName, String accountNumber,
			String accountHolder, String signature1Image, String signature2Image) {
		log.info("MemberUpdate()");

		Member m = memberDao.findById(id).orElse(null);

		m.setResidentNumber(residentNumber);
		m.setBank(bankName);
		m.setAccountNumber(accountNumber);
		m.setAccountHolder(accountHolder);
		m.setSubmitType(true);

		Image i = imgDao.findByMember(m);

		if (signature1Image != null) {
			i.updateSign1Img(signature1Image);
		}

		if (signature2Image != null) {
			i.updateSign2Img(signature2Image);
		}

		memberDao.save(m);
	}

	// 선발/미선발 데이터 가져오기
	public Member getMember(Long id) {
		Member m = memberDao.findById(id).orElse(null);

		return m;
	}
	
	/**
	 * 미선발한 현장요원이 지원 취소
	 */
	@Transactional
	public void deleteMember(Long id, String reason) {
		Member m = memberDao.findById(id).orElse(null);
		m.updateSelectionType("취소");
		
		Cancel c = Cancel.builder().member(m).reason(reason).build();
		cancelDao.save(c);
	}

	public boolean existsByResidentNumber(String residentNumber) {
		log.info("memberService / residentNumber = {}",residentNumber);
		return memberDao.existsByResidentNumber(residentNumber);
	}

	public boolean existsByPhoneNumber(String phoneNumber) {
		log.info("memberService / phoneNumber = {}",phoneNumber);
		return memberDao.existsByPhoneNumber(phoneNumber);
	}
}
