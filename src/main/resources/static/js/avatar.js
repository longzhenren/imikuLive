var url = "http://localhost:7004";
var avatar;
function geneAvatar(r) {
    if (r.avatar === "auto") {
        var data = new Identicon(
            CryptoJS.SHA256(r.uid.toString() + r.nickname.toString()).toString(
                CryptoJS.enc.Hex
            ),
            330
        ).toString();
        avatar = "data:image/png;base64," + data;
        return;
    }
    avatar = url + "/files/avatars/" + r.avatar;
}
function geneHead() {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            result = {
                result: true,
                avatar: "233.jpg",
                uid: "114514",
                email: "114@514.191",
                nickname: "Operacon",
            };
            if (!result.result) return;
            geneAvatar(result);
            $(".ava").attr("src", avatar);
            $("#logged-sw-t").text(result.nickname);
        },
    });
}
window.addEventListener("wheel", function (e) {
    var item = document.getElementById("logged-sw-t");
    if (e.deltaY > 0) item.scrollLeft += 5;
    else item.scrollLeft -= 5;
});
function toIndex() {
    window.location.href = url;
}
function toLogin() {
    window.location.href = url + "/login";
}
