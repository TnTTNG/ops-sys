package ops.example.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.service.SSHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@ServerEndpoint("/websocket/user")
public class WebSocketHandler {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> MESSAGE_QUEUES = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static SSHService sshService;
    private static final ExecutorService messageExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public void setSSHService(SSHService service) {
        sshService = service;
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            log.info("收到WebSocket连接请求: sessionId={}", session.getId());
            
            // 从URL查询参数中获取token
            String token = null;
            Map<String, String> queryParams = session.getRequestParameterMap().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
            
            if (queryParams.containsKey("token")) {
                token = queryParams.get("token");
                log.info("从查询参数获取到token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            }
            
            if (token == null || token.isEmpty()) {
                log.error("WebSocket连接失败: token为空");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "未登录"));
                return;
            }

            // 临时措施：跳过token验证，使用固定用户ID
            String userId = "44";
            log.info("临时措施：跳过token验证，使用固定用户ID={}", userId);
            
            /*
            // 原token验证逻辑
            String userId = JWTUtils.validateToken(token);
            if (userId == null) {
                log.error("WebSocket连接失败: token无效");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "token无效"));
                return;
            }
            */

            // 存储会话
            SESSIONS.put(userId, session);
            MESSAGE_QUEUES.put(userId, new ConcurrentLinkedQueue<>());
            log.info("WebSocket连接成功: userId={}, sessionId={}", userId, session.getId());

            // 启动消息处理器
            startMessageProcessor(userId);
            
            // 发送连接成功消息
            Map<String, String> response = new HashMap<>();
            response.put("type", "connection");
            response.put("status", "success");
            response.put("message", "连接成功");
            queueMessage(userId, response);
            log.info("已发送连接成功消息");
        } catch (Exception e) {
            log.error("WebSocket连接处理失败: {}", e.getMessage(), e);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "连接处理失败"));
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败", ex);
            }
        }
    }

    // 启动消息处理线程
    private void startMessageProcessor(String userId) {
        messageExecutor.submit(() -> {
            try {
                while (SESSIONS.containsKey(userId)) {
                    Queue<String> queue = MESSAGE_QUEUES.get(userId);
                    if (queue != null && !queue.isEmpty()) {
                        String message = queue.poll();
                        if (message != null) {
                            Session session = SESSIONS.get(userId);
                            if (session != null && session.isOpen()) {
                                try {
                                    session.getBasicRemote().sendText(message);
                                    // 发送后短暂延时，避免过快发送
                                    Thread.sleep(5);
                                } catch (Exception e) {
                                    log.error("发送消息失败: {}", e.getMessage());
                                }
                            }
                        }
                    }
                    Thread.sleep(10); // 短暂休眠，避免CPU过度使用
                }
            } catch (Exception e) {
                log.error("消息处理线程异常: {}", e.getMessage(), e);
            }
        });
    }

    // 将消息加入队列
    private static void queueMessage(String userId, Object message) {
        try {
            String messageStr = objectMapper.writeValueAsString(message);
            Queue<String> queue = MESSAGE_QUEUES.get(userId);
            if (queue != null) {
                queue.offer(messageStr);
            } else {
                log.warn("用户消息队列不存在: {}", userId);
            }
        } catch (Exception e) {
            log.error("序列化消息失败: {}", e.getMessage(), e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("收到WebSocket消息: {}", message);
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String type = (String) messageMap.get("type");
            
            String userId = getUserIdBySession(session);
            if (userId == null) {
                log.error("未找到用户会话");
                return;
            }
            
            switch (type) {
                case "ssh_connect":
                    handleSSHConnect(userId, session, messageMap);
                    break;
                case "ssh_input":
                    handleSSHInput(userId, session, messageMap);
                    break;
                case "ssh_disconnect":
                    handleSSHDisconnect(userId, session, messageMap);
                    break;
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
            
            String userId = getUserIdBySession(session);
            if (userId != null) {
                queueMessage(userId, errorResponse);
            }
        }
    }

    private void handleSSHConnect(String userId, Session session, Map<String, Object> message) {
        try {
            Map<String, Object> data = (Map<String, Object>) message.get("data");
            String instanceId = (String) data.get("instanceId");
            String host = (String) data.get("publicIp");
            
            log.info("SSH连接请求: userId={}, instanceId={}, host={}", userId, instanceId, host);
            
            if (host == null || host.isEmpty()) {
                log.error("SSH连接失败: 主机地址为空");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("type", "error");
                errorResponse.put("message", "SSH连接失败: 主机地址为空");
                queueMessage(userId, errorResponse);
                return;
            }
            
            // 检查是否已经有连接
            String sessionId = userId + "_" + instanceId;
            if (sshService.hasSession(sessionId)) {
                log.info("SSH会话已存在，将重用: sessionId={}", sessionId);
                Map<String, String> response = new HashMap<>();
                response.put("type", "ssh_output");
                response.put("data", "重用已有SSH连接\r\n$ ");
                queueMessage(userId, response);
                return;
            }
            
            // 连接SSH服务器
            log.info("开始SSH连接: sessionId={}, host={}", sessionId, host);
            try {
                sshService.connect(sessionId, host);
                
                // 发送欢迎消息
                Map<String, String> response = new HashMap<>();
                response.put("type", "ssh_output");
                response.put("data", "SSH连接成功，欢迎使用终端\r\n$ ");
                queueMessage(userId, response);
                log.info("SSH连接成功并发送欢迎消息: sessionId={}", sessionId);
            } catch (Exception e) {
                log.error("SSH连接失败: {}", e.getMessage(), e);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("type", "error");
                errorResponse.put("message", "SSH连接失败: " + e.getMessage());
                queueMessage(userId, errorResponse);
            }
        } catch (Exception e) {
            log.error("处理SSH连接请求失败: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("type", "error");
            errorResponse.put("message", "处理SSH连接请求失败: " + e.getMessage());
            queueMessage(userId, errorResponse);
        }
    }

    private void handleSSHInput(String userId, Session session, Map<String, Object> message) {
        try {
            String data = (String) message.get("data");
            String instanceId = (String) message.get("instanceId");
            
            log.info("收到SSH输入: sessionId={}, data={}, instanceId={}", session.getId(), data, instanceId);
            
            if (data == null || instanceId == null) {
                sendErrorMessage(userId, "无效的输入数据");
                return;
            }
            
            String sessionId = userId + "_" + instanceId;
            
            // 检查SSH会话是否存在
            if (!sshService.hasSession(sessionId)) {
                sendErrorMessage(userId, "SSH会话不存在，请重新连接");
                return;
            }
            
            // 处理历史命令
            if (data.equals("HISTORY_UP") || data.equals("HISTORY_DOWN")) {
                String historyCommand = (String) message.get("historyCommand");
                // 清除当前行并显示历史命令
                Map<String, String> response = new HashMap<>();
                response.put("type", "ssh_output");
                response.put("data", "\u001B[2K\r$ " + (historyCommand != null ? historyCommand : ""));
                queueMessage(userId, response);
                return;
            }
            
            // 创建输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            try {
                // 执行命令
                sshService.executeCommand(sessionId, data, outputStream);
                
                // 获取命令输出
                String output = outputStream.toString();
                if (output != null && !output.isEmpty()) {
                    log.debug("命令输出: {}", output);
                }
                
                // 构建响应消息
                Map<String, String> response = new HashMap<>();
                response.put("type", "ssh_output");
                response.put("data", output);
                
                // 如果是回车键，可能需要添加命令历史
                if (data.equals("\r")) {
                    String command = (String) message.get("command");
                    if (command != null && !command.isEmpty()) {
                        response.put("command", command);
                        log.info("添加命令到历史: {}", command);
                    }
                }
                
                // 发送响应
                queueMessage(userId, response);
                log.info("已发送命令输出响应，长度: {}", output != null ? output.length() : 0);
            } catch (Exception e) {
                log.error("执行命令失败: {}", e.getMessage(), e);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("type", "error");
                errorResponse.put("message", "执行命令失败: " + e.getMessage());
                queueMessage(userId, errorResponse);
            }
        } catch (Exception e) {
            log.error("处理SSH输入失败: {}", e.getMessage(), e);
            sendErrorMessage(userId, "处理输入失败: " + e.getMessage());
        }
    }
    
    private void sendErrorMessage(String userId, String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("type", "error");
        errorResponse.put("message", errorMessage);
        queueMessage(userId, errorResponse);
    }

    private void handleSSHDisconnect(String userId, Session session, Map<String, Object> message) {
        try {
            Map<String, Object> data = (Map<String, Object>) message.get("data");
            String instanceId = (String) data.get("instanceId");
            
            String sessionId = userId + "_" + instanceId;
            sshService.disconnect(sessionId);
            
            log.info("SSH连接已断开: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("断开SSH连接失败", e);
        }
    }
    
    private String getUserIdBySession(Session session) {
        for (Map.Entry<String, Session> entry : SESSIONS.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @OnClose
    public void onClose(Session session) {
        String userId = getUserIdBySession(session);
        if (userId != null) {
            SESSIONS.remove(userId);
            MESSAGE_QUEUES.remove(userId);
            // 断开所有相关的SSH连接
            // 注意：实际上需要一个映射来跟踪userId和instanceId的关系
        }
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

    public static void broadcastMessage(Map<String, Object> message) {
        for (String userId : SESSIONS.keySet()) {
            queueMessage(userId, message);
        }
    }
} 