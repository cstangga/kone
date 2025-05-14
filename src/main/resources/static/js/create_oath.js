/**
 * create_oath.html 포함
 * 
 */

document.addEventListener('DOMContentLoaded', function() {
    $(document).ready(
        function() {
            $("#signature2").jSignature({
                'background-color': 'white',
				'height': '250px',
				'width': '714px',
                'lineWidth': 12,
            });

			$("#signature2").css({
				'border': '1px solid gray',
				'display': 'flex',
				'justify-content': 'center',
				'align-items': 'center',
				'margin': '25px'

			});
			
            // 서명하기 버튼 클릭 시 모달 표시
            $("#signbtn2").click(function(e) {
                e.preventDefault();
                $("#signModal2").modal('show');
            });

            // 다시 서명 버튼
            $("#reset2").click(function(e) {
                e.preventDefault();
                $("#signature2").jSignature("reset");
            });

            // 서명 저장 버튼
            $("#save2").click(
                function(e) {
                    e.preventDefault();

                    if ($("#signature2").jSignature("getData", "native").length === 0) {
                        alert("서명을 해주세요.");
                        return;
                    }

                    // 서명 데이터를 PNG 형식으로 가져오기
                    let imgData = $("#signature2").jSignature("getData", "image");
                    let imgBase64 = "data:" + imgData[0] + "," + imgData[1];

                    // 이미지 표시 및 버튼 숨기기
                    $("#signatureImg2").attr("src", imgBase64).show();

                    $("#signModal2").modal('hide');
					$("#signature2Data").val($("#signatureImg2").attr("src"));

					$("#signature2").jSignature("reset");
                });

        });


    const submitBtn = document.getElementById('submitBtn');

    // 이벤트 리스너 설정
    submitBtn.addEventListener('click', formSubmit);

    function formSubmit(e) {
        e.preventDefault();

		console.log($("#signatureImg2"));
        if (!$("#signatureImg2").show()) {
            alert("서명을 완료해주세요.");
            return;
        }

        // 서명 이미지 데이터 저장
        $("#signature2Data").val($("#signatureImg2").attr("src"));

        // 폼 제출
        document.getElementById("contractForm").submit();
    }
})