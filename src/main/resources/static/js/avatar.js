var url = "http://localhost:7004";
var avatar, sNick;
var opo = false,
    opn = false;
var lgd = false;
var opx = null;
function geneAvatar(r) {
    if (r.avatar === "auto") {
        var data = new Identicon(
            CryptoJS.SHA256(r.uid.toString() + r.nickname.toString()).toString(
                CryptoJS.enc.Hex
            ),
            330
        ).toString();
        avatar = "data:image/png;base64," + data;
        return;
    }
    avatar = url + "/files/avatars/" + r.avatar;
}
function geneHead() {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            result = JSON.parse(result);
            // result = {
            //     uid: 100000,
            //     email: "145@1456.15",
            //     nickname: "Operacon",
            //     avatar: "233.jpg",
            // };
            if (result.result === false) return;
            lgd = true;
            geneAvatar(result);
            sNick = result.nickname;
            $(".ava").attr("src", avatar);
            $(".nic").text(sNick);
            $("#op-nick").text(sNick);
            $("#login-btn").css("display", "none");
            $("#logged-sw").css("display", "block");
        },
    });
}
window.addEventListener("wheel", function (e) {
    var item = document.getElementById("logged-sw-t");
    if (e.deltaY > 0) item.scrollLeft += 5;
    else item.scrollLeft -= 5;
});
function toIndex() {
    window.location.href = url;
}
function toLogin() {
    window.location.href = url + "/login";
}
function toSelf() {
    window.location.href = url + "/u/" + sNick;
}
function toRoom() {
    window.location.href = url + "/r/" + sNick;
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
        if (lgd === true) {
            $("#head-xy-ac").css("display", "block");
        }
        opn = true;
        return;
    }
    $("#zd-btn").css("background-color", "transparent");
    $("#head-xy-sc").css("display", "none");
    if (lgd === true) {
        $("#head-xy-ac").css("display", "none");
    }
    opn = false;
}
