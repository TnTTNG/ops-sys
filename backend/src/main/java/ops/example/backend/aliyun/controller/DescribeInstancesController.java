package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.aliyun.entity.DescribeInstances;
import ops.example.backend.aliyun.service.DescribeInstancesService;
import ops.example.backend.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-29-23:43
 * @作用 阿里云 ECS 实例管理接口
 */
@RestController
@RequestMapping("/ecs")
public class DescribeInstancesController {
    @Resource
    DescribeInstancesService describeInstancesService;

    @GetMapping("/selectId")
     public Result selectId(@RequestParam String instanceId) {
        DescribeInstances describeInstances = describeInstancesService.selectId(instanceId);
        return Result.success(describeInstances);
    }
    @GetMapping("/selectAll")
    public Result selectAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) String instanceId,
                          @RequestParam(required = false) String status) {
        List<DescribeInstances> list = describeInstancesService.selectAll(pageNum, pageSize, instanceId, status);
        int total = describeInstancesService.selectTotal(instanceId, status);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.success(data);
    }

    /**
     * 同步阿里云ECS实例数据
     * @return 同步结果
     */
    @GetMapping("/sync")
    public Result syncEcsInstances() {
        try {
            int count = describeInstancesService.syncEcsInstances();
            return Result.success("同步成功，共同步" + count + "条记录");
        } catch (Exception e) {
            return Result.error("同步失败：" + e.getMessage());
        }
    }

    /**
     * 获取单个实例ID
     * @return 实例ID
     */
    @GetMapping("/getInstanceId")
    public Result getInstanceId() {
        try {
            String instanceId = describeInstancesService.selectFirstInstanceId();
            return Result.success(instanceId);
        } catch (Exception e) {
            return Result.error("获取实例ID失败：" + e.getMessage());
        }
    }
    
    /**
     * 重启ECS实例
     * @param instance 包含实例ID的实例对象
     * @return 重启结果
     */
    @PostMapping("/restart")
    public Result restartInstance(@RequestBody DescribeInstances instance) {
        try {
            if (instance == null || instance.getInstanceId() == null || instance.getInstanceId().trim().isEmpty()) {
                return Result.error("实例ID不能为空");
            }

            boolean success = describeInstancesService.rebootInstance(instance.getInstanceId());
            if (success) {
                return Result.success("重启指令已发送，请等待实例重启完成");
            } else {
                return Result.error("重启失败，请检查实例状态或权限");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("404")) {
                return Result.error("实例不存在或不在当前区域");
            } else if (errorMessage.contains("403")) {
                return Result.error("当前实例状态不支持重启操作");
            } else {
                return Result.error("重启失败：" + errorMessage);
            }
        }
    }
}
