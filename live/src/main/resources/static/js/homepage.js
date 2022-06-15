var pageOpen = 0,
    pageClose = 0;
var pageRoom = 0,
    pageUser = 0;
var searchKey;
var card =
    '<div class="video-card"><div class="card-cover"onclick="toRoom($(this).find(\'c\').text())"><div class="card-user"><img class="card-user-ava"onclick="toUser($(this).next().text())"/><c class="card-user-nick"onclick="toUser($(this).text())">nickname</c></div></div><div class="card-name"><div class="card-name-t"onclick="toRoom($(this).parent().prev().find(\'c\').text())">roomname</div></div></div>';
var cardU =
    '<div class="video-card user-card"><div class="card-cover card-avatar" onclick=\'toUser($(this).next().find(".card-name-t").text())\'></div><div class="card-name card-nick"><div class="card-name-t" onclick="toUser($(this).text())">nickname</div></div></div>';
var dummy = '<div class="video-card card-dummy"></div>';
function toUser(e) {
    window.open(url + "/u/" + e);
}
function toRoom(e) {
    window.open(url + "/r/" + e);
}
function toTerms() {
    window.open(url + "/terms");
}
function initReqRooms() {
    getMoreOpen();
    getMoreClose();
    setInterval(getServerLoad(), 2000);
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
        geneAvatar(element.avatar, element.uid, element.nickname)
    );
    return ncd;
}
function processNU(element) {
    var ncd = $(cardU.replace("nickname", element.nickname));
    ncd.find(".card-avatar").css(
        "background-image",
        "url(" + geneAvatar(element.avatar, element.uid, element.nickname) + ")"
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
            $("#video-ct-l-open .card-dummy").remove();
            result.room.forEach((element) => {
                $("#video-ct-l-open").append(processNR(element));
            });
            $("#open-count").text(result.total);
            if (6 * pageOpen + result.room.length >= 3)
                appendDummy($("#video-ct-l-open"));
            pageOpen += 1;
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
            $("#video-ct-l-close .card-dummy").remove();
            result.room.forEach((element) => {
                $("#video-ct-l-close").append(processNR(element));
            });
            $("#close-count").text(result.total);
            if (6 * pageClose + result.room.length >= 3)
                appendDummy($("#video-ct-l-close"));
            pageClose += 1;
        },
    });
}
function getServerLoad() {
    $.ajax({
        method: "GET",
        url: url + "/api/serverLoad",
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) return;
            $("#cpu-v").text(result.cpu + " %");
            $("#net-v").text(result.net + " %");
            var w = 5 + result.cpu * 1.5;
            $("#cpu-b").animate({ width: w + "px" }, "1.8s");
            w = 5 + result.net * 1.5;
            $("#net-b").animate({ width: w + "px" }, "1.8s");
            if (result.cpu <= 45)
                $("#cpu-b").animate({ borderColor: "#3ba8ab" }, "1.8s");
            else if (result.cpu < 75)
                $("#cpu-b").animate({ borderColor: "#f07d58" }, "1.8s");
            else $("#cpu-b").animate({ borderColor: "firebrick" }, "1.8s");
            if (result.net <= 45)
                $("#net-b").animate({ borderColor: "#3ba8ab" }, "1.8s");
            else if (result.net < 75)
                $("#net-b").animate({ borderColor: "#f07d58" }, "1.8s");
            else $("#net-b").animate({ borderColor: "firebrick" }, "1.8s");
        },
    });
    return getServerLoad;
}
function toUser(t) {
    window.open(url + "/u/" + t);
}
function initSearch() {
    searchKey = window.location.href.toString().split("?key=")[1];
    $(".head-sr").val(searchKey);
    searchMoreRooms();
    searchMoreUsers();
}
function searchMoreRooms() {
    $("#ct-more-open").hide();
    $.ajax({
        method: "GET",
        url:
            url +
            "/api/searchRoomsPaged?page=" +
            pageRoom +
            "&key=" +
            searchKey,
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) return;
            if (result.more === true) $("#ct-more-open").show();
            $("#video-ct-l-open .card-dummy").remove();
            result.room.forEach((element) => {
                $("#video-ct-l-open").append(processNR(element));
            });
            $("#open-count").text(result.total);
            if (6 * pageRoom + result.room.length >= 3)
                appendDummy($("#video-ct-l-open"));
            pageRoom += 1;
        },
    });
}
function searchMoreUsers() {
    $("#ct-more-close").hide();
    $.ajax({
        method: "GET",
        url:
            url +
            "/api/searchUsersPaged?page=" +
            pageUser +
            "&key=" +
            searchKey,
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) return;
            if (result.more === true) $("#ct-more-close").show();
            $("#video-ct-l-close .card-dummy").remove();
            result.user.forEach((element) => {
                $("#video-ct-l-close").append(processNU(element));
            });
            $("#close-count").text(result.total);
            if (6 * pageUser + result.user.length >= 3)
                appendDummy($("#video-ct-l-close"));
            pageUser += 1;
        },
    });
}
