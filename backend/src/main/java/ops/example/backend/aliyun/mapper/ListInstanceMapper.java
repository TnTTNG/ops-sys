package ops.example.backend.aliyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ops.example.backend.aliyun.entity.ListInstances;

import java.util.List;

/**
 * 轻量应用服务器实例Mapper接口
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-01-19:44
 */
@Mapper
public interface ListInstanceMapper {
    /**
     * 根据实例ID查询实例信息
     * @param instanceId 实例ID
     * @return 实例信息
     */
    ListInstances selectId(@Param("instanceId") String instanceId);

    /**
     * 分页查询实例列表
     * @param instanceId 实例ID（可选）
     * @param status 实例状态（可选）
     * @param pageSize 每页大小
     * @param offset 偏移量
     * @return 实例列表
     */
    List<ListInstances> selectAll(
        @Param("instanceId") String instanceId,
        @Param("status") String status,
        @Param("pageSize") Integer pageSize,
        @Param("offset") Integer offset
    );

    /**
     * 查询实例总数
     * @param instanceId 实例ID（可选）
     * @param status 实例状态（可选）
     * @return 实例总数
     */
    int selectTotal(
        @Param("instanceId") String instanceId,
        @Param("status") String status
    );

    /**
     * 批量插入或更新实例数据
     * @param list 实例列表
     * @return 影响的行数
     */
    int batchInsert(@Param("list") List<ListInstances> list);

    /**
     * 获取第一个实例ID
     * @return 实例ID
     */
    String selectFirstInstanceId();
}
