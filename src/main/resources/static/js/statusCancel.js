/**
 * 미선발인 현장요원이 취소 동작
 */

document.addEventListener('DOMContentLoaded', function() {
	const submitBtn = document.getElementById('submitBtn');

	submitBtn.addEventListener('click', submitForm);

	function submitForm() {
		const cancelForm = document.getElementById('cancelForm');
		const reason = document.getElementById('reason');

		if (!reason.value.trim()) {
			alert('취소 사유를 입력하시오.');
			reason.focus();
		} else {
			cancelForm.submit();
		}
	}
})