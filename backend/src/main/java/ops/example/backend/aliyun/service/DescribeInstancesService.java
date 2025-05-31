package ops.example.backend.aliyun.service;

import com.aliyun.ecs20140526.models.DescribeInstancesResponse;
import com.aliyun.ecs20140526.models.DescribeInstancesResponseBody;
import jakarta.annotation.Resource;
import ops.example.backend.aliyun.entity.DescribeInstances;
import ops.example.backend.aliyun.mapper.DescribeInstancesMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-0:15
 */
@Service
public class DescribeInstancesService {

    @Resource
    DescribeInstancesMapper describeInstancesMapper;

    public List<DescribeInstances> selectAll(Integer pageNum, Integer pageSize, String instanceId, String status) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        return describeInstancesMapper.selectAll(instanceId, status, pageSize, offset);
    }

    public int selectTotal(String instanceId, String status) {
        return describeInstancesMapper.selectTotal(instanceId, status);
    }

    /**
     * 同步阿里云ECS实例数据到数据库
     * @return 数据库中的实例总数
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncEcsInstances() throws Exception {
        // 创建ECS客户端
        com.aliyun.ecs20140526.Client client = ClientUtils.createEcsClient();
        // 构建请求
        com.aliyun.ecs20140526.models.DescribeInstancesRequest request = new com.aliyun.ecs20140526.models.DescribeInstancesRequest()
                .setRegionId("cn-heyuan");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        
        // 调用API获取数据
        DescribeInstancesResponse response = client.describeInstancesWithOptions(request, runtime);
        List<DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance> items = 
            response.getBody().getInstances().getInstance();
        
        // 转换为实体对象
        List<DescribeInstances> instances = new ArrayList<>();
        for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance item : items) {
            // 先查询数据库是否存在该实例
            List<DescribeInstances> existingInstances = describeInstancesMapper.selectAll(item.getInstanceId(), null, 1, 0);
            DescribeInstances instance;
            
            if (existingInstances != null && !existingInstances.isEmpty()) {
                // 如果存在，获取现有实例
                instance = existingInstances.get(0);
            } else {
                // 如果不存在，创建新实例
                instance = new DescribeInstances();
                instance.setInstanceId(item.getInstanceId());
            }
            
            // 更新实例信息
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
                System.out.println("=== 公网IP解析开始 ===");
                System.out.println("实例ID: " + item.getInstanceId());
                System.out.println("原始公网IP对象: " + item.getPublicIpAddress());
                List<String> publicIps = item.getPublicIpAddress().getIpAddress();
                System.out.println("公网IP列表: " + publicIps);
                if (publicIps != null && !publicIps.isEmpty()) {
                    String publicIp = publicIps.get(0);
                    System.out.println("设置公网IP: " + publicIp);
                    instance.setPublicIpAddress(publicIp);
                } else {
                    System.out.println("公网IP列表为空");
                    instance.setPublicIpAddress("");
                }
                System.out.println("=== 公网IP解析结束 ===");
            } catch (Exception e) {
                System.err.println("公网IP解析错误: " + e.getMessage());
                e.printStackTrace();
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
                System.out.println("=== 私网IP解析开始 ===");
                System.out.println("实例ID: " + item.getInstanceId());
                System.out.println("原始私网IP对象: " + item.getVpcAttributes().getPrivateIpAddress());
                List<String> privateIps = item.getVpcAttributes().getPrivateIpAddress().getIpAddress();
                System.out.println("私网IP列表: " + privateIps);
                if (privateIps != null && !privateIps.isEmpty()) {
                    String privateIp = privateIps.get(0);
                    System.out.println("设置私网IP: " + privateIp);
                    instance.setPrivateIpAddress(privateIp);
                } else {
                    System.out.println("私网IP列表为空");
                    instance.setPrivateIpAddress("");
                }
                System.out.println("=== 私网IP解析结束 ===");
            } catch (Exception e) {
                System.err.println("私网IP解析错误: " + e.getMessage());
                e.printStackTrace();
                instance.setPrivateIpAddress("");
            }
            
            // 转换时间格式
            try {
                String createTime = item.getCreationTime();
                if (createTime != null) {
                    // 使用DateTimeFormatter处理时间格式
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime dateTime = LocalDateTime.parse(createTime, formatter);
                    instance.setCreationTime(dateTime);
                    if (instance.getCreatedAt() == null) {
                        instance.setCreatedAt(dateTime);
                    }
                    instance.setUpdatedAt(LocalDateTime.now());
                }
                
                String startTime = item.getStartTime();
                if (startTime != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime dateTime = LocalDateTime.parse(startTime, formatter);
                    instance.setStartTime(dateTime);
                }
            } catch (Exception e) {
                // 如果时间解析失败，记录错误并继续处理
                System.err.println("Error parsing time for instance " + item.getInstanceId() + ": " + e.getMessage());
                // 设置当前时间作为默认值
                LocalDateTime now = LocalDateTime.now();
                if (instance.getCreatedAt() == null) {
                    instance.setCreatedAt(now);
                }
                instance.setUpdatedAt(now);
            }
            
            instances.add(instance);
        }
        
        // 批量插入或更新数据库
        if (!instances.isEmpty()) {
            describeInstancesMapper.batchInsert(instances);
        }
        
        // 返回数据库中的实例总数
        return describeInstancesMapper.selectTotal(null, null);
    }

    public DescribeInstances selectId(String instanceId) {
         return describeInstancesMapper.selectId(instanceId);
    }
}
