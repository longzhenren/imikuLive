var url = "http://localhost:7004";
function reg() {
    window.location.href = url + "/register";
}
function tk() {
    window.open(url + "/terms");
}
function checkEmail(s) {
    var re = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
    if (re.test(s)) return true;
    return false;
}
function tip(i) {
    if (i > 0) {
        if (i == 1) $("#tip1").css("visibility", "visible");
        else if (i == 2) $("#tip2").css("visibility", "visible");
    } else if (i < 0) {
        if (i == -1) $("#tip1").css("visibility", "hidden");
        else if (i == -2) $("#tip2").css("visibility", "hidden");
    }
}
function submit() {
    var email = document.getElementById("email").value.toLowerCase();
    var pass = document.getElementById("password").value;
    if (checkEmail(email) === false || pass.length < 8) {
        fail("请输入正确的邮箱地址和密码");
        return;
    }
    $.ajax({
        method: "POST",
        url: url + "/api/login",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            email: email,
            password: pass,
        }),
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) {
                fail(result.message);
                return;
            }
            $("#succ").animate({ left: "+=200%" }, "2s");
            setTimeout(function () {
                window.location.href = url;
            }, 1000);
        },
    });
}
function forget() {
    var email = document.getElementById("email").value.toLowerCase();
    if (checkEmail(email) === false) {
        fail("请输入正确的邮箱地址后重试");
        return;
    }
    $.ajax({
        method: "POST",
        url: url + "/api/forgot",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
            email: email,
        }),
        success: function (result) {
            result = JSON.parse(result);
            if (result.result === false) {
                fail(result.message);
                return;
            }
            $(".forg").animate({ top: "+=37%" }, "1s");
            return;
        },
    });
}
function fail(s) {
    document.getElementById("fail-cont").textContent = s;
    $(".fail").animate({ top: "-=37%" }, "1s").delay(2000);
    $(".fail").animate({ top: "+=37%" }, "1s");
}
