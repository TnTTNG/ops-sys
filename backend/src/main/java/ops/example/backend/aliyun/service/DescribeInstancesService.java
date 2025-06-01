package ops.example.backend.aliyun.service;

import com.aliyun.ecs20140526.models.DescribeInstancesResponse;
import com.aliyun.ecs20140526.models.DescribeInstancesResponseBody;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.aliyun.entity.DescribeInstances;
import ops.example.backend.aliyun.mapper.DescribeInstancesMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-0:15
 */
@Slf4j
@Service
public class DescribeInstancesService {

    @Resource
    DescribeInstancesMapper describeInstancesMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Transactional(rollbackFor = Exception.class)
    public List<DescribeInstances> selectAll(Integer pageNum, Integer pageSize, String instanceId, String status) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        return describeInstancesMapper.selectAll(instanceId, status, pageSize, offset);
    }

    @Transactional(rollbackFor = Exception.class)
    public int selectTotal(String instanceId, String status) {
        return describeInstancesMapper.selectTotal(instanceId, status);
    }

    /**
     * 获取第一个实例ID
     * @return 实例ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String selectFirstInstanceId() {
        return describeInstancesMapper.selectFirstInstanceId();
    }

    /**
     * 同步阿里云ECS实例数据到数据库
     * @return 数据库中的实例总数
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncEcsInstances() throws Exception {
        log.info("开始同步ECS实例数据");
        
        // 创建两个异步任务
        CompletableFuture<List<DescribeInstances>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchInstancesWithClient(ClientUtils.createEcsClient());
            } catch (Exception e) {
                log.error("使用第一个AK获取实例数据失败", e);
                return new ArrayList<>();
            }
        }, executorService);

        CompletableFuture<List<DescribeInstances>> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchInstancesWithClient(ClientUtils.createEcsClient1());
            } catch (Exception e) {
                log.error("使用第二个AK获取实例数据失败", e);
                return new ArrayList<>();
            }
        }, executorService);

        // 等待所有任务完成并合并结果
        List<DescribeInstances> allInstances = new ArrayList<>();
        allInstances.addAll(future1.get());
        allInstances.addAll(future2.get());

        log.info("总共获取到{}个实例", allInstances.size());

        // 批量插入或更新数据库
        if (!allInstances.isEmpty()) {
            describeInstancesMapper.batchInsert(allInstances);
        }

        // 返回数据库中的实例总数
        return describeInstancesMapper.selectTotal(null, null);
    }

    /**
     * 使用指定的客户端获取实例数据
     */
    private List<DescribeInstances> fetchInstancesWithClient(com.aliyun.ecs20140526.Client client) throws Exception {
        List<DescribeInstances> instances = new ArrayList<>();
        
        // 构建请求
        com.aliyun.ecs20140526.models.DescribeInstancesRequest request = new com.aliyun.ecs20140526.models.DescribeInstancesRequest()
                .setRegionId("cn-heyuan");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        
        // 调用API获取数据
        DescribeInstancesResponse response = client.describeInstancesWithOptions(request, runtime);
        List<DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance> items = 
            response.getBody().getInstances().getInstance();

        for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance item : items) {
            try {
                // 先查询数据库是否存在该实例
                List<DescribeInstances> existingInstances = describeInstancesMapper.selectAll(item.getInstanceId(), null, 1, 0);
                DescribeInstances instance;
                
                if (existingInstances != null && !existingInstances.isEmpty()) {
                    instance = existingInstances.get(0);
                } else {
                    instance = new DescribeInstances();
                    instance.setInstanceId(item.getInstanceId());
                }
                
                // 更新实例信息
                updateInstanceInfo(instance, item);
                instances.add(instance);
                
            } catch (Exception e) {
                log.error("处理实例数据失败: {}", item.getInstanceId(), e);
            }
        }
        
        return instances;
    }

    /**
     * 更新实例信息
     */
    private void updateInstanceInfo(DescribeInstances instance, DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance item) {
        instance.setHostName(item.getHostName());
        instance.setMemory(item.getMemory());
        instance.setCpu(item.getCpu());
        instance.setCpuOptions(String.valueOf(item.getCpuOptions()));
        instance.setInternetMaxBandwidthOut(item.getInternetMaxBandwidthOut());
        instance.setOsName(item.getOSName());
        instance.setImageId(item.getImageId());
        instance.setStatus(item.getStatus());
        
        // 处理公网IP地址
        try {
            List<String> publicIps = item.getPublicIpAddress().getIpAddress();
            instance.setPublicIpAddress(publicIps != null && !publicIps.isEmpty() ? publicIps.get(0) : "");
        } catch (Exception e) {
            log.error("处理公网IP失败: {}", item.getInstanceId(), e);
            instance.setPublicIpAddress("");
        }
        
        instance.setInstanceType(item.getInstanceType());
        instance.setRegionId(item.getRegionId());
        instance.setZoneId(item.getZoneId());
        instance.setInstanceName(item.getInstanceName());
        instance.setDescription(item.getDescription());
        instance.setInstanceTypeFamily(item.getInstanceTypeFamily());
        instance.setOsType(item.getOSType());
        instance.setInternetChargeType(item.getInternetChargeType());
        instance.setInternetMaxBandwidthIn(item.getInternetMaxBandwidthIn());
        instance.setVpcId(item.getVpcAttributes().getVpcId());
        instance.setVSwitchId(item.getVpcAttributes().getVSwitchId());
        
        // 处理私网IP地址
        try {
            List<String> privateIps = item.getVpcAttributes().getPrivateIpAddress().getIpAddress();
            instance.setPrivateIpAddress(privateIps != null && !privateIps.isEmpty() ? privateIps.get(0) : "");
        } catch (Exception e) {
            log.error("处理私网IP失败: {}", item.getInstanceId(), e);
            instance.setPrivateIpAddress("");
        }
        
        // 处理时间
        try {
            String createTime = item.getCreationTime();
            if (createTime != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(createTime, formatter);
                instance.setCreationTime(dateTime);
                if (instance.getCreatedAt() == null) {
                    instance.setCreatedAt(dateTime);
                }
            }
            
            String startTime = item.getStartTime();
            if (startTime != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(startTime, formatter);
                instance.setStartTime(dateTime);
            }
        } catch (Exception e) {
            log.error("处理时间失败: {}", item.getInstanceId(), e);
            LocalDateTime now = LocalDateTime.now();
            if (instance.getCreatedAt() == null) {
                instance.setCreatedAt(now);
            }
        }
        
        instance.setUpdatedAt(LocalDateTime.now());
    }

    public DescribeInstances selectId(String instanceId) {
        return describeInstancesMapper.selectId(instanceId);
    }
}
