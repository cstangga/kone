/**
 * apply.html 포함
 * 
 */

document.addEventListener('DOMContentLoaded', function() {
	
	/**주소 검색 및 입력창에 표시*/
	let isPop = false;

	const inputAdd = document.getElementById('address');
	inputAdd.addEventListener('click', selectAdd);

	function selectAdd() {		
		if(isPop){
			return;
		}

		isPop = true;
		new daum.Postcode({
			oncomplete: function(data) {
				address.value= data.roadAddress;
			},			
			onclose:function(status){
				isPop =false;
			}
		}).open();
	}


	/** 지원서 제출 */
	const urlParams = new URLSearchParams(window.location.search);
	const hasError = urlParams.get('error');

	if (hasError !== null) {
		alert("이미 지원된 연락처입니다");
	}

	const btnSubmit = document.getElementById('btnSubmit');
	btnSubmit.addEventListener('click', submitForm);

	const form = document.getElementById('applyform');
	let isResidentNumberChecked = false; // 주민번호 중복체크를 했는가 (true=중복체크 했다), (false = 중복체크 안했다)
	let isPhoneNumberChecked = false; // 핸드폰 번호 중복체크를 했는가 (true=중복체크 했다), (false = 중복체크 안했다)

	// 폼 제출
	function submitForm(e) {

		const area = document.getElementById('desiredArea');
		if (area.value === '지역을 선택해주세요.') {
			e.preventDefault();
			alert("지역을 선택하세요");
			return;
		}

		if (isResidentNumberChecked===false) {
			console.log("submitForm / isResidentNumberChecked = "+isResidentNumberChecked);
			alert('주민번호 중복 확인을 완료해주세요.');
			return false;
		}
		if (isPhoneNumberChecked === false) {
			console.log("submitForm / isPhoneNumberChecked = "+isPhoneNumberChecked);
			alert('전화번호 중복 확인을 완료해주세요.11111');
			return false;
		}
		form.submit();
	}

// 주민번호 중복 확인
	const checkResidentNumberDuplicateBtn=document.getElementById('checkResidentNumberDuplicateBtn');
	checkResidentNumberDuplicateBtn.addEventListener('click',checkResidentNumberDuplicate);

	function checkResidentNumberDuplicate() {
		const jumin1 = document.querySelector('[name="jumin1"]').value;
		const jumin2 = document.querySelector('[name="jumin2"]').value;
		if (!jumin1 || !jumin2) {
			alert('주민번호를 입력해주세요.');
			return;
		}
		if (jumin1.length!==6) {
			alert('주민번호 앞자리는 6자리여야 합니다.');
			return;
		}

		if (jumin2.length!==7) {
			alert('주민번호 뒷자리는 7자리여야 합니다.');
			return;
		}

		fetch(`/member/checkRegidentNumberDuplicate?jumin1=${jumin1}&jumin2=${jumin2}`)
			.then(response => response.json())
			.then(data => {
				if (data.duplicate) {
					alert('이미 존재하는 주민번호입니다.');
					jumin1.value = "";
					jumin2.value = "";
					residentNumberCheckBtnStatus(0);
				} else {
					alert('사용 가능한 주민번호입니다.');
					residentNumberCheckBtnStatus(1);
				}
			})
			.catch(() => {
				alert('중복 확인 중 오류가 발생했습니다.');
			});
	}

// 전화번호 중복 확인
	const checkPhoneNumberDuplicateBtn=document.getElementById('checkPhoneNumberDuplicateBtn');
	checkPhoneNumberDuplicateBtn.addEventListener('click',checkPhoneNumberDuplicate);
	function checkPhoneNumberDuplicate() {
		const phoneNumber = document.getElementById('phoneNumber').value;
		if (!phoneNumber) {
			alert('전화번호를 입력해주세요.');
			return;
		}
		if (phoneNumber.length!==11) {
			alert('전화번호는 11자리입니다.')
			return;
		}

		fetch(`/member/checkPhoneNumberDuplicate?phoneNumber=${phoneNumber}`)
			.then(response => response.json())
			.then(data => {
				if (data.duplicate) {
					alert('이미 존재하는 전화번호입니다.');
					phoneNumber.value = "";
					phoneNumberCheckBtnStatus(0);
				} else {
					alert('사용 가능한 전화번호입니다.');
					isPhoneNumberChecked = true;
					phoneNumberCheckBtnStatus(1);
				}
			})
			.catch(() => {
				alert('중복 확인 중 오류가 발생했습니다.');
			});
	}

// 주민번호 입력 변경 시 체크 버튼 자동 리셋
	document.querySelector('[name="jumin1"]').addEventListener('input', function() {
		residentNumberCheckBtnStatus(0);
	});
	document.querySelector('[name="jumin2"]').addEventListener('input', function() {
		residentNumberCheckBtnStatus(0);
	});

	// 전화번호 입력 변경 시 체크 버튼 자동 리셋
	document.getElementById('phoneNumber').addEventListener('input', function() {
		phoneNumberCheckBtnStatus(0);
	});

	// ✅ 버튼 상태 변경 함수 // 0=활성화, 1 = 비활성화
	function phoneNumberCheckBtnStatus(status) {
		console.log("phoneNumberCheckBtnStatus / status = {}",status);


		// 중복 체크를 할 수 있다. = 중복체크를 해야한다 or 중복체크 해야 될 값이 변경됐다
		if(status===0) {

			checkPhoneNumberDuplicateBtn.classList.remove('btn-success', 'checked');
			checkPhoneNumberDuplicateBtn.classList.add('btn-outline-primary');
			checkPhoneNumberDuplicateBtn.innerHTML = '중복 체크';
			checkPhoneNumberDuplicateBtn.disabled = false;
			isPhoneNumberChecked = false;
		}

		// 중복 체크를 할 수 없다 = 중복체크를 통과 했다
		else{
			checkPhoneNumberDuplicateBtn.classList.remove('btn-outline-primary');
			checkPhoneNumberDuplicateBtn.classList.add('btn-success', 'checked');
			checkPhoneNumberDuplicateBtn.innerHTML = '중복 체크 ✅';
			checkPhoneNumberDuplicateBtn.disabled = true;
			isPhoneNumberChecked = true;
		}
	}

	function residentNumberCheckBtnStatus(status) {
		console.log("residentNumberCheckBtnStatus / status = {}",status);

		// 중복 체크를 할 수 있다. = 중복체크를 해야한다 or 중복체크 해야 될 값이 변경됐다
		if(status===0) {

			checkResidentNumberDuplicateBtn.classList.remove('btn-success', 'checked');
			checkResidentNumberDuplicateBtn.classList.add('btn-outline-primary');
			checkResidentNumberDuplicateBtn.innerHTML = '중복 체크';
			checkResidentNumberDuplicateBtn.disabled = false;
			isResidentNumberChecked=false;
		}

		// 중복 체크를 할 수 없다 = 중복체크를 통과 했다
		else{
			checkResidentNumberDuplicateBtn.classList.remove('btn-outline-primary');
			checkResidentNumberDuplicateBtn.classList.add('btn-success', 'checked');
			checkResidentNumberDuplicateBtn.innerHTML = '중복 체크 ✅';
			checkResidentNumberDuplicateBtn.disabled = true;
			isResidentNumberChecked=true;
		}
	}
})

