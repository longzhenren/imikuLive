/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.service;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

@Service
public class SocketIOService {
    @Autowired
    SocketIOServer socketIOServer;
    private final HashMap<SocketIOClient, String> roomMap = new HashMap<>();
    private final HashMap<SocketIOClient, Integer> duplicateClients = new HashMap<>();

    @PostConstruct
    private void autoStartup() {
        socketIOServer.start();
    }

    @PreDestroy
    private void autoStop() {
        socketIOServer.stop();
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String id = client.getHandshakeData().getSingleUrlParam("room");
        client.joinRoom(id);
        roomMap.put(client, id);
        if (duplicateClients.containsKey(client))
            duplicateClients.put(client, duplicateClients.get(client) + 1);
        else
            duplicateClients.put(client, 1);
        BroadcastOperations room = socketIOServer.getRoomOperations(id);
        room.sendEvent("audience_num", room.getClients().size());
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        if (duplicateClients.get(client) == 1) {
            BroadcastOperations room = socketIOServer.getRoomOperations(roomMap.get(client));
            room.sendEvent("audience_num", room.getClients().size());
            roomMap.remove(client);
            duplicateClients.remove(client);
        } else {
            duplicateClients.put(client, duplicateClients.get(client) - 1);
        }
    }

    @OnEvent(value = "danmaku")
    public void danmaku(SocketIOClient client, AckRequest request, String data) {
        socketIOServer.getRoomOperations(roomMap.get(client)).sendEvent("danmaku", client, data);
    }

    public void openRoom(int rid) {
        socketIOServer.getRoomOperations(Integer.toString(rid)).sendEvent("open");
    }

    public void closeRoom(int rid) {
        socketIOServer.getRoomOperations(Integer.toString(rid)).sendEvent("close");
    }
}
