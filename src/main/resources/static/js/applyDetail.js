/**
 * view.html 포함
 */
document.addEventListener('DOMContentLoaded', function() {

	function confirmSelection() {
		return confirm('현장요원으로 선발하시겠습니까?');
	}

	//< !--모달 작동 확인을 위한 JavaScript-- >
	document.addEventListener('DOMContentLoaded', function() {
		var myModal = new bootstrap.Modal(document.getElementById('selectionModal'));

		// 선발 버튼 클릭 이벤트 리스너 추가
		document.querySelector('[data-bs-target="#selectionModal"]').addEventListener('click', function() {
			myModal.show();
		});
	});

	//-----------------------------------
	const searchInput = document.getElementById('schoolSearchInput');
	const schoolTableBody = document.getElementById('schoolTableBody');
	// CSRF 토큰을 JavaScript 변수로 선언
	const csrfToken = document.querySelector('meta[name="_csrf"]').content;
	const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
	// 값 확인을 위한 콘솔 출력
	console.log('CSRF Token:', window.csrfToken);
	console.log('CSRF Header:', window.csrfHeader);
	//------------------------------------
	let searchTimeout;

	searchInput.addEventListener('input', function() {
		clearTimeout(searchTimeout);
		searchTimeout = setTimeout(() => {
			const keyword = this.value.trim();
			searchSchools(keyword);
		}, 300); // 300ms 디바운스
	});

	function searchSchools(keyword) {
		const url = `/admin/search?keyword=${encodeURIComponent(keyword)}`;

		fetch(url)
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();  // JSON 형식으로 응답 처리
			})
			.then(schools => {
				// 학교 목록을 테이블에 추가하는 코드
				schoolTableBody.innerHTML = ''; // 테이블 초기화 (기존 데이터 삭제)

				schools.forEach(school => {
					console.log("학교 시간 = ",school.testDate)
					const testDate = new Date(school.testDate);
					const formattedDate = `${testDate.getFullYear()}-${String(testDate.getMonth() + 1).padStart(2, '0')}-${String(testDate.getDate()).padStart(2, '0')} ${String(testDate.getHours()).padStart(2, '0')}:${String(testDate.getMinutes()).padStart(2, '0')}`;

					const row = document.createElement('tr');
					row.innerHTML = `
	        <td>${school.name}</td>
	        <td>${school.area}</td>
	        <td>${formattedDate}</td>
	        <td>
	            <button class="selBtn btn btn-sm btn-primary" data-id="${school.id}">
	                선택
	            </button>
	        </td>
	    `;
					schoolTableBody.appendChild(row);

					// 검색된 학교를 현장요원에게 배치하는 버튼
					const btnSelect = document.querySelectorAll('button.selBtn');

					for (let btn of btnSelect) {
						btn.addEventListener('click', selectSchool);
					}
				});
			})
			.catch(error => {
				console.error('Error:', error);
			});
	}

	// CSRF 토큰 가져오기
	// CSRF 토큰 관련 변수들을 전역 범위에서 선언


	function selectSchool(e) {
		const memberId = document.getElementById('memberName').getAttribute('data-id');
		const schoolId = e.target.getAttribute('data-id');

		if (confirm('이 학교를 선택하시겠습니까?')) {
			const headers = {
				'Content-Type': 'application/json'
			};

			fetch('/admin/select-wordkingday', {
				method: 'POST',
				headers: headers,
				body: JSON.stringify({
					schoolId: schoolId,
					memberId: memberId
				})
			})
				.then(response => {
					if (response.status === 409) {
						alert('이미 선택 및 배정된 학교입니다.');
						return;
					}
					if (!response.ok) {
						throw new Error('네트워크 오류 발생');
					}
					alert('학교 선택이 완료되었습니다.');
					location.reload();
				})
				.catch(error => {
					console.error('Error:', error);
				});
		}
	}


	const delBtn = document.querySelectorAll('button.delBtn');

	for (let btn of delBtn) {
		btn.addEventListener('click', deleteWorkingday);
	}

	function deleteWorkingday(e) {
		const id = e.target.getAttribute('data-id');

		if (!confirm('학교를 삭제하시겠습니까?')) {
			return;
		}
		

		fetch(`/admin/delete-workingday/${id}`)
			.then(response => {
				// sid(schoolId) 를 서버로 보낸다
				location.reload();
			})
			.catch(error => {
				console.error('Error:', error);
			});
	}

	//목록버튼 클릭 시, 이전페이지로 돌아가기
	const ppBtn = document.getElementById('ppBtn');
	ppBtn.addEventListener('click', prevPage);

	function prevPage() {
		location.href="/admin/apply/list";
	}
})