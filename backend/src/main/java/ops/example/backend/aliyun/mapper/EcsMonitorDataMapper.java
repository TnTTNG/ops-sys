package ops.example.backend.aliyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ops.example.backend.aliyun.entity.EcsMonitorData;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ECS实例监控数据Mapper接口
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-31-19:40
 */
@Mapper
public interface EcsMonitorDataMapper {
    
    /**
     * 批量插入监控数据
     * @param monitorDataList 监控数据列表
     * @return 插入记录数
     */
    int batchInsert(@Param("list") List<EcsMonitorData> monitorDataList);

    /**
     * 根据实例ID和时间范围查询监控数据
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageSize 每页大小
     * @param offset 偏移量
     * @return 监控数据列表
     */
    List<EcsMonitorData> selectByInstanceIdAndTimeRange(
        @Param("instanceId") String instanceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("pageSize") Integer pageSize,
        @Param("offset") Integer offset
    );

    /**
     * 根据实例ID和时间范围查询监控数据总数
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据总数
     */
    int selectTotalByInstanceIdAndTimeRange(
        @Param("instanceId") String instanceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 根据实例ID查询最新的监控数据
     * @param instanceId 实例ID
     * @return 最新的监控数据
     */
    EcsMonitorData selectLatestByInstanceId(@Param("instanceId") String instanceId);

    /**
     * 删除指定时间范围之前的监控数据
     * @param beforeTime 时间点
     * @return 删除记录数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 获取监控数据总记录数
     * @return 记录总数
     */
    int selectTotalCount();
}
