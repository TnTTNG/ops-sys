package ops.example.backend.aliyun.service;

import com.aliyun.ecs20140526.models.DescribeInstanceVncUrlRequest;
import com.aliyun.ecs20140526.models.DescribeInstanceVncUrlResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import jakarta.annotation.Resource;
import ops.example.backend.aliyun.common.Result;
import ops.example.backend.aliyun.entity.DescribeInstances;
import ops.example.backend.aliyun.mapper.DescribeInstancesMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import org.springframework.stereotype.Service;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-02-1:40
 */

@Service
public class InstanceVncService {
    
    @Resource
    private DescribeInstancesMapper describeInstancesMapper;

    public Result openInstanceVnc(String instanceId) {
        try {
            // 获取实例信息
            DescribeInstances instance = describeInstancesMapper.selectId(instanceId);
            if (instance == null) {
                return Result.error("实例不存在");
            }

            // 创建ECS客户端
            com.aliyun.ecs20140526.Client client = ClientUtils.createEcsClient();
            com.aliyun.ecs20140526.Client client1 = ClientUtils.createEcsClient1();

            // 构建VNC请求
            DescribeInstanceVncUrlRequest request = new DescribeInstanceVncUrlRequest()
                    .setRegionId("cn-heyuan")
                    .setInstanceId(instanceId);
            // DescribeInstanceVncUrlRequest request1 = new DescribeInstanceVncUrlRequest()
            //         .setRegionId("cn-heyuan")
            //         .setInstanceId(instanceId);
            
            // 调用API获取VNC地址
            RuntimeOptions runtime = new RuntimeOptions();
            DescribeInstanceVncUrlResponse response = client.describeInstanceVncUrlWithOptions(request, runtime);
            String vncUrl = response.getBody().getVncUrl();
            
            // 判断是否为Windows系统
            boolean isWindows = true; // 默认Windows系统
            
            // 构建Web管理终端URL
            String webVncUrl = String.format("https://g.alicdn.com/aliyun/ecs-console-vnc2/0.0.8/index.html?vncUrl=%s&instanceId=%s&isWindows=%s",
                    vncUrl, instanceId, isWindows);
            
            return Result.success(webVncUrl);
        } catch (Exception e) {
            return Result.error("获取VNC地址失败：" + e.getMessage());
        }
    }

}
