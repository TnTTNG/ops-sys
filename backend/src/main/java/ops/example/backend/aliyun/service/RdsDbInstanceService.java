package ops.example.backend.aliyun.service;

import com.aliyun.rds20140815.models.DescribeDBInstancesResponse;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponseBody;
import com.aliyun.rds20140815.models.RestartDBInstanceRequest;
import com.aliyun.rds20140815.models.RestartDBInstanceResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-29-19:06
 */
@Slf4j
@Service
public class RdsDbInstanceService {

    @Resource
    RdsDbInstanceMapper rdsDbInstanceMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public List<RdsDbInstance> selectAll() {
        return rdsDbInstanceMapper.selectAll();
    }

    /**
     * 同步阿里云RDS实例数据到数据库
     * @return 同步的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncRdsInstances() throws Exception {
        log.info("开始同步RDS实例数据");

        // 创建两个异步任务
        CompletableFuture<List<RdsDbInstance>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return getRdsInstancesWithClient(ClientUtils.createClient(), "cn-heyuan");
            } catch (Exception e) {
                log.warn("使用第一个AK获取RDS实例数据失败: {}", e.getMessage());
                return new ArrayList<>();
            }
        }, executorService);

        CompletableFuture<List<RdsDbInstance>> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return getRdsInstancesWithClient(ClientUtils.createClient1(), "cn-shenzhen");
            } catch (Exception e) {
                log.warn("使用第二个AK获取RDS实例数据失败: {}", e.getMessage());
                return new ArrayList<>();
            }
        }, executorService);

        // 等待两个任务都完成并合并结果
        CompletableFuture<List<RdsDbInstance>> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
            List<RdsDbInstance> allInstances = new ArrayList<>();
            if (!result1.isEmpty()) {
                log.info("使用第一个AK成功获取RDS实例数据，共{}个实例", result1.size());
                allInstances.addAll(result1);
            }
            if (!result2.isEmpty()) {
                log.info("使用第二个AK成功获取RDS实例数据，共{}个实例", result2.size());
                allInstances.addAll(result2);
            }
            return allInstances;
        });

        List<RdsDbInstance> allInstances = combinedFuture.get();

        if (allInstances.isEmpty()) {
            log.warn("没有找到需要同步的RDS实例");
            return 0;
        }

        log.info("总共获取到{}个RDS实例", allInstances.size());

        // 批量插入数据库
        return rdsDbInstanceMapper.batchInsert(allInstances);
    }

    /**
     * 使用指定的客户端获取RDS实例数据
     */
    private List<RdsDbInstance> getRdsInstancesWithClient(com.aliyun.rds20140815.Client client, String regionId) throws Exception {
        // 构建请求
        com.aliyun.rds20140815.models.DescribeDBInstancesRequest request = new com.aliyun.rds20140815.models.DescribeDBInstancesRequest()
                .setRegionId(regionId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        // 调用API获取数据
        DescribeDBInstancesResponse response = client.describeDBInstancesWithOptions(request, runtime);
        List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> items =
                response.getBody().getItems().getDBInstance();

        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为实体对象
        List<RdsDbInstance> instances = new ArrayList<>();
        for (DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance item : items) {
            try {
                RdsDbInstance instance = new RdsDbInstance();
                instance.setDbInstanceId(item.getDBInstanceId());
                instance.setDbInstanceDescription(item.getDBInstanceDescription());
                instance.setRegionId(item.getRegionId());
                instance.setDbInstanceStatus(item.getDBInstanceStatus());
                instance.setEngine(item.getEngine());
                instance.setEngineVersion(item.getEngineVersion());
                instance.setZoneId(item.getZoneId());
                instance.setDbInstanceClass(item.getDBInstanceClass());
                instance.setCreateTime(item.getCreateTime());
                instance.setVSwitchId(item.getVSwitchId());
                instance.setTipsLevel(item.getTipsLevel());
                instance.setDeletionProtection(item.getDeletionProtection());
                instance.setLockMode(item.getLockMode());
                instance.setPayType(item.getPayType());
                instance.setDbInstanceStorageType(item.getDBInstanceStorageType());
                instance.setVpcId(item.getVpcId());
                instance.setConnectionMode(item.getConnectionMode());
                instance.setConnectionString(item.getConnectionString());
                instance.setExpireTime(item.getExpireTime());
                instance.setDbInstanceMemory(item.getDBInstanceMemory());
                instance.setResourceGroupId(item.getResourceGroupId());
                instance.setDbInstanceNetType(item.getDBInstanceNetType());
                instance.setDbInstanceType(item.getDBInstanceType());
                instance.setMutriORsignle(item.getMutriORsignle());
                instance.setInstanceNetworkType(item.getInstanceNetworkType());

                // 转换时间格式
                String createTime = item.getCreateTime();
                if (createTime != null) {
                    // 解析ISO 8601格式的时间
                    Instant instant = Instant.parse(createTime);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    instance.setCreatedAt(dateTime);
                    instance.setUpdatedAt(dateTime);
                } else {
                    instance.setCreatedAt(LocalDateTime.now());
                    instance.setUpdatedAt(LocalDateTime.now());
                }

                instances.add(instance);
            } catch (Exception e) {
                log.error("处理RDS实例数据失败: {}", item.getDBInstanceId(), e);
            }
        }

        return instances;
    }

    /**
     * 重启RDS实例
     * @param dbInstanceId RDS实例ID
     * @return 是否重启成功
     */
    public boolean restartDBInstance(String dbInstanceId) {
        log.info("开始重启RDS实例: {}", dbInstanceId);

        try {
            boolean success = restartDBInstanceWithClient(ClientUtils.createClient(), dbInstanceId, "cn-heyuan");
            if (success) {
                log.info("使用第一个AK成功重启RDS实例: {}", dbInstanceId);
                return true;
            }
        } catch (Exception e) {
            log.warn("使用第一个AK重启RDS实例失败: {}", e.getMessage());
        }

        // 如果第一个AK失败，尝试使用第二个AK
        try {
            boolean success = restartDBInstanceWithClient(ClientUtils.createClient1(), dbInstanceId, "cn-shenzhen");

            if (success) {
                log.info("使用第二个AK成功重启RDS实例: {}", dbInstanceId);
                return true;
            }
        } catch (Exception e) {
            log.warn("使用第二个AK重启RDS实例失败: {}", e.getMessage());
        }
        log.error("RDS实例重启失败: {}", dbInstanceId);
        return false;
    }

    /**
     * 使用指定的客户端重启RDS实例
     */
    private boolean restartDBInstanceWithClient(com.aliyun.rds20140815.Client client, String dbInstanceId, String regionId) throws Exception {
        RestartDBInstanceRequest request = new RestartDBInstanceRequest()
                .setDBInstanceId(dbInstanceId);
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            RestartDBInstanceResponse response = client.restartDBInstanceWithOptions(request, runtime);
            return response != null && response.getBody() != null;
        } catch (Exception e) {
            log.error("重启RDS实例失败: {}, 区域: {}", dbInstanceId, regionId, e);
            throw e;
        }
    }
}