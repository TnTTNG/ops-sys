package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.aliyun.common.Result;
import ops.example.backend.aliyun.service.InstanceVncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-02-1:43
 */

@RestController
@RequestMapping("/vnc")
public class InstanceVncController {
    @Resource
    private InstanceVncService instanceVncService;

    /**
     * 获取实例的Web管理终端地址
     * @param instanceId 实例ID
     * @return Web管理终端地址
     */
    @GetMapping("/open")
    public Result openInstanceVnc(@RequestParam String instanceId) {
        return instanceVncService.openInstanceVnc(instanceId);
    }
}
