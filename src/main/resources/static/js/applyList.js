/**
 * applyList.html 포함 
 * 검색 관련하여 데이터를 가져옴
 */

document.addEventListener('DOMContentLoaded', function() {

	const prevBtn = document.getElementById('prevBtn');
	const nextBtn = document.getElementById('nextBtn');
	const btns = document.querySelectorAll('button.numBtn');

	if(prevBtn&&nextBtn&&btns){
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

		if (keyword == null && select==null) {
			location.href = `/admin/apply/list?p=${page}`
		} else if(keyword == null && select != null){
			location.href= `/admin/apply/list?p=${page}&select=${select}`;
		}else if(keyword!=null && select == null){
			location.href = `/admin/apply/list?p=${page}&keyword=${keyword}`
		}else {
			location.href = `/admin/apply/list?p=${page}&keyword=${keyword}&select=${select}`
		}
	}


	const all = document.getElementById('all');
	const sele = document.getElementById('sele');
	const unse = document.getElementById('unse');
	const canc = document.getElementById('canc');

	all.addEventListener('click', sortList);
	sele.addEventListener('click', sortList);
	unse.addEventListener('click', sortList);
	canc.addEventListener('click', sortList);

	function sortList(e) {
		const select = e.target.id;
		
		const urlParams = new URLSearchParams(window.location.search);
		const keyword = urlParams.get('keyword');
		const page = urlParams.get('p');


		if (keyword == null && page == null) {
			location.href = `/admin/apply/list?select=${select}`
		} else if (keyword != null && page != null) {
			location.href = `/admin/apply/list?keyword=${keyword}&select=${select}`
		} else if (keyword != null && page == null) {
			location.href = `/admin/apply/list?keyword=${keyword}&select=${select}`
		} else {
			location.href = `/admin/apply/list?select=${select}`
		}

	}
})