/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.controller;

import fun.imiku.live.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Autowired
    IndexService indexService;

    @PostMapping("/api/serverLoad")
    public String serverLoad() {
        return indexService.getServerLoad();
    }
}
