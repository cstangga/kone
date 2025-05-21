document.addEventListener("DOMContentLoaded", function () {
    const areaSelect = document.getElementById("areaSelect");
    const searchBtn = document.getElementById("searchSchoolBtn");
    const searchInput = document.getElementById("schoolSearchKeyword");
    const resetBtn = document.getElementById("resetBtn");

    areaSelect.addEventListener("change", function () {
        const selectedArea = areaSelect.value;
        const keyword = searchInput.value.trim();
        let url = "/admin/school/list";
        const params = [];

        if (selectedArea && selectedArea !== "전체") {
            params.push(`area=${encodeURIComponent(selectedArea)}`);
        }
        if (keyword) {
            params.push(`keyword=${encodeURIComponent(keyword)}`);
        }

        if (params.length > 0) {
            url += "?" + params.join("&");
        }

        window.location.href = url;
    });

    searchBtn.addEventListener("click", function () {
        areaSelect.dispatchEvent(new Event("change")); // ✅ area까지 포함해서 검색되게
    });

    searchInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter") searchBtn.click();
    });

    // ✅ 초기화 버튼 클릭 시 전체 목록으로 리셋
    resetBtn.addEventListener("click", function () {
        window.location.href = "/admin/school/list";
    });

});