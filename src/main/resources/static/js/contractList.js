/**
 * contractList.html에 포함됨.
 */
document.addEventListener('DOMContentLoaded', function() {
	const prevBtn = document.getElementById('prevBtn');
	const nextBtn = document.getElementById('nextBtn');
	const btns = document.querySelectorAll('button.numBtn');

	if (prevBtn && nextBtn && btns) {
		prevBtn.addEventListener('click', prevNext);
		nextBtn.addEventListener('click', prevNext);
		for (b of btns) {
			b.addEventListener('click', prevNext);
		}

	}

	function prevNext(e) {
		const page = e.target.getAttribute('data-id');
		const urlParams = new URLSearchParams(window.location.search);

		const keyword = urlParams.get('keyword');
		const select = urlParams.get('select');

		if (keyword == null && select == null) {
			location.href = `/admin/contract/list?p=${page}`
		} else if (keyword == null && select != null) {
			location.href = `/admin/contract/list?p=${page}&select=${select}`;
		} else if (keyword != null && select == null) {
			location.href = `/admin/contract/list?p=${page}&keyword=${keyword}`
		} else {
			location.href = `/admin/contract/list?p=${page}&keyword=${keyword}&select=${select}`
		}
	}


	const all = document.getElementById('all');
	const yes = document.getElementById('yes');
	const no = document.getElementById('no');
	const not = document.getElementById('not');

	all.addEventListener('click', sortList);
	yes.addEventListener('click', sortList);
	no.addEventListener('click', sortList);
	not.addEventListener('click', sortList);

	function sortList(e) {
		const select = e.target.id;

		const urlParams = new URLSearchParams(window.location.search);
		const keyword = urlParams.get('keyword');
		const page = urlParams.get('p');


		if (keyword == null && page == null) {
			location.href = `/admin/contract/list?select=${select}`
		} else if (keyword != null && page != null) {
			location.href = `/admin/contract/list?keyword=${keyword}&select=${select}`
		} else if (keyword != null && page == null) {
			location.href = `/admin/contract/list?keyword=${keyword}&select=${select}`
		} else {
			location.href = `/admin/contract/list?select=${select}`
		}

	}


	const modal = document.getElementById('submitModal');
	let currentMemberId = null;

	//모달 등록 버튼 클릭 시
	document.querySelectorAll('.modal-submit-btn').forEach((btn) => {
		btn.addEventListener('click', () => {
			//폼을 등록할 유저의 id를 가져옴
			currentMemberId = btn.getAttribute('data-member-id');
			console.log('모달이 열렸습니다. 현재 선택된 Member ID:', currentMemberId);

			//모달 초기화 
			// 모달 초기화 작업 (필요시)
			const inputPay = modal.querySelector('#inputPay');
			if (inputPay) inputPay.value = ''; // 입력 필드 초기화

			//submitType이 false로 정상적이게 된다면 상태는 미제출로 변경하기.
		});
	});

	//비용과 함께 계약서 폼 등록 버튼 클릭 시
	const paySubmitBtn = modal.querySelector('.pay-submit-btn');

	paySubmitBtn.addEventListener('click', () => {
		const inputPay = modal.querySelector('#inputPay').value;

		console.log('비용={}', inputPay);
		//비용 유효성 검사
		if (!currentMemberId || !inputPay) {
			alert('비용을 입력한 후 등록 버튼을 눌러주세요.');
			return;
		}

		// 서버로 데이터 전송 (예: axios 사용)
		axios.post('/admin/contract/submit', {
			id: currentMemberId,
			pay: inputPay,
		})
			.then((response) => {
				alert('등록이 완료되었습니다.');
				location.reload(); // 페이지 새로고침

			})
			.catch((error) => {
				console.error('등록 실패:', error);
				alert('등록 중 오류가 발생했습니다.');
			});

	});
});