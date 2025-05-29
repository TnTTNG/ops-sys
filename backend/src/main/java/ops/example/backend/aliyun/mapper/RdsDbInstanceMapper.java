package ops.example.backend.aliyun.mapper;

import ops.example.backend.aliyun.entity.RdsDbInstance;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-29-19:03
 */

@Mapper
public interface RdsDbInstanceMapper {

    List<RdsDbInstance> selectAll();
    
    /**
     * 批量插入RDS实例数据
     * @param instances RDS实例列表
     * @return 插入成功的记录数
     */
    int batchInsert(List<RdsDbInstance> instances);
}
