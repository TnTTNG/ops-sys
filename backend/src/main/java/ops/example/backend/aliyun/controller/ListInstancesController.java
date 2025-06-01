package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.aliyun.entity.ListInstances;
import ops.example.backend.aliyun.service.ListInstancesService;
import ops.example.backend.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轻量应用服务器实例管理接口
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-01-19:44
 */
@RestController
@RequestMapping("/swas")
public class ListInstancesController {
    @Resource
    private ListInstancesService listInstancesService;

    /**
     * 根据实例ID查询实例信息
     * @param instanceId 实例ID
     * @return 实例信息
     */
    @GetMapping("/selectId")
    public Result selectId(@RequestParam String instanceId) {
        ListInstances listInstances = listInstancesService.selectId(instanceId);
        return Result.success(listInstances);
    }

    /**
     * 分页查询实例列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param instanceId 实例ID（可选）
     * @param status 实例状态（可选）
     * @return 实例列表和总数
     */
    @GetMapping("/selectAll")
    public Result selectAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) String instanceId,
                          @RequestParam(required = false) String status) {
        List<ListInstances> list = listInstancesService.selectAll(pageNum, pageSize, instanceId, status);
        int total = listInstancesService.selectTotal(instanceId, status);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.success(data);
    }

    /**
     * 同步阿里云轻量应用服务器实例数据
     * @return 同步结果
     */
    @GetMapping("/sync")
    public Result syncSwasInstances() {
        try {
            int count = listInstancesService.syncSwasInstances();
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
            String instanceId = listInstancesService.selectFirstInstanceId();
            return Result.success(instanceId);
        } catch (Exception e) {
            return Result.error("获取实例ID失败：" + e.getMessage());
        }
    }


    /**
     * 登录实例
     * @param instanceId 实例ID
     * @return 登录响应
     */
    @GetMapping("/login")
    public Result loginInstance(@RequestParam String instanceId) {
        return listInstancesService.loginInstance(instanceId);
    }
} 