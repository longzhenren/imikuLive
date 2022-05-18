/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.imiku.live.service.RoomService;
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
public class RoomController {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RoomService roomService;

    @PostMapping("/api/openRoom")
    public String openRoom(@RequestBody Map<String, Object> param, HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            roomService.openRoom((int) param.get("uid"), (int) session.getAttribute("uid"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }
}
