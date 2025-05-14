/**
 * 계약서 조회, 작성, 수정 시 prev/next/home버튼 동작하게 하는 코드
 */

document.addEventListener('DOMContentLoaded', function() {
	const sections = ['contract_sec', 'account_sec', 'oath_sec'];
	let currentSectionIndex = 0;

	const nextBtn = document.getElementById('nextBtn');
	const prevBtn = document.getElementById('prevBtn');


	updateSections();

	// 이벤트 리스너 설정
	nextBtn.addEventListener('click', nextSection);
	prevBtn.addEventListener('click', prevSection);


	function nextSection() {
		if (currentSectionIndex < sections.length - 1) {
			currentSectionIndex++;
			updateSections();
		}
	}

	function prevSection() {
		if (currentSectionIndex > 0) {
			currentSectionIndex--;
			updateSections();
		}
	}

	function addHref() {
		location.href = "/logout";
	}

	function updateSections() {
		const host = window.location.pathname;

		// 모든 섹션 숨기기
		sections.forEach(sectionId => {
			document.getElementById(sectionId).classList.remove('active-sec');
			document.getElementById(sectionId).classList.add('hidden-sec');
		});

		// 현재 섹션만 보이기
		document.getElementById(sections[currentSectionIndex]).classList.remove('hidden-sec');
		document.getElementById(sections[currentSectionIndex]).classList.add('active-sec');

		// 버튼 상태 업데이트
		prevBtn.innerHTML = currentSectionIndex === 0 ? '로그아웃' : '◀이전';
		if (currentSectionIndex === 0) {
			prevBtn.addEventListener('click', addHref)
		} else {
			prevBtn.removeEventListener('click', addHref)
		}

		// 마지막 섹션에서는 NEXT 버튼의 텍스트와 동작 변경
		if (currentSectionIndex === 2) {

			if (host === "/contracts/create") {
				nextBtn.innerHTML = '제출';
				nextBtn.classList.remove('btn-light')
				nextBtn.classList.add('btn-success')
				nextBtn.addEventListener('click', formSubmit);
			} else {
				nextBtn.innerHTML = '다운로드';
				nextBtn.classList.remove('btn-light')
				nextBtn.classList.add('btn-danger')
				nextBtn.addEventListener('click', downloadPdf);
			}
		} else {
			nextBtn.innerHTML = '다음▶';
			nextBtn.classList.add('btn-light')
			if (host === "/contracts/create") {
				nextBtn.classList.remove('btn-success')
				nextBtn.removeEventListener('click', formSubmit);
			} else {
				nextBtn.classList.remove('btn-danger')
				nextBtn.removeEventListener('click', downloadPdf);
			}
		}
	}


	function formSubmit(e) {
		e.preventDefault();

		//const re

		// 필수 입력값 검증
		if (!$("#residentNumber").val()) {
			alert("주민등록번호를 입력해주세요.");
			return;
		}

		if (!$("#signatureImg1").show() || !$("#signatureImg2").show()) {
			alert("모든 서명을 완료해주세요.");
			return;
		}

		// 계좌 정보 검증
		if (!$("input[name='bankName']").val() ||
			!$("input[name='accountNumber']").val() ||
			!$("input[name='accountHolder']").val()) {
			alert("계좌 정보를 모두 입력해주세요.");
			return;
		}

		// 파일 업로드 검증
		if (
			(!$("#accuntfileName").val() && !$("#cardfileName").val())
			&&
			(!$("#accuntfile").val() || !$("#cardfile").val())
		) {
			alert("통장사본과 신분증 파일을 모두 업로드해주세요.");
			return;
		}

		// 폼 제출
		document.getElementById("contractForm").submit();
	}
	
	function downloadPdf(e){
		location.href="/download/pdf"
	}
})