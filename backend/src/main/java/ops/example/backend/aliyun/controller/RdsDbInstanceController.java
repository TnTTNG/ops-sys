package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.aliyun.common.Result;
import ops.example.backend.aliyun.entity.RdsDbInstance;
import ops.example.backend.aliyun.service.RdsDbInstanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-29-19:09
 * @作用 阿里云 RDS 数据库实例管理接口
 */

@RestController
@RequestMapping("/rds")
public class RdsDbInstanceController {
    @Resource
    RdsDbInstanceService rdsDbInstanceService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        try {
            List<RdsDbInstance> list = rdsDbInstanceService.selectAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取RDS实例列表失败：" + e.getMessage());
        }
    }

    /**
     * 同步阿里云RDS实例数据
     * @return 同步结果
     */
    @GetMapping("/sync")
    public Result syncRdsInstances() {
        try {
            rdsDbInstanceService.syncRdsInstances();
            return Result.success("同步成功");
        } catch (Exception e) {
            return Result.error("同步失败：" + e.getMessage());
        }
    }

    @PostMapping("/restart")
    public Result restartDBInstance(@RequestBody RdsDbInstance instance) {
        try {
            if (instance == null || instance.getDbInstanceId() == null || instance.getDbInstanceId().trim().isEmpty()) {
                return Result.error("实例ID不能为空");
            }

            boolean success = rdsDbInstanceService.restartDBInstance(instance.getDbInstanceId());
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
