/**
 * 관리자에서 현장요원 취소함
 */
document.addEventListener('DOMContentLoaded', function(){
	const submitBtn = document.getElementById('submitCancelBtn');
	submitBtn.addEventListener('click', submitForm);
	
	function submitForm() {
		const cancelForm = document.getElementById('cancelForm');
		const reason = document.getElementById('reason');
		const callName = document.getElementById('callname');

		if (!reason.value.trim()) {
			alert('취소 사유를 입력하시오.');
			reason.focus();
			return;
		} 
		
		if(!callName.value.trim()){
			alert('상담사를 작성하세요.');
			callName.focus();
			return;
		}
			cancelForm.submit();
	}
})