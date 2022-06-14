/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import fun.imiku.live.component.NMS;
import fun.imiku.live.dao.RoomDAO;
import fun.imiku.live.dao.UserDAO;
import fun.imiku.live.entity.Room;
import fun.imiku.live.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class IndexService {
    @Autowired
    NMS nms;
    @Autowired
    RoomDAO roomDAO;
    @Autowired
    UserDAO userDAO;

    public String getServerLoad() {
        return nms.getServerLoad();
    }

    public void getRoomsByOpenPaged(int open, int page, HashMap<String, Object> ret) {
        // 此处限定了一页 6 个房间
        Page<Room> res = roomDAO.findByOpen(open, PageRequest.of(page, 6));
        ret.put("more", res.hasNext());
        List<HashMap<String, Object>> room = new ArrayList<>();
        for (Room i : res.getContent()) {
            HashMap<String, Object> x = new HashMap<>();
            User aut = userDAO.findByRoom(i.getId()).get(0);
            x.put("nickname", aut.getNickname());
            x.put("avatar", aut.getAvatar());
            x.put("name", i.getName());
            x.put("cover", i.getCover());
            x.put("rid", i.getId());
            x.put("uid", aut.getId());
            room.add(x);
        }
        ret.put("room", room);
        ret.put("total", res.getTotalElements());
        ret.put("result", true);
    }

    public void searchRoomsPaged(int page, String key, HashMap<String, Object> ret) {
        // 此处限定了一页 6 个房间
        Page<Room> res = roomDAO.searchByKeyword(key, PageRequest.of(page, 6));
        ret.put("more", res.hasNext());
        List<HashMap<String, Object>> room = new ArrayList<>();
        for (Room i : res.getContent()) {
            HashMap<String, Object> x = new HashMap<>();
            User aut = userDAO.findByRoom(i.getId()).get(0);
            x.put("nickname", aut.getNickname());
            x.put("avatar", aut.getAvatar());
            x.put("name", i.getName());
            x.put("cover", i.getCover());
            x.put("rid", i.getId());
            x.put("uid", aut.getId());
            x.put("open", i.getOpen());
            room.add(x);
        }
        ret.put("room", room);
        ret.put("total", res.getTotalElements());
        ret.put("result", true);
    }

    public void searchUsersPaged(int page, String key, HashMap<String, Object> ret) {
        // 此处限定了一页 6 个用户
        Page<User> res = userDAO.searchByKeyword(key, PageRequest.of(page, 6));
        ret.put("more", res.hasNext());
        List<HashMap<String, Object>> user = new ArrayList<>();
        for (User i : res.getContent()) {
            HashMap<String, Object> x = new HashMap<>();
            x.put("nickname", i.getNickname());
            x.put("avatar", i.getAvatar());
            x.put("uid", i.getId());
            user.add(x);
        }
        ret.put("user", user);
        ret.put("total", res.getTotalElements());
        ret.put("result", true);
    }
}
