/**
 * detail.html 포함
 * 
 */

document.addEventListener('DOMContentLoaded', function() {

	if (document.getElementById('homeBtn')) {
		const homeBtn = document.getElementById('homeBtn');
		homeBtn.addEventListener('click', addHref);
	}


	const modal = new bootstrap.Modal(document.getElementById('updateModal'));

	//모달 등록 버튼 클릭 시
	const updateBtn = document.querySelector('#updateBtn');
	updateBtn.addEventListener('click', updateForm);

	function updateForm() {
		modal.show();
	}


	//비용과 함께 계약서 폼 등록 버튼 클릭 시
	const submitBtn = document.querySelector('.submit-btn');
	const currentId = document.getElementById('member_id').value;

	submitBtn.addEventListener('click', () => {
		const inputPay = document.querySelector('#pay').value;
		const inputResi = document.querySelector('#reside').value;
		const inputNum = document.querySelector('#accountNum').value;
		const inputHold = document.querySelector('#accountHold').value;



		// 서버로 데이터 전송 (예: axios 사용)
		axios.post('/admin/contract/update', {
			id: currentId,
			pay: inputPay,
			residentNumber: inputResi,
			accountNumber: inputNum,
			accountHolder: inputHold,
		}, {
			headers: {
				'Content-Type': 'application/json'
			}
		})
			.then((response) => {
				alert('수정이 완료되었습니다.');
				location.reload(); // 페이지 새로고침

			})
			.catch((error) => {
				console.error('등록 실패:', error);
				alert('수정 중 오류가 발생했습니다.');
			});

	});
	




	const sections = ['contract_sec', 'account_sec', 'oath_sec'];
	let currentSectionIndex = 0;

	const nextBtn = document.getElementById('nextBtn');
	const prevBtn = document.getElementById('prevBtn');
	updateSections();
	// 이벤트 리스너 설정
	nextBtn.addEventListener('click', nextSection);
	prevBtn.addEventListener('click', prevSection);

	function addHref() {

	    location.href = "/admin/contract/list";
	}

	function updateSections() {

	    // 모든 섹션 숨기기
	    sections.forEach(sectionId => {
	        document.getElementById(sectionId).classList.remove('active-sec');
	        document.getElementById(sectionId).classList.add('hidden-sec');
	    });

	    // 현재 섹션만 보이기
	    document.getElementById(sections[currentSectionIndex]).classList.remove('hidden-sec');
	    document.getElementById(sections[currentSectionIndex]).classList.add('active-sec');

	    // 버튼 상태 업데이트
	    prevBtn.innerHTML = currentSectionIndex === 0 ? '목록' : '◀이전';
	    if (currentSectionIndex === 0) {
	        prevBtn.addEventListener('click', addHref)
	    } else {
	        prevBtn.removeEventListener('click', addHref)
	    }

	    // 마지막 섹션에서는 NEXT 버튼의 텍스트와 동작 변경
	    if (currentSectionIndex === 2) {
	        nextBtn.disabled = true;
	    } else {
	        nextBtn.disabled = false;
	    }
	}

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

})