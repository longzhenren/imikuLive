$(function () {
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
});
