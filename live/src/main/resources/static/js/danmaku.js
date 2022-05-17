$(function () {
    $("#uavatar").attr(
        "src",
        geneAvatar(
            $("#d-avatar").text(),
            $("#d-uid").text(),
            $("#d-nickname").text()
        )
    );
    if ($("#d-open").text() === "0") {
        var arr = [
            "主播在摸鱼~",
            "现在没有开播哦~",
            "主播在睡大觉(￣o￣) . z Z",
            "主播在赶 ddl ~",
        ];
        $("#openstxt").text(arr[Math.floor(Math.random() * arr.length)]);
    } else {
        $("#openst").hide();
        $("#openstxt").hide();
        $("#openstimg").hide();
        const dp = new DPlayer({
            container: document.getElementById("dplayer"),
            live: true,
            mutex: false,
            autoplay: true,
            screenshot: true,
            danmaku: true,
            theme: "#39c5bb",
            video: {
                url: "http://live.imiku.fun:8888/live/ch1.flv",
                type: "flv",
            },
        });
        dp.play();
    }
});
function toUser() {
    window.open(url + "/u/" + $("#d-nickname").text());
}
