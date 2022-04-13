var url = "http://localhost:7004";
var avatar;
function toIndex() {
    window.location.href = url;
}
function toLogin() {
    window.location.href = url + "/login";
}
function ifg() {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            // if (!result.result) return;
            geneAvatar(result);
        },
    });
}
