/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import fun.imiku.live.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;

    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }

    @RequestMapping("/api/login")
    public String login(HttpServletRequest request, Map<String, Object> map, HttpSession session) {
        if (loginService.checkLogin(request.getParameter("email"), request.getParameter("password"), session)) {
            // cookies?
            return "index";
        } else {
            map.put("msg", "邮箱或密码错误");
            //由于此处不是重定向，所以相当于根据字符串直接去templates下找login.html
            //所以不能写成返回"/"或者"/index.html",否则会报找不到页面
            return "login";
        }
    }
}
