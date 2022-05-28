var socket, dp;
var danN =
    '<div class="dc-c"><div class="dc-cn">name</div><div class="dc-ct">text</div></div>';
$(function () {
    $("#uavatar").attr(
        "src",
        geneAvatar(
            $("#d-avatar").text(),
            $("#d-uid").text(),
            $("#d-nickname").text()
        )
    );
    if ($("#d-intro").text().length === 0)
        $("#introt").text("主播懒得写简介ヾ(≧▽≦*)o");
    if ($("#d-open").text() === "0") {
        var arr = [
            "主播在摸鱼~",
            "现在没有开播哦~",
            "主播在睡大觉  ヾ(￣▽￣)",
            "主播在赶 ddl ~",
        ];
        $("#openst").css(
            "background-image",
            "url(" +
                geneCover(
                    $("#d-cover").text(),
                    $("#d-room").text(),
                    $("#d-name").text()
                ) +
                ")"
        );
        $("#openstxt").text(arr[Math.floor(Math.random() * arr.length)]);
        $("#dbg-c").text("现在没有开播啊");
    } else {
        $("#openst").hide();
        $("#openstxt").hide();
        $("#openstimg").hide();
        $("#dc-admin")
            .children(".dc-cn")
            .attr("onclick", "window.open('" + url + "/u/Administrator')");
        $("#dc-admin").children(".dc-cn").css("color", "#3ba8ab");
        $("#dc-admin").children(".dc-ct").css("color", "aliceblue");
        var uri =
            url +
            "/stream/" +
            $("#d-room").text() +
            "/" +
            $("#d-room").text() +
            ".flv";
        socket = io.connect(wsUrl + "/?room=" + $("#d-room").text());
        socket.on("audience_num", (data) => {
            $("#current-n").text(data);
        });
        socket.on("danmaku", (data) => {
            drawDanS(data, false);
        });
        dp = new DPlayer({
            container: document.getElementById("dplayer"),
            live: true,
            mutex: false,
            autoplay: true,
            screenshot: true,
            danmaku: true,
            theme: "#39c5bb",
            apiBackend: {
                read: function (options) {
                    options.success();
                },
                send: sendDanS,
            },
            video: {
                url: uri,
                type: "flv",
            },
        });
        dp.play();
    }
});
function initDanS(e) {
    if (e === true && $("#d-open").text() === "1") {
        $("#danmaku-s").attr("onclick", "sendDanS()");
        $("#danmaku-s").text("发 射");
        $("#dc").append("");
        return;
    }
    if ($("#d-open").text() === "1") {
        $("#danmaku-s").attr("onclick", "toLogin()");
        $("#danmaku-s").text("去登录");
        $("#danmaku-i").attr("placeholder", "登录后才能发弹幕  \\(￣︶￣*\\))");
    } else {
        $("#danmaku-i").attr(
            "placeholder",
            "不开播没啥好讨论的吧  \\(￣︶￣*\\))"
        );
        $("#danmaku-s").text("等开播");
    }
    $("#danmaku-i").attr("disabled", "true");
}
function toUser() {
    window.open(url + "/u/" + $("#d-nickname").text());
}
function sendDanS(data) {
    if (typeof data === "undefined") {
        var text = $("#danmaku-i").val();
        if (text.length === 0) return;
        var dan = {
            text: text,
            color: "#fff",
            type: 0,
            name: lgs_r.nickname,
        };
        dp.danmaku.send(dan, $("#danmaku-i").val(""));
    } else {
        data.data.name = lgs_r.nickname;
        socket.emit("danmaku", JSON.stringify(data.data));
        try {
            data.success();
        } catch (error) {}
        drawDanS(data.data, true);
    }
}
function drawDanS(data, e) {
    if (!e) {
        data = JSON.parse(data);
        dp.danmaku.draw(data);
    }
    var nt = $(danN.replace("name", data.name).replace("text", data.text));
    $("#dc").append(nt);
    nt.children(".dc-cn").attr(
        "onclick",
        "window.open('" + url + "/u/" + data.name + "')"
    );
    if (e) {
        nt.children(".dc-cn").css("color", "#f07d58");
        nt.children(".dc-ct").css("color", "aliceblue");
    }
}
