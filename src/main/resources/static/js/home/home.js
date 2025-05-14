/**
 * home.js 포함
 * 현장요원이 성공적으로 지원하였으면 팝업창을 띄워줌
 */

document.addEventListener('DOMContentLoaded', function(){
	const urlParams = new URLSearchParams(window.location.search);
	const hasSuccess = urlParams.get('success');
	
	if (hasSuccess !== null) {
		
	    alert("지원이 완료되었습니다.");
	}
})