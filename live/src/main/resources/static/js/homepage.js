var pageOpen = 0,
    pageClose = 0;
var card =
    '<div class="video-card"><div class="card-cover"onclick="toRoom($(this).find(\'c\').text())"><div class="card-user"><img class="card-user-ava"onclick="toUser($(this).next().text())"/><c class="card-user-nick"onclick="toUser($(this).text())">nickname</c></div></div><div class="card-name"><div class="card-name-t"onclick="toRoom($(this).parent().prev().find(\'c\').text())">roomname</div></div></div>';
var dummy = '<div class="video-card card-dummy"></div>';
function toUser(e) {
    window.open(url + "/u/" + e);
}
function toRoom(e) {
    window.open(url + "/r/" + e);
}
function initReqRooms() {
    getMoreOpen();
    getMoreClose();
}
function processNR(element) {
    var ncd = $(
        card
            .replace("nickname", element.nickname)
            .replace("roomname", element.name)
    );
    ncd.find(".card-cover").css(
        "background-image",
        "url(" + geneCover(element.cover, element.rid, element.name) + ")"
    );
    ncd.find(".card-user-ava").attr(
        "src",
        geneAvatar(result.avatar, result.uid, result.nickname)
    );
    return ncd;
}
function appendDummy(e) {
    var x = 6;
    while (x != 0) {
        e.append($(dummy));
        x -= 1;
    }
}
function getMoreOpen() {
    $("#ct-more-open").hide();
    $.ajax({
        method: "GET",
        url: url + "/api/getOpenRoomsPaged?page=" + pageOpen,
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) return;
            if (result.more === true) $("#ct-more-open").show();
            pageOpen += 1;
            $("#video-ct-l-open .card-dummy").remove();
            result.room.forEach((element) => {
                $("#video-ct-l-open").append(processNR(element));
            });
            appendDummy($("#video-ct-l-open"));
        },
    });
}
function getMoreClose() {
    $("#ct-more-close").hide();
    $.ajax({
        method: "GET",
        url: url + "/api/getCloseRoomsPaged?page=" + pageClose,
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) return;
            if (result.more === true) $("#ct-more-close").show();
            pageClose += 1;
            $("#video-ct-l-close .card-dummy").remove();
            result.room.forEach((element) => {
                $("#video-ct-l-close").append(processNR(element));
            });
            appendDummy($("#video-ct-l-close"));
        },
    });
}
