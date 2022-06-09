/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;

@Component
public class PullRouter {
    // 拉流路由器，通过 gateway 实现拉流鉴权并以路由的方式进行流地址保护。通过 Actuator API 向其传参
    @Autowired
    RestTemplate restTemplate;
    @Value("${site.gatewayUrl}")
    String gatewayUrl;
    @Value("${nms.http}")
    String nmsHttpUrl;

    // 并不持久化房间的开启情况，在每次重启时所有房间默认关闭
    private final HashSet<Integer> roomsOpen = new HashSet<>();
    private static final String addRouteReq = "{\"id\":\"@RID\",\"predicates\":[{\"name\":\"Path\",\"args\":{\"_gen" +
            "key_0\":\"/stream/@RID/**\"}}],\"filters\":[{\"name\":\"StripPrefix\",\"args\":{\"_genkey_0\":\"2\"}}," +
            "{\"name\":\"PrefixPath\",\"args\":{\"_genkey_0\":\"/@APP\"}}],\"uri\":\"@URL\",\"order\":0}";
    private static final HttpHeaders headers = new HttpHeaders();

    static {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public boolean checkRoomOpen(int t) {
        return roomsOpen.contains(t);
    }

    private boolean flushCache() {
        ResponseEntity<String> res = restTemplate.postForEntity(gatewayUrl + "/actuator/gateway/refresh",
                null, String.class);
        return res.getStatusCodeValue() == 200;
    }

    public boolean addRoomRoute(int rid, String app) {
        String con = addRouteReq.replace("@RID", Integer.toString(rid)).replace("@URL", nmsHttpUrl)
                .replace("@APP", app);
        HttpEntity<String> request = new HttpEntity<>(con, headers);
        ResponseEntity<String> res = restTemplate.postForEntity(gatewayUrl + "/actuator/gateway/routes/" + rid,
                request, String.class);
        if (res.getStatusCodeValue() != 201)
            return false;
        if (flushCache()) {
            roomsOpen.add(rid);
            return true;
        }
        return false;
    }

    public boolean delRoomRoute(int rid) {
        ResponseEntity<String> res = restTemplate.exchange(gatewayUrl + "/actuator/gateway/routes/" + rid,
                HttpMethod.DELETE, null, String.class);
        if (res.getStatusCodeValue() != 200)
            return false;
        if (flushCache()) {
            roomsOpen.remove(rid);
            return true;
        }
        return false;
    }

    public HashSet<Integer> getInActiveRooms(HashSet<Integer> active) {
        HashSet<Integer> ret = (HashSet) roomsOpen.clone();
        ret.removeAll(active);
        return ret;
    }
}
