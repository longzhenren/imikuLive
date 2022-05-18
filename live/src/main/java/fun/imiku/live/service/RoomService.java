/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import fun.imiku.live.dao.RoomDAO;
import fun.imiku.live.dao.UserDAO;
import fun.imiku.live.entity.Room;
import fun.imiku.live.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    RoomDAO roomDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HashSet<Integer> roomsOpen;

    public boolean pageByNickname(String nick, Model model) {
        List<User> res = userDAO.findByNickname(nick);
        if (res.size() == 0) return false;
        if (res.get(0).getRoom() == 0) return false;
        List<Room> rst = roomDAO.findById(res.get(0).getRoom());
        if (rst.size() == 0) return false;
        Room tar = rst.get(0);
        User usr = res.get(0);
        model.addAttribute("title", tar.getName() + " - imikuLive");
        model.addAttribute("nickname", usr.getNickname());
        model.addAttribute("gend", usr.getGender());
        model.addAttribute("avatar", usr.getAvatar());
        model.addAttribute("uid", usr.getId());
        model.addAttribute("rid", usr.getRoom());
        model.addAttribute("name", tar.getName());
        model.addAttribute("cover", tar.getCover());
        model.addAttribute("open", roomsOpen.contains(usr.getRoom()) ? 1 : 0);
        if (tar.getIntro() != null)
            model.addAttribute("intro", tar.getIntro());
        return true;
    }

    public void openRoom(int uid, int sid, HashMap<String, Object> ret) {
        if (uid != sid) {
            ret.put("result", false);
            ret.put("message", "请勿冒充其他用户");
            return;
        }
        List<User> res =  userDAO.findById(uid);
        if(res.size() == 0 || res.get(0).getRoom() != 0) {
            ret.put("result", false);
            ret.put("message", "Bad Request");
            return;
        }
        User rst = res.get(0);
        Room tar = new Room();
        tar.setName(rst.getNickname() + "的直播间");
        tar.setCover("auto");
        tar.setPri(0);
        roomDAO.saveAndFlush(tar);
        rst.setRoom(tar.getId());
        userDAO.saveAndFlush(rst);
        ret.put("result", true);
    }
}
