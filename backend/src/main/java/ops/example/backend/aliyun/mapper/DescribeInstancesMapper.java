package ops.example.backend.aliyun.mapper;

import ops.example.backend.aliyun.entity.DescribeInstances;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-0:15
 */
@Mapper
public interface DescribeInstancesMapper {
    List<DescribeInstances> selectAll(@Param("instanceId") String instanceId,
                                    @Param("status") String status,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("offset") Integer offset);

    int selectTotal(@Param("instanceId") String instanceId,
                   @Param("status") String status);

    int batchInsert(List<DescribeInstances> instances);

    @Select("SELECT * FROM ecs_instance WHERE instance_id = #{instanceId}")
    DescribeInstances selectId(String instanceId);

    /**
     * 获取第一个实例ID
     * @return 实例ID
     */
    @Select("SELECT instance_id FROM describe_instances LIMIT 1")
    String selectFirstInstanceId();

}
