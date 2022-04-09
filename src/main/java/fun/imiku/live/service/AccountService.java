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
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    @Autowired
    UserDAO userDAO;

    public void checkLogin(String email, String password, HttpSession session, HashMap<String, Object> ret) {
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) {
            ret.put("result", false);
            ret.put("message", "邮箱或密码错误");
            return;
        }
        try {
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
            ret.put("result", true);
        } catch (Exception e) {
            ret.put("result", false);
            ret.put("message", "内部服务器错误");
        }
    }

    public void forget(String email, HashMap<String, Object> ret){
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) {
            ret.put("result", false);
            ret.put("message", "邮箱地址不存在");
            return;
        }
        User tar = res.get(0);
        int innerCode = -(int)(System.currentTimeMillis() % 1000000000 + Math.round(Math.random() % 1000000000));
        tar.setInnerCode(innerCode);
        userDAO.save(tar);
        // TODO: 发邮件

        ret.put("result", true);
    }
}
