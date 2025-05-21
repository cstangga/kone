package com.kone.ucp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//241214 선희 코드 수정:1.아이디(휴대폰번호),비밀번호(이름)으로 수정/2.submitType 변수 타입 boolean -> Boolean으로 변경


@AllArgsConstructor(access = AccessLevel.PRIVATE) // 클래스의 모든 필드를 포함하는 생성자를 생성
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동으로 생성
@Data // @Getter, @Setter, @ToString ,@EqualsAndHashCode,@RequiredArgsConstructor
		// 애너테이션들을 포함.
@Builder // 빌더 패턴을 자동으로 구현됨.
@Entity // 반드시 기본 생성자가 필요하기 때문에 @NoArgsConstructor 필수.
@Table(name = "MEMBER")
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;//유저 pk
	
	@Basic(optional=false)
	private String name;	//이름 -> 아이디 역할

	// 250514 은비 수정
	@Basic(optional=false)
	@Column(nullable = false, unique = true)
	private String residentNumber; // 주민등록번호 -> 비밀번호 역할(암호화 비밀번호)

	private String phoneNumber;		//연락처
	
	@Builder.Default
	@ToString.Exclude  // 서버 -> html(화면) 값은 넘어가지만, [[${member}]] 에는 안보임
	@JsonInclude
	@ElementCollection(fetch = FetchType.EAGER)
	/*
	 * EmployeeType이 Set<EmployeeType>으로 정의되어 있으며, 이는 컬렉션 데이터로 매핑됨. fetch =
	 * FetchType.EAGER -> 엔터티가 로드될 때 관련 컬렉션 데이터도 즉시 로드됨.
	 */
	@Enumerated(EnumType.STRING) // Enum 타입 데이터 매핑(EnumType.STRING -> 문자열로 저장)
	private Set<EmpType> empType = new HashSet<>(); // 직원형태(관리자, 일반, 지사)
	/* Set: 중복을 허용하지 않는 컬렉션 */
	private String address; // 주소

	private String desiredArea; // 근무희망지역

	@Builder.Default
	private String selectionType="미선발"; // 선발구분(미선발, 선발, 취소)
	
	//241214 선희 코드 수정
	private Boolean submitType; // 계약서 제출구분(null[폼 등록 전],false[미제출], true[제출])

	private String accountNumber; // 계좌번호

	private String accountHolder; // 예금주

	private String bank; // 은행명

	private LocalDateTime creation_date; // 계약서 작성 날짜

	private Long pay; // 비용

	public Member addRole(EmpType type) {
		empType.add(type);
		return this;
	}

	public Member clearRoles() {
		empType.clear();
		return this;
	}
	
	 // Getters and Setters
    public Boolean isSubmitType() {
        return submitType;
    }

    public void setSubmitType(Boolean submitType) {
        this.submitType = submitType;
    }
    
    //241215 선희 코드 수정 
    public String getEmpTypeKor() {
        return empType.stream()
        		.map(EmpType :: getKor)
        		.collect(Collectors.joining(", "));// kor 속성 반환
    }
    
    //선발 미선발 취소 상태 변경
    public Member updateSelectionType(String selection) {
    	this.selectionType = selection;
    	return this;
    }

}
