var socket, dp;
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
            drawDanS(data);
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
    if (e === true) {
        $("#danmaku-s").attr("onclick", "sendDanS()");
        $("#danmaku-s").text("发 射");
        return;
    }
    $("#danmaku-i").attr("placeholder", "登录后才能发弹幕  \\(￣︶￣*\\))");
    $("#danmaku-i").attr("disabled", "true");
    $("#danmaku-s").attr("onclick", "toLogin()");
    $("#danmaku-s").text("去登录");
}
function toUser() {
    window.open(url + "/u/" + $("#d-nickname").text());
}
function sendDanS(data) {
    if (typeof data === "undefined") {
        var text = $("#danmaku-i").val();
        if (text.length === 0) return;
        // socket.emit(
        //     "danmaku",
        //     JSON.stringify({
        //         text: text,
        //         color: "#fff",
        //         type: 0,
        //     })
        // );
        // $("#danmaku-i").val("");
        dp.danmaku.send(
            {
                text: text,
                color: "#fff",
                type: 0,
            },
            $("#danmaku-i").val("")
        );
    } else {
        console.log(data);
        socket.emit("danmaku", JSON.stringify(data.data));
        try {
            data.success();
        } catch (error) {}
    }
}
function drawDanS(data) {
    dp.danmaku.draw(JSON.parse(data));
}
