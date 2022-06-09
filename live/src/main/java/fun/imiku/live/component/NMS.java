/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.component;

import fun.imiku.live.service.RoomService;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;

@Component
@EnableScheduling
@EnableAsync
public class NMS {
    @Lazy
    @Autowired
    RestTemplate restTemplate;
    @Lazy
    @Autowired
    ServerLoad serverLoad;
    @Lazy
    @Autowired
    RoomService roomService;
    @Value("${nms.api_user}")
    String apiUser;
    @Value("${nms.api_pass}")
    String apiPass;
    @Value("${nms.http}")
    String nmsHttpUrl;
    @Value("${nms.speed_up}")
    float nmsSpeedUp;
    @Value("${nms.speed_down}")
    float nmsSpeedDn;
    private HttpEntity<String> httpEntityWithBasicAuth;
    private final HashSet<Integer> roomsActive = new HashSet<>();

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ServerLoad serverLoad() {
        return new ServerLoad();
    }

    public String getServerLoad() {
        return "{\"cpu\":\"" + serverLoad.getCpuLoad() + "\",\"net\":\"" + serverLoad.getNetLoad() + "\"}";
    }

    private HttpEntity<String> httpEntityWithBasicAuth() {
        if (httpEntityWithBasicAuth == null)
            httpEntityWithBasicAuth = new HttpEntity<>(new HttpHeaders() {{
                String auth = apiUser + ":" + apiPass;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }});
        return httpEntityWithBasicAuth;
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 2)
    public void reqServerLoad() {
        // 每 2s 存储一次服务器负载，来自客户端的轮询直接取值，而不是重复调用 nms 的 api
        try {
            ResponseEntity<Map> result = restTemplate.exchange
                    (nmsHttpUrl + "/api/server", HttpMethod.GET, httpEntityWithBasicAuth(), Map.class);
            if (result.getStatusCodeValue() == 200) {
                serverLoad.setCpuLoad(((Map) result.getBody().get("cpu")).get("load") + "%");
                Long now = ((Integer) ((Map) result.getBody().get("net")).get("outbytes")).longValue();
                float loadUp = (now - serverLoad.getOutBytes()) / nmsSpeedUp / 2500;
                serverLoad.setOutBytes(now);
                now = ((Integer) ((Map) result.getBody().get("net")).get("inbytes")).longValue();
                float loadDn = (now - serverLoad.getInBytes()) / nmsSpeedDn / 2500;
                serverLoad.setInBytes(now);
                serverLoad.setNetLoad((loadDn > loadUp ? Math.round(loadDn) : Math.round(loadUp)) + "%");
            }
        } catch (Exception ignored) {
        }
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60 * 3)
    public void reqStream() {
        // 每 3m 存储一次现有的推流，如果开启的房间连续两次没有对应的推流，则关闭该房间
        try {
            ResponseEntity<Map> result = restTemplate.exchange
                    (nmsHttpUrl + "/api/streams", HttpMethod.GET, httpEntityWithBasicAuth(), Map.class);
            if (result.getStatusCodeValue() == 200) {
                roomsActive.clear();
                for (Object i : result.getBody().values()) {
                    int rid = Integer.parseInt((String) ((Map) i).keySet().iterator().next());
                    if (((Map) ((Map) i).values().iterator().next()).get("publisher") != null)
                        roomsActive.add(rid);
                }
                roomService.closeInActiveRooms(roomsActive);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Data
class ServerLoad {
    private String cpuLoad;
    private String netLoad;
    private Long inBytes = 0L;
    private Long outBytes = 0L;
}