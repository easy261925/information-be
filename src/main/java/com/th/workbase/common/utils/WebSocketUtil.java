package com.th.workbase.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date 2021-03-27-15:46
 * @Author tangJ
 * @Description
 * @Version 1.0
 */

@ServerEndpoint("/webSocket/{username}")
@Slf4j
@Component
public class WebSocketUtil {

    private static int onlineCount = 0;
    private static final Map<String, WebSocketUtil> clients = new ConcurrentHashMap<>();
    private Session session;
    private String username;

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {
        this.username = username;
        this.session = session;
        WebSocketUtil.onlineCount++;
        clients.put(username, this);
    }

    @OnClose
    public void onClose() {
        clients.remove(username);
        WebSocketUtil.onlineCount--;
    }

    @OnMessage
    public void onMessage(String message,Session session) {
        log.info(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket发生错误：" + throwable.getMessage());
    }

    public static void sendMessageToAll(String message) {
        // 向所有连接websocket的客户端发送消息
        // 可以修改为对某个客户端发消息
        for (WebSocketUtil item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    public static boolean sendMessageToSingle(String message, String target) {
        WebSocketUtil webSocketUtil = clients.get(target);
        if (webSocketUtil != null) {
            webSocketUtil.session.getAsyncRemote().sendText(message);
            return true;
        }
        return false;
    }

}

