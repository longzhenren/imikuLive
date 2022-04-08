/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.imiku.live.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
public class AccountController {
    @Autowired
    LoginService loginService;
    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/api/login")
    public String login(HttpServletRequest request, HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        loginService.checkLogin(request.getParameter("email"), request.getParameter("password"), session,ret);
        try {
            return objectMapper.writeValueAsString(ret);
        }catch (Exception e){
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }
}
