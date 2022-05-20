$(function () {
    $("[data-toggle='popover']").popover({ trigger: "hover" });
    $("[data-toggle='click']").popover({ trigger: "focus" });
    window.onresize = edit_e;
    if (window.innerWidth >= 768 && $("d-room").text() != "0") edit_m(true);
});
var info_post_able = false;
var act_upc = 0,
    mvd = 0;
var cropper;
function ava_upload() {
    if (act_upc == 4) cropper.destroy();
    act_upc = 4;
    var bufava = document.getElementById("ava-open").files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $(".upc").css("display", "none");
        var tar = document.getElementById("crop-tar");
        tar.src = e.target.result;
        cropper = new Cropper(tar, {
            aspectRatio: 1,
            cropBoxResizable: true,
            cropBoxMovable: true,
            viewMode: 1,
            dragMode: "move",
            scalable: false,
            minCropBoxWidth: 40,
        });

        $("#upc-a").fadeIn("0.3s");
    };
    reader.readAsDataURL(bufava);
}
function ava_post() {
    cropper.getCroppedCanvas().toBlob((blob) => {
        var formData = new FormData();
        formData.append("file", blob);
        $.ajax({
            method: "POST",
            url: url + "/api/setAvatar",
            data: formData,
            processData: false,
            contentType: false,
            success: function (result) {
                result = JSON.parse(result);
                if (result.result === true)
                    window.location.href = url + "/u/" + lgs_r.nickname;
            },
        });
    });
}
function nick_upload() {
    $(".upc").css("display", "none");
    act_upc = 1;
    $("#upc-nick").attr("value", lgs_r.nickname);
    $("#upc-n").fadeIn("0.3s");
}
function gend_upload() {
    $(".upc").css("display", "none");
    act_upc = 2;
    var dg = document.getElementById("d-gender").textContent;
    if (dg === "1") upc_gd(1);
    if (dg === "2") upc_gd(2);
    if (dg === "3") upc_gd(3);
    $("#upc-g").fadeIn("0.3s");
}
function intr_upload() {
    if ($("#d-open").text() === "1") return;
    $(".upc").css("display", "none");
    act_upc = 3;
    $("#upc-i").fadeIn("0.3s");
    info_post_able = true;
}
function info_post() {
    if (!info_post_able || act_upc <= 0) return;
    if (act_upc === 1)
        lgs_r.nickname = document.getElementById("upc-nick").value;
    if (act_upc === 3) {
        if (document.getElementById("upc-intro").value.length == 0)
            lgs_r.intro = null;
        else lgs_r.intro = document.getElementById("upc-intro").value;
    }
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
                upc_errM(false, result.message);
                act_upc = 0;
                return;
            }
            window.location.href = url + "/u/" + lgs_r.nickname;
        },
    });
}
function hide_upc() {
    $(".upc").fadeOut("0.3s");
    info_post_able = false;
    document.getElementById("upc-err-nic").textContent = "";
    act_upc = 0;
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
function upc_gd(i) {
    info_post_able = true;
    if (i == 1) {
        document.getElementById("upc-btn-1").classList.add("upc-btn-acvtive");
        document
            .getElementById("upc-btn-2")
            .classList.remove("upc-btn-acvtive");
        document
            .getElementById("upc-btn-3")
            .classList.remove("upc-btn-acvtive");
        lgs_r.gender = "1";
        document.getElementById("upc-btn-t").textContent = "男性";
    } else if (i == 2) {
        document.getElementById("upc-btn-2").classList.add("upc-btn-acvtive");
        document
            .getElementById("upc-btn-1")
            .classList.remove("upc-btn-acvtive");
        document
            .getElementById("upc-btn-3")
            .classList.remove("upc-btn-acvtive");
        lgs_r.gender = "2";
        document.getElementById("upc-btn-t").textContent = "女性";
    } else if (i == 3) {
        document.getElementById("upc-btn-3").classList.add("upc-btn-acvtive");
        document
            .getElementById("upc-btn-2")
            .classList.remove("upc-btn-acvtive");
        document
            .getElementById("upc-btn-1")
            .classList.remove("upc-btn-acvtive");
        lgs_r.gender = "3";
        document.getElementById("upc-btn-t").textContent = "机性";
    } else info_post_able = false;
}
function toOpen(e) {
    if ($("#d-room").text() === "0") {
        $("#card-room").text("开通直播间");
        $("#card-conf").css("display", "none");
    }
    document.getElementById("card-room").classList.add("card-btn");
    $("#card-room").attr(
        "onclick",
        "window.location.href = url + '/r/' + $('#d-nickname').text()"
    );
}
function edit_e() {
    if (window.innerWidth >= 768 && $("d-room").text() != "0" && mvd == 0)
        edit_m(true);
    if (window.innerWidth < 768 && $("d-room").text() != "0" && mvd == 1)
        edit_m(false);
}
function edit_m(e) {
    if (e) {
        $(".edit-p-m").each(function (index, domEle) {
            $(domEle).css("transform", "translateY(-20px)");
        });
        mvd = 1;
    } else {
        $(".edit-p-m").each(function (index, domEle) {
            $(domEle).css("transform", "unset");
        });
        mvd = 0;
    }
}
