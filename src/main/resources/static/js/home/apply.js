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

	function submitForm(e) {
		const input = document.getElementById("phonenumber");
		if (input.value.length !== 11) {
			e.preventDefault();
			alert("입력은 정확히 11글자여야 합니다.");
			return; // 제출을 막음
		}

		const area = document.getElementById('desiredArea');
		if (area.value == '지역을 선택해주세요.') {
			e.preventDefault();
			alert("지역을 선택하세요");
			return;
		}
		form.submit();
	}
})