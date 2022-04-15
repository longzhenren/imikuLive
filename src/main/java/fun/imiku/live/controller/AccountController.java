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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/forgot")
    public String forgot(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.forget((String) param.get("email"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/resetPassword")
    public String reset(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.reset((String) param.get("e"), Integer.parseInt((String) param.get("i")),
                    (String) param.get("p"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
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
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/checkEmail")
    public String checkEmail(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.checkEmail((String) param.get("email"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @GetMapping("/api/getUserByNickname")
    public String getUser(@RequestBody Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.getByNickname((String) param.get("name"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @GetMapping("/api/loginState")
    public String loginState(HttpSession session) {
        // 网站功能简单，不使用 Cookie
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.loginState(session, ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/logout")
    public String logout(HttpSession session) {
        try {
            session.invalidate();
            return "{\"result\":true}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/setAvatar")
    public String setAvatar(HttpSession session, @RequestParam("file") MultipartFile file) {
        try {
            accountService.setAvatar(session, file);
            return "{\"result\":true}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/updateInfo")
    public String updateInfo(HttpSession session, @RequestBody Map<String, Object> param) {
        if (session.getAttribute("uid") == null || (int) session.getAttribute("uid") != (int) param.get("uid"))
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        HashMap<String, Object> ret = new HashMap<>();
        try {
            accountService.updateInfo(session, param, ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }
}
