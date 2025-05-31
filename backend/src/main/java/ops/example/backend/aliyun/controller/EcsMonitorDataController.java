package ops.example.backend.aliyun.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ops.example.backend.aliyun.entity.EcsMonitorData;
import ops.example.backend.aliyun.service.EcsMonitorDataService;
import ops.example.backend.common.Result;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ECS实例监控数据控制器
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-31-19:37
 */
@Slf4j
@RestController
@RequestMapping("/ecs/monitor")
public class EcsMonitorDataController {

    @Autowired
    private EcsMonitorDataService ecsMonitorDataService;

    /**
     * 获取实例监控数据
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    @GetMapping("/data")
    public Result getMonitorData(
            @RequestParam String instanceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<EcsMonitorData> data = ecsMonitorDataService.getInstanceMonitorData(instanceId, startTime, endTime);
            Map<String, Object> result = new HashMap<>();
            result.put("list", data);
            result.put("total", data.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取监控数据失败", e);
            return Result.error("获取监控数据失败：" + e.getMessage());
        }
    }

    /**
     * 查询历史监控数据
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    @GetMapping("/history")
    public Result getHistoryData(
            @RequestParam String instanceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<EcsMonitorData> data = ecsMonitorDataService.queryHistoryData(instanceId, startTime, endTime);
            Map<String, Object> result = new HashMap<>();
            result.put("list", data);
            result.put("total", data.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询历史数据失败", e);
            return Result.error("查询历史数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取最新监控数据
     * @param instanceId 实例ID
     * @return 最新的监控数据
     */
    @GetMapping("/latest")
    public Result getLatestData(@RequestParam String instanceId) {
        try {
            EcsMonitorData data = ecsMonitorDataService.getLatestData(instanceId);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取最新数据失败", e);
            return Result.error("获取最新数据失败：" + e.getMessage());
        }
    }

    /**
     * 清理历史数据
     * @param beforeTime 清理该时间点之前的数据
     * @return 操作结果
     */
    @DeleteMapping("/clean")
    public Result cleanHistoryData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        try {
            ecsMonitorDataService.cleanHistoryData(beforeTime);
            return Result.success("清理历史数据成功");
        } catch (Exception e) {
            log.error("清理历史数据失败", e);
            return Result.error("清理历史数据失败：" + e.getMessage());
        }
    }
}
