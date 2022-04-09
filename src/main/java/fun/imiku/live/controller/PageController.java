/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class PageController {
    @RequestMapping("/login")
    public String loginPage(@RequestParam Map<String, Object> param, Model model) {
        if(param.containsKey("e")) model.addAttribute("email", param.get("e"));
        return "login";
    }

    @RequestMapping("/resetpassword")
    public String resetPassword(@RequestParam Map<String, Object> param, Model model) {
        model.addAttribute("email", param.get("e"));
        model.addAttribute("id", param.get("i"));
        return "reset";
    }

    @RequestMapping("/terms")
    public String termsPage() {
        return "terms";
    }
}
