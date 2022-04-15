var url = "http://localhost:7004";
var sNick;
var opo = false,
    opn = false;
var ss1 = false,
    ss2 = false;
var lgd = false;
var opx = null;
var lgs_r;
function geneAvatar(r, i, n) {
    var avatar;
    var options = {
        background: [255, 255, 255, 100],
        size: 330,
        format: "svg",
        margin: 0,
    };
    if (r === "auto") {
        var data = new Identicon(
            CryptoJS.SHA256(i.toString() + n.toString()).toString(
                CryptoJS.enc.Hex
            ),
            options
        ).toString();
        avatar = "data:image/svg+xml;base64," + data;
        return avatar;
    }
    avatar = url + "/files/avatars/" + r;
    return avatar;
}
function geneHead() {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            result = JSON.parse(result);
            result = {
                uid: 100000,
                email: "145@1456.15",
                nickname: "SuperUser",
                avatar: "auto",
            };
            if (result.result === false) return;
            lgd = true;
            lgs_r = result;
            lgs_r.gender = document.getElementById("d-gender").textContent;
            sNick = result.nickname;
            $(".ava").attr(
                "src",
                geneAvatar(result.avatar, result.uid, result.nickname)
            );
            $(".nic").text(sNick);
            $("#op-nick").text(sNick);
            $("#login-btn").css("display", "none");
            $("#logged-sw").css("display", "block");
        },
    });
}
function ifg() {
    geneHead();
    $("#card-i").attr(
        "src",
        geneAvatar(
            document.getElementById("d-avatar").textContent,
            document.getElementById("d-uid").textContent,
            document.getElementById("d-nickname").textContent
        )
    );
    if (document.getElementById("d-room").textContent == "0") {
        document.getElementById("card-room").textContent = "该用户没有直播间";
        document.getElementById("card-room").classList.remove("card-room");
    }
}
window.addEventListener("wheel", function (e) {
    if (ss1) {
        var item = document.getElementById("logged-sw-t");
        if (e.deltaY > 0) item.scrollLeft += 5;
        else item.scrollLeft -= 5;
    }
    if (ss2) {
        var item = document.getElementById("card-nick");
        if (e.deltaY > 0) item.scrollLeft += 25;
        else item.scrollLeft -= 25;
    }
});
function toIndex() {
    window.location.href = url;
}
function toLogin() {
    if (url.includes("/"))
        window.location.href = url + "/login?f=" + window.location.href;
    else window.location.href = url;
}
function toSelf() {
    window.location.href = url + "/u/" + sNick;
}
function toMR() {
    window.location.href = url + "/r/" + sNick;
}
function toUR() {
    if (document.getElementById("d-room").textContent == "0") return;
    window.location.href =
        url + "/r/" + document.getElementById("d-nickname").textContent;
}
function logout() {
    $.ajax({
        method: "POST",
        url: url + "/api/logout",
        success: function (result) {
            toIndex();
        },
    });
}
function opsw(e) {
    if (e > 0) {
        if (opx != null) {
            console.log(1154);
            clearTimeout(opx);
            opx = null;
            return;
        }
        if (!opo) {
            $("#logged-sw-t").fadeOut();
            $("#logged-sw-i").animate({
                width: "80px",
                height: "80px",
                left: "-=25px",
            });
            $("#logged-sw-op").fadeIn();
            opo = true;
            return;
        }
    }
    if (e < 0 && opo) {
        opx = setTimeout(function hide() {
            $("#logged-sw-t").fadeIn();
            $("#logged-sw-i").animate({
                width: "30px",
                height: "30px",
                left: "+=25px",
            });
            $("#logged-sw-op").fadeOut();
            opo = false;
            opx = null;
        }, 100);
    }
}
function search() {
    var con = document.getElementById("head-sr").value;
    alert(con);
}
function mofold() {
    if (!opn) {
        $("#zd-btn").css("background-color", "#505050");
        $("#head-xy-sc").css("display", "block");
        if (lgd === true) $("#head-xy-ac").css("display", "block");
        opn = true;
        return;
    }
    $("#zd-btn").css("background-color", "transparent");
    $("#head-xy-sc").css("display", "none");
    if (lgd === true) $("#head-xy-ac").css("display", "none");
    opn = false;
}
