var url = "http://localhost:7004";
var x = 0;
var able = false;
var pass1;
var reg1 = /[a-zA-Z]/;
var reg2 = /[0-9]/;
var reg3 = /\W/;
function refresh() {
    location.reload();
}
function ifg() {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            if (result.result) window.location.href = url;
        },
    });
}
function tk() {
    window.open(url + "/terms");
}
function submit() {
    if (able === false) {
        wr(false, "请正确输入密码");
        return;
    }
    loading(true);
    if (x === 0) {
        x += 1;
        document.getElementById("ts").textContent = "再输一次";
        pass1 = document.getElementById("password").value;
        succ();
        document.getElementById("password").value = "";
        document.getElementById("password").placeholder = "确认密码";
        able = false;
        $("#password").css("width", "270px");
        $(".refsvg").css("display", "inline");
        loading(false);
        return;
    }
    var email = document.getElementById("e").textContent.toLowerCase();
    var id = document.getElementById("i").textContent;
    $.ajax({
        method: "POST",
        url: url + "/api/resetPassword",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            e: email,
            p: pass1,
            i: id,
        }),
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) {
                fail(result.message);
                loading(false);
                return;
            }
            $(".forg").animate({ top: "+=50%" }, "1s").delay(1000);
            $("#succ").animate({ left: "+=200%" }, "2s");
            setTimeout(function () {
                window.location.href = url + "/login?e=" + email;
            }, 1500);
        },
    });
}
function succ() {
    $("#succ").animate({ left: "+=200%" }, "2s");
    setTimeout(function () {
        $("#succ").css("left", "-100%");
    }, 2000);
}
function un(e) {
    var pass = document.getElementById("password").value;
    if (x === 0) {
        if (pass.length < 8) {
            wr(false, "密码长度至少 8 位");
            if (pass.length === 0)
                $("#password").css(
                    "border-color",
                    "#f07d58 #f07d58 #3ba8ab #f07d58"
                );
            return;
        }
        var cx = 0;
        if (reg1.test(pass)) cx += 1;
        if (reg2.test(pass)) cx += 1;
        if (reg3.test(pass)) cx += 1;
        if (cx < 2) {
            wr(false, "密码应至少包含数字、字母和符号其中两样");
            return;
        }
        wr(true, "");
        return;
    }
    if (pass1 != pass) {
        wr(false, "两次密码不一致");
        if (pass.length === 0)
            $("#password").css(
                "border-color",
                "#f07d58 #f07d58 #3ba8ab #f07d58"
            );
        return;
    }
    wr(true, "");
}
function wr(t, w) {
    if (t === true) {
        able = true;
        $("#password").css("border-color", "#f07d58 #f07d58 #3ba8ab #f07d58");
        document.getElementById("mes").textContent = "";
        return;
    }
    able = false;
    $("#password").css("border-color", "#f07d58 #f07d58 #ff0000 #f07d58");
    document.getElementById("mes").textContent = w;
}
function fail(s) {
    document.getElementById("fail-cont").textContent = s;
    $(".fail").animate({ top: "-=50%" }, "1s");
}
function loading(e) {
    if (e === true) {
        $(".sigsvg").css("visibility", "hidden");
        $(".loadsvg").fadeIn("0.2s");
        return;
    }
    $(".sigsvg").css("visibility", "visible");
    $(".loadsvg").fadeOut("0.2s");
}
