var socket;
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
        const dp = new DPlayer({
            container: document.getElementById("dplayer"),
            live: true,
            mutex: false,
            autoplay: true,
            screenshot: true,
            danmaku: true,
            theme: "#39c5bb",
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
function sendDanS() {}
