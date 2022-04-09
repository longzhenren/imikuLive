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
    public String login(@RequestBody Map<String,Object> param, HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        accountService.checkLogin((String)param.get("email"), (String)param.get("password"), session, ret);
        try {
            return objectMapper.writeValueAsString(ret);
        }catch (Exception e){
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }

    @PostMapping("/api/forgot")
    public String forgot(@RequestBody Map<String,Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        accountService.forget((String)param.get("email"), ret);
        try {
            return objectMapper.writeValueAsString(ret);
        }catch (Exception e){
            return "{\"result\":false;\"message\":\"内部服务器错误\";}";
        }
    }
}
