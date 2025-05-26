package ops.example.backend.controller;

// import ops.example.backend.service.WebSocketService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.common.Result;
import ops.example.backend.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-26-18:44
 */
@Slf4j
@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/broadcastUserInfo")
    public Result broadcastUserInfo(@RequestBody Map<String, Object> userInfo) {
        try {
            // 构建广播消息
            Map<String, Object> message = new HashMap<>();
            message.put("type", "broadcast");
            message.put("data", userInfo);
            
            // 发送广播消息
            WebSocketHandler.broadcastMessage(message);
            return Result.success("广播消息发送成功");
        } catch (Exception e) {
            log.error("广播消息失败", e);
            return Result.error("广播消息失败");
        }
    }
}
