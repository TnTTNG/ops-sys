package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.aliyun.entity.DescribeInstances;
import ops.example.backend.aliyun.service.DescribeInstancesService;
import ops.example.backend.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
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
}
