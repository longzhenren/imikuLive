/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import fun.imiku.live.dao.UserDAO;
import fun.imiku.live.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    JavaMailSender mailSender;

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
        session.setAttribute("id", tar.getId());
        session.setAttribute("nickname", tar.getNickname());
        session.setAttribute("avatar", tar.getAvatar());
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
        String text = "<body style=\"margin:0;padding:0\"><style>" +
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
                "border-radius:4px;cursor:pointer;width:36%;letter-spacing:8px\"><a href=\"LLLLlink\" target=" +
                "\"_blank\" style=\"text-decoration:none;color:inherit\">重设密码</a></div></div><div class=" +
                "\"mail-tip\" style=\"color:#aaa;padding:15px 0\">*如果这不是您本人执行的操作，请忽略此邮件</div></div>" +
                "</div></table></td></tr></table></body>";
        text = text.replace("LLLLlink",
                "http://localhost:7004/resetpassword?e=" + email + "&i=" + -innerCode);
        helper.setText(text, true);
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
        if (tar.getInnerCode() != -id) {
            ret.put("result", false);
            ret.put("message", "请勿冒充其他用户");
            return;
        }
        tar.setInnerCode(0);
        tar.setPassword(DigestUtils.md5DigestAsHex(pass.getBytes(StandardCharsets.UTF_8)).substring(5, 29));
        userDAO.saveAndFlush(tar);
        ret.put("result", true);
    }
}