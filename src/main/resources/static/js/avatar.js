var url = "http://localhost:7004";
var avatar;
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
