var shutx = 0;
var cropper, nic_name;
function cov_upload() {
    if (act_upc == 4) cropper.destroy();
    act_upc = 4;
    var bufava = document.getElementById("ava-open").files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $(".upc").css("display", "none");
        var tar = document.getElementById("crop-tar");
        tar.src = e.target.result;
        cropper = new Cropper(tar, {
            aspectRatio: 1.78,
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
function cov_post() {
    cropper.getCroppedCanvas().toBlob((blob) => {
        var formData = new FormData();
        formData.append("file", blob);
        $.ajax({
            method: "POST",
            url: url + "/api/setCover",
            data: formData,
            processData: false,
            contentType: false,
            success: function (result) {
                result = JSON.parse(result);
                if (result.result === true)
                    window.location.href = url + "/c/" + lgs_r.nickname;
            },
        });
    });
}
function name_upload() {
    if ($("#d-open").text() === "1") return;
    $(".upc").css("display", "none");
    act_upc = 1;
    $("#upc-nick").attr("value", $("#d-name").text());
    $("#upc-n").fadeIn("0.3s");
}
function gend_upload() {
    if ($("#d-open").text() === "1") return;
    $(".upc").css("display", "none");
    act_upc = 2;
    var dg = document.getElementById("d-gender").textContent;
    if (dg === "1") upc_gd(1);
    if (dg === "2") upc_gd(2);
    if (dg === "3") upc_gd(3);
    $("#upc-g").fadeIn("0.3s");
}
function room_post() {
    if (!info_post_able || act_upc <= 0) return;
    var intro = null;
    if (document.getElementById("upc-intro").value.length != 0)
        intro = document.getElementById("upc-intro").value;
    $.ajax({
        method: "POST",
        url: url + "/api/updateRoom",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            uid: lgs_r.uid,
            name: npc_name,
            intro: intro,
        }),
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) {
                upc_errM(false, result.message);
                act_upc = 0;
                return;
            }
            window.location.href = url + "/c/" + lgs_r.nickname;
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
        upc_errM(false, "名称长度应为 4 到 15 位");
        if (nk.length === 0)
            $("#upc-nick").css(
                "border-color",
                "#f07d58 #f07d58 #3ba8ab #f07d58"
            );
        return;
    }
    if (nk.includes("/") || nk.includes("?") || nk.includes("\\")) {
        upc_errM(false, "名称包含非法字符");
        if (nk.length === 0)
            $("#upc-nick").css(
                "border-color",
                "#f07d58 #f07d58 #3ba8ab #f07d58"
            );
        return;
    }
    upc_errM(true, "");
    npc_name = nk;
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
function cog(e) {
    $("#card-cover").attr(
        "src",
        geneCover(
            $("#d-cover").text(),
            $("#d-room").text(),
            $("#d-name").text()
        )
    );
    $("#card-mail-i").attr("value", url + "/r/" + $("#d-nickname").text());
    cog_view();
    npc_name = $("#d-name").text();
    setTimeout(shut, 180000);
}
function room_sw() {
    if ($("#d-open").text() === "1") {
        $.ajax({
            method: "POST",
            url: url + "/api/roomOff",
            success: function (result) {
                result = JSON.parse(result);
                if (result.result === false) {
                    $("#card-mail-i").attr("value", result.message);
                    return;
                }
                $("#room-sw-i").animate(
                    {
                        left: "-=120px",
                        borderColor: "#f07d58",
                        backgroundColor: "#1e1e1e",
                        color: "aliceblue",
                    },
                    "0.3s"
                );
                $("#room-sw-i").text("OFF");
                $("#room-sw-i").animate({ color: "aliceblue" });
                $("#room-sw-t").animate(
                    { left: "+=16px", color: "#1e1e1e" },
                    "0.3s"
                );
                $("#room-sw-t").text("房间关闭");
                $("#room-sw").animate(
                    {
                        borderColor: "#1e1e1e",
                        backgroundColor: "#f07d58",
                    },
                    "0.3s"
                );
                $("#d-open").text("0");
                cog_view();
            },
        });
    } else {
        $.ajax({
            method: "POST",
            url: url + "/api/roomOn",
            success: function (result) {
                result = JSON.parse(result);
                if (result.result === false) {
                    $("#card-mail-i").attr("value", result.message);
                    return;
                }
                $("#d-open").text("1");
                shutx = 0;
                cog_view();
            },
        });
    }
}
function cog_view() {
    if ($("#d-open").text() === "1") {
        $(".rtmpd").css("display", "table");
        $("#rtmp-mp").hide();
        $("#card-nick-t").attr("data-content", "关闭房间后才能修改哦");
        $("#card-intro").attr("data-content", "关闭房间后才能修改哦");
        $("#card-intro").attr("data-content", "关闭房间后才能修改哦");
        $("#room-sw-i").animate(
            {
                left: "+=120px",
                borderColor: "#3ba8ab",
                backgroundColor: "aliceblue",
                color: "#1e1e1e",
            },
            "0s"
        );
        $("#room-sw-i").text("ON");
        $("#room-sw-i").animate({ color: "#f07d58" });
        $("#room-sw-t").animate({ left: "-=16px", color: "aliceblue" }, "0s");
        $("#room-sw-t").text("房间开启");
        $("#room-sw").animate(
            {
                borderColor: "aliceblue",
                backgroundColor: "#3ba8ab",
            },
            "0s"
        );
        $("#d-open").text("1");
        $(".shutxb").attr("data-content", "已复制");
        $.ajax({
            method: "GET",
            url: url + "/api/getRtmpInfo",
            success: function (result) {
                result = JSON.parse(result);
                if (result.result === false) {
                    $(".shutxb").attr("value", result.message);
                    $(".shutxb").attr("data-content", result.message);
                    return;
                }
                $("#card-rtmp-i").attr("value", result.addr);
                $("#card-rtmpkey-i").attr("value", result.key);
            },
        });
    } else {
        $(".rtmpd").css("display", "none");
        $("#rtmp-mp").show();
        $("#card-nick-t").attr("data-content", "修改名称");
        $("#card-intro").attr("data-content", "关闭房间后才能修改哦");
        $("#card-intro").attr("data-content", "修改简介");
    }
}
function shut() {
    shutx = 1;
    $(".shutxb").attr("value", "已过期，请刷新页面");
    $(".shutxb").attr("data-content", "信息已过期，请刷新页面");
}
