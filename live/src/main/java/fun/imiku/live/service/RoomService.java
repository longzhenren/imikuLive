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

import java.util.List;

@Service
public class RoomService {
    @Autowired
    RoomDAO roomDAO;
    @Autowired
    UserDAO userDAO;

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
        model.addAttribute("avatar", usr.getAvatar());
        model.addAttribute( "rid", usr.getRoom());
        model.addAttribute("name", tar.getName());
        model.addAttribute("cover", tar.getCover());
        model.addAttribute("intro", tar.getIntro());
        model.addAttribute("open", tar.getOpen());
        return true;
    }
}
