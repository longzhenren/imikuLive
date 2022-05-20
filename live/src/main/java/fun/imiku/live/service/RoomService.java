/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import fun.imiku.live.component.PullRouter;
import fun.imiku.live.dao.RoomDAO;
import fun.imiku.live.dao.UserDAO;
import fun.imiku.live.entity.Room;
import fun.imiku.live.entity.User;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class RoomService {
    @Value("${site.files}")
    String localFile;
    @Value("${nms.rtmp}")
    String rtmpUrl;
    @Value("${nms.secret}")
    String rtmpSecret;
    @Autowired
    RoomDAO roomDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    PullRouter pullRouter;

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
        model.addAttribute("open", pullRouter.checkRoomOpen(usr.getRoom()) ? 1 : 0);
        if (tar.getIntro() != null) model.addAttribute("intro", tar.getIntro());
        else model.addAttribute("intro", "主播懒得写简介ヾ(≧▽≦*)o");
        return true;
    }

    public void openRoom(int uid, HttpSession session, HashMap<String, Object> ret) {
        if (uid != (int) session.getAttribute("uid")) {
            ret.put("result", false);
            ret.put("message", "请勿冒充其他用户");
            return;
        }
        List<User> res = userDAO.findById(uid);
        if (res.size() == 0 || res.get(0).getRoom() != 0) {
            ret.put("result", false);
            ret.put("message", "Bad Request");
            return;
        }
        User rst = res.get(0);
        Room tar = new Room();
        int innerCode = (int) (System.currentTimeMillis() % 1000000000 + Math.round(Math.random() % 1000000000));
        String app = DigestUtils.md5DigestAsHex(Integer.toString(innerCode)
                .getBytes(StandardCharsets.UTF_8)).substring(10, 18);
        tar.setName(rst.getNickname() + "的直播间");
        tar.setCover("auto");
        tar.setPri(0);
        tar.setApp(app);
        roomDAO.saveAndFlush(tar);
        rst.setRoom(tar.getId());
        userDAO.saveAndFlush(rst);
        session.setAttribute("room", tar.getId());
        ret.put("result", true);
    }

    public void setCover(HttpSession session, MultipartFile file)
            throws IOException {
        List<User> res = userDAO.findByNickname((String) session.getAttribute("nickname"));
        if (res.size() == 0) throw new IOException();
        Room tar = roomDAO.findById(res.get(0).getRoom()).get(0);
        int innerCode = (int) (System.currentTimeMillis() % 1000000000 + Math.round(Math.random() % 1000000000));
        String filename = DigestUtils.md5DigestAsHex(Integer.toString(innerCode)
                .getBytes(StandardCharsets.UTF_8)).substring(5, 30);
        FileOutputStream fileOutputStream =
                new FileOutputStream(localFile + "/covers/" + filename);
        IOUtils.copy(file.getInputStream(), fileOutputStream);
        fileOutputStream.close();
        tar.setCover(filename);
        roomDAO.save(tar);
    }

    public boolean checkName(String nick, HashMap<String, Object> ret) {
        if (nick.length() < 4 || nick.length() > 15) {
            ret.put("result", false);
            ret.put("message", "名称长度应为 4 到 15 位");
            return false;
        }
        if (nick.contains("/") || nick.contains("\\") || nick.contains("?")) {
            ret.put("result", false);
            ret.put("message", "名称包含非法字符");
            return false;
        }
        List<Room> res = roomDAO.findByName(nick);
        if (res.size() != 0) {
            ret.put("result", false);
            ret.put("message", "该名称已被使用，请更换名称");
            return false;
        }
        ret.put("result", true);
        return true;
    }

    public void updateRoom(HttpSession session, Map<String, Object> param, HashMap<String, Object> ret) {
        Room tar = roomDAO.findById((int) session.getAttribute("room")).get(0);
        if (!param.get("name").equals(tar.getName())) {
            if (!checkName((String) param.get("name"), ret)) return;
            tar.setName((String) param.get("name"));
        }
        if (param.containsKey("intro"))
            tar.setIntro((String) param.get("intro"));
        roomDAO.saveAndFlush(tar);
        ret.put("result", true);
    }

    public void roomOn(int rid, HashMap<String, Object> ret) {
        if (rid == 0 || pullRouter.checkRoomOpen(rid)) {
            ret.put("result", false);
            ret.put("message", "Bad Request");
            return;
        }
        Room tar = roomDAO.findById(rid).get(0);
        if (tar.getPri() == 0)
            if (!pullRouter.addRoomRoute(rid, tar.getApp())) {
                ret.put("result", false);
                ret.put("message", "Internal Server Error");
                return;
            }
        // 使用 unix 时间戳指定有效时间，此处为当前时间 + 2 天，限定了一次直播最长持续 2 天
        String time = Long.toString(System.currentTimeMillis() / 1000L + 3600 * 24 * 2);
        String rStr = "/" + tar.getApp() + "/" + rid + "-" + time + "-" + rtmpSecret;
        tar.setSign(time + "-" + DigestUtils.md5DigestAsHex(rStr.getBytes(StandardCharsets.UTF_8)));
        ret.put("result", true);
        roomDAO.saveAndFlush(tar);
    }

    public void roomOff(int rid, HashMap<String, Object> ret) {
        if (rid == 0 || !pullRouter.checkRoomOpen(rid)) {
            ret.put("result", false);
            ret.put("message", "Bad Request");
            return;
        }
        if (!pullRouter.delRoomRoute(rid)) {
            ret.put("result", false);
            ret.put("message", "Internal Server Error");
            return;
        }
        ret.put("result", true);
    }

    public void getRtmpInfo(int rid, HashMap<String, Object> ret) {
        if (!pullRouter.checkRoomOpen(rid)) {
            ret.put("result", false);
            ret.put("message", "Bad Request");
            return;
        }
        Room tar = roomDAO.findById(rid).get(0);
        ret.put("result", true);
        ret.put("addr", rtmpUrl + "/" + tar.getApp());
        ret.put("key", rid + "?sign=" + tar.getSign());
    }
}
