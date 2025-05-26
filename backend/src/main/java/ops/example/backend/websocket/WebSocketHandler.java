package ops.example.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.common.JWTUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/websocket/user")
public class WebSocketHandler {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.error("WebSocket连接失败: token为空");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "未登录"));
                return;
            }

            // 验证token
            String userId = JWTUtils.validateToken(token);
            if (userId == null) {
                log.error("WebSocket连接失败: token无效");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "token无效"));
                return;
            }

            // 存储会话
            SESSIONS.put(userId, session);
            log.info("WebSocket连接成功: userId={}", userId);

            // 发送连接成功消息
            Map<String, String> response = new HashMap<>();
            response.put("type", "connection");
            response.put("status", "success");
            response.put("message", "连接成功");
            sendMessage(session, response);
        } catch (Exception e) {
            log.error("WebSocket连接处理失败", e);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "连接处理失败"));
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败", ex);
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("收到WebSocket消息: {}", message);
            // 处理接收到的消息
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String type = (String) messageMap.get("type");
            
            // 根据消息类型处理
            switch (type) {
                case "broadcast":
                    broadcastMessage(messageMap);
                    break;
                default:
                    log.warn("未知的消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("type", "error");
            errorResponse.put("message", "消息处理失败");
            sendMessage(session, errorResponse);
        }
    }

    @OnClose
    public void onClose(Session session) {
        // 移除会话
        SESSIONS.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("WebSocket连接关闭");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket错误", error);
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "发生错误"));
        } catch (IOException e) {
            log.error("关闭WebSocket连接失败", e);
        }
    }

    private static void sendMessage(Session session, Object message) {
        try {
            String messageStr = objectMapper.writeValueAsString(message);
            session.getBasicRemote().sendText(messageStr);
        } catch (IOException e) {
            log.error("发送WebSocket消息失败", e);
        }
    }

    public static void broadcastMessage(Map<String, Object> message) {
        String messageStr;
        try {
            messageStr = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("序列化广播消息失败", e);
            return;
        }

        SESSIONS.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(messageStr);
            } catch (IOException e) {
                log.error("广播消息失败", e);
            }
        });
    }
} 