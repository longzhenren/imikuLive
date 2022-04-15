$(function () {
    $("[data-toggle='popover']").popover({ trigger: "hover" });
});
var info_post_able = false;
var act_upc = 0;
function ava_upload() {
    alert(lgs_r.nickname);
}
function nick_upload() {
    act_upc = 1;
    $("#upc-nick").attr("placeholder", lgs_r.nickname);
    $("#upc-n").fadeIn("0.3s");
}
function gend_upload() {}
function intr_upload() {}
function info_post() {
    if (!info_post_able) return;
    if (act_upc === 1)
        lgs_r.nickname = document.getElementById("upc-nick").value;
    $.ajax({
        method: "POST",
        url: url + "/api/updateInfo",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            uid: lgs_r.uid,
            nickname: lgs_r.nickname,
            intro: lgs_r.intro,
            gender: lgs_r.gender,
        }),
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) {
                if (act_upc === 1) upc_errM(false, result.message);
                act_upc = 0;
                return;
            }
            window.location.href = url + "/u/" + lgs_r.nickname;
        },
    });
}
function hide_upc() {
    $(".upc").fadeOut("0.3s");
}
function upc_Ncheck() {
    var nk = document.getElementById("upc-nick").value.toString();
    if (nk.length < 4 || nk.length > 15) {
        upc_errM(false, "昵称长度应为 4 到 15 位");
        if (nk.length === 0)
            $("#upc-nick").css(
                "border-color",
                "#f07d58 #f07d58 #3ba8ab #f07d58"
            );
        return;
    }
    if (nk.includes("/") || nk.includes("?") || nk.includes("\\")) {
        upc_errM(false, "昵称包含非法字符");
        if (nk.length === 0)
            $("#upc-nick").css(
                "border-color",
                "#f07d58 #f07d58 #3ba8ab #f07d58"
            );
        return;
    }
    upc_errM(true, "");
}
function upc_errM(r, m) {
    if (r === true) {
        info_post_able = true;
        document.getElementById("upc-err-nic").textContent = "";
        $("#upc-nick").css("border-color", "#f07d58 #f07d58 #3ba8ab #f07d58");
        return;
    }
    info_post_able = false;
    $("#upc-nick").css("border-color", "#f07d58 #f07d58 #ff0000 #f07d58");
    document.getElementById("upc-err-nic").textContent = m;
}
