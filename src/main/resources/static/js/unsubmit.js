/**
 * 
 */

document.addEventListener('DOMContentLoaded', function() {
	const unsubBtn = document.getElementById('unsubBtn');
	const currentId = document.getElementById('member_id').value;

	unsubBtn.addEventListener('click', unsubmission);

	function unsubmission() {
		// 서버로 데이터 전송 (예: axios 사용)

		axios.get(`/admin/contract/unsub/${currentId}`)
			.then((response) => {
				alert('수정이 완료되었습니다.');
				location.href = "/admin/contract/list";

			})
			.catch((error) => {
				console.error('등록 실패:', error);
				alert('수정 중 오류가 발생했습니다.');
			});

	}
})