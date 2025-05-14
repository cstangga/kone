package com.kone.ucp.domain;

import lombok.Getter;

/*Member 테이블에 empType 컬럼을 enum으로 구성
 */

@Getter//클래스의 모든 필드에 대해 자동으로 getter 메서드 생성(getAuthority(), getKor())
public enum EmpType {
	//authority,kor라는 속성 할당
	GUEST("ROLE_GUEST","게스트")
	,USER("ROLE_USER","일반")
	,BRANCH("ROLE_BRANCH","지사")
	,ADMIN("ROLE_ADMIN","관리자");
	
	//필드 선언
	private String authority;//직원 유형의 권한 정보
	private String kor;//권한을 한국어로 표현
	
	//생성자 
	EmpType(String authority, String kor) {
		this.authority = authority;
		this.kor = kor;
	}
	
	public String getAuthority() {
		return this.authority;
	}
	public String getLabel() {
		return this.kor;
	}
	
}
