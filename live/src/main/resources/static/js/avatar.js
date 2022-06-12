var sNick;
var opo = false,
    opn = false;
var ss1 = false,
    ss2 = false;
var lgd = false;
var opx = null;
var lgs_r;
var foregroundClr = [
    [240, 125, 88, 255],
    [57, 197, 187, 255],
    [178, 34, 34, 255],
    [250, 227, 176, 255],
    [240, 248, 255, 255],
];
function geneCover(r, i, n) {
    var avatar;
    var options = {
        foreground: foregroundClr[i % 5],
        background: [255, 255, 255, 100],
        size: 256,
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
    avatar = url + "/files/covers/" + r;
    return avatar;
}

function geneAvatar(r, i, n) {
    var avatar;
    var options = {
        foreground: foregroundClr[i % 5],
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
function geneHead(f) {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            result = JSON.parse(result);
            // result = {
            //     uid: 100000,
            //     email: "145@1456.15",
            //     nickname: "SuperUser",
            //     avatar: "auto",
            // };
            if (result.result === false) {
                if (typeof f === "undefined") return;
                f(false);
                return;
            }
            lgd = true;
            lgs_r = result;
            sNick = result.nickname;
            $(".ava").attr(
                "src",
                geneAvatar(result.avatar, result.uid, result.nickname)
            );
            $(".nic").text(sNick);
            $("#op-nick").text(sNick);
            $("#login-btn").css("display", "none");
            $("#logged-sw").css("display", "block");
            if (typeof f === "undefined") return;
            f(true);
        },
    });
}
function ifg(f) {
    geneHead(f);
    try {
        $("#card-i").attr(
            "src",
            geneAvatar(
                document.getElementById("d-avatar").textContent,
                document.getElementById("d-uid").textContent,
                document.getElementById("d-nickname").textContent
            )
        );
        if (document.getElementById("d-room").textContent == "0") {
            document.getElementById("card-room").textContent =
                "该用户没有直播间";
            document.getElementById("card-room").classList.remove("card-btn");
        }
    } catch (e) {}
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
    else window.location.href = url + "/login";
}
function toSelf() {
    window.location.href = url + "/u/" + sNick;
}
function toMR() {
    if ($("#d-room").text() === "0")
        window.location.href = url + "/r/" + lgs_r.nickname;
    window.location.href = url + "/c/" + lgs_r.nickname;
}
function toUR() {
    if ($("#d-room").text() != "0")
        window.location.href = url + "/r/" + lgs_r.nickname;
}
function toCC() {
    window.location.href = url + "/c/" + lgs_r.nickname;
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
