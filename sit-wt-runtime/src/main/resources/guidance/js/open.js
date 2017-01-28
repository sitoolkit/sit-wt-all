var baseUrl = window.location.search.substring(1);

if (baseUrl) {
    var p = document.getElementById("baseUrlNav");
    p.innerHTML = "<a href='" + baseUrl + "'>" + baseUrl + "</a>を開く";
}
