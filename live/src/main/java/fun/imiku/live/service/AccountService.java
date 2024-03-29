/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import fun.imiku.live.component.FileResource;
import fun.imiku.live.dao.UserDAO;
import fun.imiku.live.entity.User;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AccountService {
    @Value(value = "${site.url}")
    String url;
    @Value(value = "#{'${site.nicksBanned:}'.split(',')}")
    Set<String> nicksBanned;
    @Autowired
    UserDAO userDAO;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    FileResource fileResource;

    private static final Pattern mailPt = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*@[A-Za-z0-9]+(([.\\-])[A-Za-z0-9]+)" +
            "*\\.[A-Za-z0-9]+$");
    private static final String regMail = "<body style=\"margin:0;padding:0\"><style>" +
            ".mail-confirm-button:hover{background-color:#479db4}" +
            "</style><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td><table align=" +
            "\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"690\" style=\"border-collapse:" +
            "collapse\"><div class=\"mail-wrapper\" style=\"background-color:#1e1e1e;padding:40px 40px\">" +
            "<div class=\"mail-card\" style=\"margin:0 auto;max-width:630px;background-color:#333;box-shadow:" +
            "0 0 15px rgb(0 0 0 / 10%);border-radius:4px;text-align:center;padding:30px;color:#fff\"><div" +
            " class=\"mail-title\" style=\"font-size:20px;font-weight:600;padding:15px 0;color:#3ba8ab;" +
            "letter-spacing:8px\">认证您的 <span style=\"letter-spacing:2px;color:#f07d58\">imikuLive</span>" +
            " 注册邮箱</div><div class=\"mail-desc\" style=\"padding:15px 0\">您刚刚使用此邮箱地址注册了账号</div>" +
            "<div class=\"mail-desc\" style=\"padding:15px 0\">请点击以下链接进行认证</div><div class=\"mail-" +
            "confirm\" style=\"padding:15px 0\"><div class=\"mail-confirm-button\" style=\"background-color" +
            ":#3ba8ab;display:inline-block;padding:12px;color:#fff;border-radius:4px;cursor:pointer;width:36%" +
            ";letter-spacing:8px\"><a href=\"@LINK\" target=\"_blank\" style=\"text-decoration:none;" +
            "color:inherit\">认证邮箱</a></div></div><div class=\"mail-tip\" style=\"color:#aaa;padding:15px" +
            " 0\">*如果这不是您本人执行的操作，请忽略此邮件</div></div></div></table></td></tr></table></body>";
    private static final String fgtMail = "<body style=\"margin:0;padding:0\"><style>" +
            ".mail-confirm-button:hover{background-color:#479db4}</style><table border=\"0\" cellpadding=\"0\" " +
            "cellspacing=\"0\" width=\"100%\"><tr><td><table align=\"center\" border=\"0\" cellpadding=\"0\" " +
            "cellspacing=\"0\" width=\"690\" style=\"border-collapse:collapse\"><div class=\"mail-wrapper\" " +
            "style=\"background-color:#1e1e1e;padding:40px 40px\"><div class=\"mail-card\" style=\"margin:0 " +
            "auto;max-width:630px;background-color:#333;box-shadow:0 0 15px rgb(0 0 0 / 10%);border-radius:4px;" +
            "text-align:center;padding:30px;color:#fff\"><div class=\"mail-title\" style=\"font-size:20px;" +
            "font-weight:600;padding:15px 0;color:#3ba8ab;letter-spacing:8px\">重设您的 <span style=\"letter" +
            "-spacing:2px;color:#f07d58\">imikuLive</span> 账户密码</div><div class=\"mail-desc\" style=\"" +
            "padding:15px 0\">您似乎刚在登录时忘记了您的密码</div><div class=\"mail-desc\" style=\"padding:15px 0\">" +
            "请点击以下链接进行密码重设</div><div class=\"mail-confirm\" style=\"padding:15px 0\"><div class=\"mail" +
            "-confirm-button\" style=\"background-color:#3ba8ab;display:inline-block;padding:12px;color:#fff;" +
            "border-radius:4px;cursor:pointer;width:36%;letter-spacing:8px\"><a href=\"@LINK\" target=" +
            "\"_blank\" style=\"text-decoration:none;color:inherit\">重设密码</a></div></div><div class=" +
            "\"mail-tip\" style=\"color:#aaa;padding:15px 0\">*如果这不是您本人执行的操作，请忽略此邮件</div></div>" +
            "</div></table></td></tr></table></body>";

    public void checkLogin(String email, String password, String ip, HttpSession session, HashMap<String, Object> ret) {
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) {
            ret.put("result", false);
            ret.put("message", "邮箱或密码错误");
            return;
        }
        String bufPass = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)).substring(5, 29);
        User tar = res.get(0);
        if (!tar.getPassword().equals(bufPass)) {
            ret.put("result", false);
            ret.put("message", "邮箱或密码错误");
            return;
        }
        if (tar.getInnerCode() > 0) {
            ret.put("result", false);
            ret.put("message", "请验证您的邮箱");
            return;
        }
        session.setAttribute("uid", tar.getId());
        session.setAttribute("email", email);
        session.setAttribute("nickname", tar.getNickname());
        session.setAttribute("avatar", tar.getAvatar());
        session.setAttribute("room", tar.getRoom());
        session.setAttribute("gender", tar.getGender());
        tar.setIp(ip);
        userDAO.saveAndFlush(tar);
        ret.put("result", true);
    }

    public void forget(String email, HashMap<String, Object> ret) throws MessagingException {
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) {
            ret.put("result", false);
            ret.put("message", "邮箱地址不存在");
            return;
        }
        User tar = res.get(0);
        int innerCode = -(int) (System.currentTimeMillis() % 1000000000 + Math.round(Math.random() % 1000000000));
        tar.setInnerCode(innerCode);
        userDAO.save(tar);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("live@imiku.fun");
        helper.setTo(email);
        helper.setSubject("【imikuLive】重设您的密码");
        helper.setText(fgtMail.replace("@LINK",
                url + "/resetPassword?e=" + email + "&i=" + -innerCode), true);
        mailSender.send(message);
        ret.put("result", true);
    }

    public void reset(String email, int id, String pass, HashMap<String, Object> ret) {
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) {
            ret.put("result", false);
            ret.put("message", "邮箱不存在，请合理使用");
            return;
        }
        User tar = res.get(0);
        if (tar.getInnerCode() != -id || tar.getInnerCode() == 0) {
            ret.put("result", false);
            ret.put("message", "请勿冒充其他用户");
            return;
        }
        tar.setInnerCode(0);
        tar.setPassword(DigestUtils.md5DigestAsHex(pass.getBytes(StandardCharsets.UTF_8)).substring(5, 29));
        userDAO.saveAndFlush(tar);
        ret.put("result", true);
    }

    public void register(String email, String pass, String nick, HashMap<String, Object> ret) throws MessagingException {
        if (!checkEmail(email, ret) || !checkNick(nick, ret)) return;
        User tar = new User();
        tar.setEmail(email);
        tar.setNickname(nick);
        tar.setPassword(DigestUtils.md5DigestAsHex(pass.getBytes(StandardCharsets.UTF_8)).substring(5, 29));
        tar.setAvatar("auto");
        tar.setIp("未知");
        tar.setGender(3);
        tar.setRoom(0);
        int innerCode = (int) (System.currentTimeMillis() % 1000000000 + Math.round(Math.random() % 1000000000));
        tar.setInnerCode(innerCode);
        userDAO.saveAndFlush(tar);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("live@imiku.fun");
        helper.setTo(email);
        helper.setSubject("【imikuLive】认证您的邮箱");
        helper.setText(regMail.replace("@LINK",
                url + "/confirm?e=" + email + "&i=" + innerCode), true);
        mailSender.send(message);
    }

    public boolean confirm(String email, int id) {
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) return false;
        User tar = res.get(0);
        if (tar.getInnerCode() != id || tar.getInnerCode() == 0) return false;
        tar.setInnerCode(0);
        userDAO.saveAndFlush(tar);
        return true;
    }

    public boolean checkEmail(String email, HashMap<String, Object> ret) {
        if (!mailPt.matcher(email).matches()) {
            ret.put("result", false);
            ret.put("message", "邮箱格式不正确");
            return false;
        }
        // 调用 https://github.com/ivolo/disposable-email-domains 提供的 api 进行临时邮箱排除
        ResponseEntity<String> result = restTemplate.getForEntity("https://open.kickbox.com/v1/disposable/" + email, String.class);
        if (result.getStatusCodeValue() != 200) {
            ret.put("result", false);
            ret.put("message", "内部服务器错误");
            return false;
        }
        // 根据返回体长度快速判断是否 true
        if (Objects.requireNonNull(result.getBody()).length() == 19) {
            ret.put("result", false);
            ret.put("message", "禁止使用临时邮箱注册");
            return false;
        }
        List<User> res = userDAO.findByEmail(email);
        if (res.size() != 0) {
            ret.put("result", false);
            ret.put("message", "该邮箱已注册，请直接登录");
            return false;
        }
        ret.put("result", true);
        return true;
    }

    public boolean checkNick(String nick, HashMap<String, Object> ret) {
        if (nick.length() < 4 || nick.length() > 15) {
            ret.put("result", false);
            ret.put("message", "昵称长度应为 4 到 15 位");
            return false;
        }
        if (nicksBanned.contains(nick)) {
            ret.put("result", false);
            ret.put("message", "该昵称禁用");
            return false;
        }
        if (nick.contains("/") || nick.contains("\\") || nick.contains("?")) {
            ret.put("result", false);
            ret.put("message", "昵称包含非法字符");
            return false;
        }
        List<User> res = userDAO.findByNickname(nick);
        if (res.size() != 0) {
            ret.put("result", false);
            ret.put("message", "该昵称已被使用，请更换昵称");
            return false;
        }
        ret.put("result", true);
        return true;
    }

    public void getByNickname(String nickname, HashMap<String, Object> ret) {
        List<User> res = userDAO.findByNickname(nickname);
        if (res.size() == 0) {
            ret.put("result", false);
            return;
        }
        User tar = res.get(0);
        ret.put("uid", tar.getId());
        ret.put("email", tar.getEmail());
        ret.put("gender", tar.getGender());
        ret.put("avatar", tar.getAvatar());
        ret.put("intro", tar.getIntro());
        ret.put("room", tar.getRoom());
    }

    public boolean pageByNickname(String nickname, Model model) {
        List<User> res = userDAO.findByNickname(nickname);
        if (res.size() == 0) return false;
        User tar = res.get(0);
        model.addAttribute("title", nickname + " - imikuLive");
        model.addAttribute("uid", tar.getId());
        model.addAttribute("nickname", nickname);
        model.addAttribute("email", tar.getEmail());
        model.addAttribute("avatar", tar.getAvatar());
        model.addAttribute("room", tar.getRoom());
        model.addAttribute("gend", tar.getGender());
        if (tar.getIntro() != null) model.addAttribute("intro", tar.getIntro());
        else model.addAttribute("intro", "这个人懒得自我介绍~");
        if (tar.getGender() == 1) model.addAttribute("gender", "♂️");
        if (tar.getGender() == 2) model.addAttribute("gender", "♀️");
        if (tar.getGender() == 3) model.addAttribute("gender", "\uD83E\uDD16");
        return true;
    }

    public void loginState(HttpSession session, HashMap<String, Object> ret) {
        if (session.getAttribute("uid") == null) {
            ret.put("result", false);
            return;
        }
        ret.put("result", true);
        ret.put("uid", session.getAttribute("uid"));
        ret.put("email", session.getAttribute("email"));
        ret.put("nickname", session.getAttribute("nickname"));
        ret.put("avatar", session.getAttribute("avatar"));
        ret.put("room", session.getAttribute("room"));
        ret.put("gender", session.getAttribute("gender"));
    }

    public void setAvatar(HttpSession session, MultipartFile file) throws IOException {
        List<User> res = userDAO.findByNickname((String) session.getAttribute("nickname"));
        User tar = res.get(0);
        int innerCode = (int) (System.currentTimeMillis() % 1000000000 + Math.round(Math.random() % 1000000000));
        String filename = DigestUtils.md5DigestAsHex(Integer.toString(innerCode)
                .getBytes(StandardCharsets.UTF_8)).substring(5, 30);
        fileResource.saveAndDeletePrev(file, "/avatars/", filename, tar.getAvatar());
        tar.setAvatar(filename);
        userDAO.save(tar);
        session.setAttribute("avatar", filename);
    }

    public void updateInfo(HttpSession session, Map<String, Object> param, HashMap<String, Object> ret) {
        User tar = userDAO.findById((int) session.getAttribute("uid")).get(0);
        if (!param.get("nickname").equals(session.getAttribute("nickname"))) {
            if (!checkNick((String) param.get("nickname"), ret)) return;
            tar.setNickname((String) param.get("nickname"));
        }
        if (param.containsKey("intro"))
            tar.setIntro((String) param.get("intro"));
        tar.setGender(Integer.parseInt(param.get("gender").toString()));
        userDAO.saveAndFlush(tar);
        session.setAttribute("nickname", param.get("nickname"));
        ret.put("result", true);
    }
}
