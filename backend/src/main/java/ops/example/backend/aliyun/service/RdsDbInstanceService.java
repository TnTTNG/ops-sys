package ops.example.backend.aliyun.service;

import com.aliyun.rds20140815.models.DescribeDBInstancesResponse;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponseBody;
import jakarta.annotation.Resource;
import ops.example.backend.aliyun.entity.RdsDbInstance;
import ops.example.backend.aliyun.mapper.RdsDbInstanceMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-29-19:06
 */

@Service
public class RdsDbInstanceService {

    @Resource
    RdsDbInstanceMapper rdsDbInstanceMapper;

    public List<RdsDbInstance> selectAll() {
        return rdsDbInstanceMapper.selectAll();
    }

    /**
     * 同步阿里云RDS实例数据到数据库
     * @return 同步的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncRdsInstances() throws Exception {
        // 创建RDS客户端
        com.aliyun.rds20140815.Client client = ClientUtils.createClient();
        // 构建请求
        com.aliyun.rds20140815.models.DescribeDBInstancesRequest request = new com.aliyun.rds20140815.models.DescribeDBInstancesRequest()
                .setRegionId("cn-heyuan");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        
        // 调用API获取数据
        DescribeDBInstancesResponse response = client.describeDBInstancesWithOptions(request, runtime);
        List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> items = 
            response.getBody().getItems().getDBInstance();
        
        // 转换为实体对象
        List<RdsDbInstance> instances = new ArrayList<>();
        for (DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance item : items) {
            RdsDbInstance instance = new RdsDbInstance();
            instance.setDbInstanceId(item.getDBInstanceId());
            instance.setDescription(item.getDBInstanceDescription());
            instance.setRegionId(item.getRegionId());
            instance.setStatus(item.getDBInstanceStatus());
            instance.setEngine(item.getEngine());
            instance.setEngineVersion(item.getEngineVersion());
            
            // 转换时间格式
            String createTime = item.getCreateTime();
            if (createTime != null) {
                // 解析ISO 8601格式的时间
                Instant instant = Instant.parse(createTime);
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                instance.setCreatedAt(dateTime);
                instance.setUpdatedAt(dateTime);
            }
            
            instances.add(instance);
        }
        
        // 批量插入数据库
        return rdsDbInstanceMapper.batchInsert(instances);
    }
}
