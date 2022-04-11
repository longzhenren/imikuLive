/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.imiku.live.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class AccountController {
    @Autowired
    AccountService accountService;
    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/api/login")
    public String login(@RequestBody Map<String, Object> param, HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.checkLogin((String) param.get("email"), (String) param.get("password"),
                    (String) param.get("ip"), session, ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }

    @PostMapping("/api/forgot")
    public String forgot(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.forget((String) param.get("email"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }

    @PostMapping("/api/resetpassword")
    public String reset(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.reset((String) param.get("e"), Integer.parseInt((String) param.get("i")),
                    (String) param.get("p"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }

    @PostMapping("/api/register")
    public String register(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.register((String) param.get("email"), (String) param.get("password"),
                    (String) param.get("nickname"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }

    @PostMapping("/api/checkemail")
    public String checkEmail(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.checkEmail((String) param.get("email"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }
}
