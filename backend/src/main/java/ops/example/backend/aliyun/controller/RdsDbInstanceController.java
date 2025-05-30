package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.aliyun.common.Result;
import ops.example.backend.aliyun.entity.RdsDbInstance;
import ops.example.backend.aliyun.service.RdsDbInstanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            int count = rdsDbInstanceService.syncRdsInstances();
            return Result.success("同步成功，共同步" + count + "条记录");
        } catch (Exception e) {
            return Result.error("同步失败：" + e.getMessage());
        }
    }
}
