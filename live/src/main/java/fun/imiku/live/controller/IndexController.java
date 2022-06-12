/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.imiku.live.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/api/serverLoad")
    public String serverLoad() {
        return indexService.getServerLoad();
    }

    @GetMapping("/api/getOpenRoomsPaged")
    public String getOpenRoomsPaged(@RequestParam Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            indexService.getRoomsByOpenPaged(1, Integer.parseInt((String) param.get("page")), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }

    @GetMapping("/api/getCloseRoomsPaged")
    public String getCloseRoomsPaged(@RequestParam Map<String, Object> param) {
        HashMap<String, Object> ret = new HashMap<>();
        try {
            indexService.getRoomsByOpenPaged(0, Integer.parseInt((String) param.get("page")), ret);
            return objectMapper.writeValueAsString(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"Bad Request\"}";
        }
    }
}
