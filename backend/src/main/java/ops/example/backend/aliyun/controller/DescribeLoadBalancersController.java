package ops.example.backend.aliyun.controller;

import jakarta.annotation.Resource;
import ops.example.backend.common.Result;
import ops.example.backend.aliyun.entity.DescribeLoadBalancers;
import ops.example.backend.aliyun.service.DescribeLoadBalancersService;
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
 * @created_date 2025-05-30-15:53
 * @作用 阿里云 SLB 负载均衡实例管理接口
 */
@RestController
@RequestMapping("/slb")
public class DescribeLoadBalancersController {
    @Resource
    DescribeLoadBalancersService describeLoadBalancersService;

    /**
     * 查询负载均衡实例列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param loadBalancerId 负载均衡实例ID
     * @param status 实例状态
     * @return 负载均衡实例列表和总数
     */
    @GetMapping("/selectAll")
    public Result selectAll(@RequestParam(required = false) Integer pageNum,
                          @RequestParam(required = false) Integer pageSize,
                          @RequestParam(required = false) String loadBalancerId,
                          @RequestParam(required = false) String status) {
        List<DescribeLoadBalancers.LoadBalancer> list = describeLoadBalancersService.selectAll(pageNum, pageSize, loadBalancerId, status);
        int total = describeLoadBalancersService.selectTotal(loadBalancerId, status);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.success(data);
    }

    /**
     * 同步阿里云SLB负载均衡实例数据
     * @return 同步结果
     */
    @GetMapping("/sync")
    public Result syncSlbInstances() {
        try {
            int count = describeLoadBalancersService.syncSlbInstances();
            return Result.success("同步成功，共同步" + count + "条记录");
        } catch (Exception e) {
            return Result.error("同步失败：" + e.getMessage());
        }
    }
}
