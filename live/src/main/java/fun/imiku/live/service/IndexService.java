/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import fun.imiku.live.component.NMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexService {
    @Autowired
    NMS nms;

    public String getServerLoad() {
        return nms.getServerLoad();
    }
}
