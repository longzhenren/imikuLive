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
import java.util.List;

@Service
public class LoginService {
    @Autowired
    UserDAO userDAO;

    public boolean checkLogin(String email, String password, HttpSession session){
        List<User> res = userDAO.findByEmail(email);
        if (res.size() == 0) return false;
        try {
            String bufPass = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)).substring(5, 29);
            if (!res.get(0).getPassword().equals(bufPass)) return false;
            User tar = res.get(0);
            session.setAttribute("id", tar.getId());
            session.setAttribute("nickname", tar.getNickname());
            session.setAttribute("avatar", tar.getAvatar());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
