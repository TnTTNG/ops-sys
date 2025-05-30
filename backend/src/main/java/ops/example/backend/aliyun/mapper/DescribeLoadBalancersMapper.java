package ops.example.backend.aliyun.mapper;

import ops.example.backend.aliyun.entity.DescribeLoadBalancers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-15:54
 */
@Mapper
public interface DescribeLoadBalancersMapper {
    /**
     * 查询负载均衡实例列表
     * @param loadBalancerId 负载均衡实例ID
     * @param status 实例状态
     * @param pageSize 每页大小
     * @param offset 偏移量
     * @return 负载均衡实例列表
     */
    List<DescribeLoadBalancers.LoadBalancer> selectAll(@Param("loadBalancerId") String loadBalancerId,
                                    @Param("status") String status,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("offset") Integer offset);

    /**
     * 查询负载均衡实例总数
     * @param loadBalancerId 负载均衡实例ID
     * @param status 实例状态
     * @return 实例总数
     */
    int selectTotal(@Param("loadBalancerId") String loadBalancerId,
                   @Param("status") String status);

    /**
     * 批量插入负载均衡实例
     * @param loadBalancers 负载均衡实例列表
     * @return 插入的记录数
     */
    int batchInsert(List<DescribeLoadBalancers.LoadBalancer> loadBalancers);
}
