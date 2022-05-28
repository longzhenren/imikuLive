/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import fun.imiku.live.service.AccountService;
import fun.imiku.live.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@Controller
public class PageController {
    @Autowired
    AccountService accountService;
    @Autowired
    RoomService roomService;

    @RequestMapping("/login")
    public String loginPage(@RequestParam Map<String, Object> param, Model model) {
        if (param.containsKey("e")) model.addAttribute("email", param.get("e"));
        return "login";
    }

    @RequestMapping("/register")
    public String registerPage(@RequestParam Map<String, Object> param, Model model) {
        if (param.containsKey("e")) model.addAttribute("email", param.get("e"));
        return "register";
    }

    @RequestMapping("/resetPassword")
    public String resetPasswordPage(@RequestParam Map<String, Object> param, Model model) {
        model.addAttribute("email", param.get("e"));
        model.addAttribute("id", param.get("i"));
        return "reset";
    }

    @RequestMapping("/confirm")
    public String confirmPage(@RequestParam Map<String, Object> param, Model model) {
        if (accountService.confirm((String) param.get("e"), Integer.parseInt((String) param.get("i")))) {
            model.addAttribute("email", param.get("e"));
            return "confirm";
        }
        return "redirect:/error/400";
    }

    @RequestMapping("/terms")
    public String termsPage() {
        return "terms";
    }

    @RequestMapping("/u/**")
    public String userPage(HttpServletRequest request, Model model, HttpSession session) throws UnsupportedEncodingException {
        String nick = URLDecoder.decode(request.getRequestURI().substring(3), "utf-8");
        if (accountService.pageByNickname(nick, model)) {
            if (nick.equals(session.getAttribute("nickname")))
                return "self";
            return "user";
        }
        return "/error/404";
    }

    @RequestMapping("/r/**")
    public String userRoom(HttpServletRequest request, Model model, HttpSession session) throws UnsupportedEncodingException {
        String nick = URLDecoder.decode(request.getRequestURI().substring(3), "utf-8");
        if (roomService.pageByNickname(nick, model))
            return "room";
        if (nick.equals(session.getAttribute("nickname"))) {
            model.addAttribute("uid", session.getAttribute("uid"));
            model.addAttribute("nickname", nick);
            return "open";
        }
        return "/error/404";
    }

    @RequestMapping("/c/**")
    public String configRoom(HttpServletRequest request, Model model, HttpSession session) throws UnsupportedEncodingException {
        String nick = URLDecoder.decode(request.getRequestURI().substring(3), "utf-8");
        if (!nick.equals(session.getAttribute("nickname")))
            return "/error/404";
        if (roomService.pageByNickname(nick, model))
            return "conf";
        return "/error/404";
    }
}
