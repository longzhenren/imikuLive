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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            roomService.openRoom((int) param.get("uid"), session, ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/setCover")
    public String setCover(HttpSession session, @RequestParam("file") MultipartFile file) {
        try {
            roomService.setCover(session, file);
            return "{\"result\":true}";
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/updateRoom")
    public String updateRoom(HttpSession session, @RequestBody Map<String, Object> param) {
        if (session.getAttribute("uid") == null || (int) session.getAttribute("uid") != (int) param.get("uid"))
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        HashMap<String, Object> ret = new HashMap<>();
        try {
            roomService.updateRoom(session, param, ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/roomOn")
    public String roomOn(HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        if (session.getAttribute("uid") == null)
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        try {
            roomService.roomOn((int) session.getAttribute("room"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @PostMapping("/api/roomOff")
    public String roomOff(HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        if (session.getAttribute("uid") == null)
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        try {
            roomService.roomOff((int) session.getAttribute("room"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @GetMapping("/api/getRtmpInfo")
    public String getRtmpInfo(HttpSession session) {
        HashMap<String, Object> ret = new HashMap<>();
        if (session.getAttribute("uid") == null)
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        try {
            roomService.getRtmpInfo((int) session.getAttribute("room"), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }
}
