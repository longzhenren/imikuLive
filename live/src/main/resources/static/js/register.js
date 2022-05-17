var x = 0;
var able = false;
var able1 = false,
    able2 = false;
var ma, pa;
var re = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
var reg1 = /[a-zA-Z]/;
var reg2 = /[0-9]/;
var reg3 = /\W/;
function tk() {
    window.open(url + "/terms");
}
function ifg() {
    $.ajax({
        method: "GET",
        url: url + "/api/loginState",
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === true) window.location.href = url;
        },
    });
}
function refresh() {
    window.location.href = url + "/register?e=" + ma;
}
function checkEmail(s) {
    if (re.test(s)) return true;
    return false;
}
function em(e) {
    var email = document.getElementById("email").value.toLowerCase();
    if (x === 0) {
        if (email.length <= 4) return;
        if (checkEmail(email) === false) {
            wr(false, "电子邮箱格式错误", "email");
            if (email.length === 0)
                $("#email").css(
                    "border-color",
                    "#f07d58 #f07d58 #3ba8ab #f07d58"
                );
            return;
        }
        wr(true, "", "email");
        return;
    }
    if (email.length < 4 || email.length > 15) {
        wr(false, "昵称长度应为 4 到 15 位", "email");
        if (email.length === 0)
            $("#email").css("border-color", "#f07d58 #f07d58 #3ba8ab #f07d58");
        return;
    }
    if (email.includes("/") || email.includes("?") || email.includes("\\")) {
        wr(false, "昵称包含非法字符", "email");
        if (email.length === 0)
            $("#email").css("border-color", "#f07d58 #f07d58 #3ba8ab #f07d58");
        return;
    }
    wr(true, "", "email");
}
function un(e) {
    var pass = document.getElementById("password").value;
    if (x === 0) {
        if (pass.length < 8) {
            wr(false, "密码长度至少 8 位", "password");
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
            wr(false, "密码应至少包含数字、字母和符号其中两样", "password");
            return;
        }
        wr(true, "", "password");
        return;
    }
    if (pa != pass) {
        wr(false, "两次密码不一致", "password");
        if (pass.length === 0)
            $("#password").css(
                "border-color",
                "#f07d58 #f07d58 #3ba8ab #f07d58"
            );
        return;
    }
    wr(true, "", "password");
}
function submit() {
    if (checkEmail(document.getElementById("email").value.toLowerCase()))
        wr(true, "", "email");
    if (able === false) {
        wr(false, "请正确输入邮箱和密码", "password");
        return;
    }
    loading(true);
    if (x === 0) {
        ma = document.getElementById("email").value.toLowerCase();
        pa = document.getElementById("password").value;
        $.ajax({
            method: "POST",
            url: url + "/api/checkEmail",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({
                email: ma,
            }),
            success: function (result) {
                result = JSON.parse(result);
                if (result.result === false) {
                    fail(result.message);
                    loading(false);
                    return;
                }
                x += 1;
                document.getElementById("ts").textContent = "补充信息";
                succ();
                document.getElementById("email").value = "";
                document.getElementById("email").placeholder = "昵称";
                document.getElementById("password").value = "";
                document.getElementById("password").placeholder = "重复密码";
                able = false;
                able1 = false;
                able2 = false;
                document.getElementById("exasvg").innerHTML =
                    '<path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/><path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>';
                $("#password").css("width", "270px");
                $(".refsvg").css("display", "inline");
                loading(false);
            },
        });
        return;
    }
    var nick = document.getElementById("email").value;
    $.ajax({
        method: "POST",
        url: url + "/api/register",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            email: ma,
            password: pa,
            nickname: nick,
        }),
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) {
                fail(result.message);
                $("#email").css(
                    "border-color",
                    "#f07d58 #f07d58 #ff0000 #f07d58"
                );
                loading(false);
                return;
            }
            $(".forg").animate({ top: "+=50%" }, "1s");
            loading(false);
        },
    });
}
function wr(t, w, tt) {
    if (t === true) {
        if (tt.length == 5) able1 = true;
        else able2 = true;
        if (able1 && able2) able = true;
        $("#" + tt).css("border-color", "#f07d58 #f07d58 #3ba8ab #f07d58");
        document.getElementById("mes").textContent = "";
        return;
    }
    able = false;
    if (tt.length == 5) able1 = false;
    else able2 = false;
    $("#" + tt).css("border-color", "#f07d58 #f07d58 #ff0000 #f07d58");
    document.getElementById("mes").textContent = w;
}
function fail(s) {
    document.getElementById("fail-cont").textContent = s;
    $(".fail").animate({ top: "-=50%" }, "1s").delay(2000);
    $(".fail").animate({ top: "+=50%" }, "1s");
}
function succ() {
    $("#succ").animate({ left: "+=200%" }, "2s");
    setTimeout(function () {
        $("#succ").css("left", "-100%");
    }, 2000);
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
