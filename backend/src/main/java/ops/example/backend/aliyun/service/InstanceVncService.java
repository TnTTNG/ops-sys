package ops.example.backend.aliyun.service;

import com.aliyun.ecs20140526.models.DescribeInstanceVncUrlRequest;
import com.aliyun.ecs20140526.models.DescribeInstanceVncUrlResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.aliyun.common.Result;
import ops.example.backend.aliyun.entity.DescribeInstances;
import ops.example.backend.aliyun.mapper.DescribeInstancesMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-02-1:40
 */
@Slf4j
@Service
public class InstanceVncService {
    
    @Resource
    private DescribeInstancesMapper describeInstancesMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public Result openInstanceVnc(String instanceId) {
        try {
            // 获取实例信息
            DescribeInstances instance = describeInstancesMapper.selectId(instanceId);
            if (instance == null) {
                return Result.error("实例不存在");
            }

            // 创建两个异步任务
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                try {
                    return getVncUrlWithClient(ClientUtils.createEcsClient(), instanceId);
                } catch (Exception e) {
                    log.warn("使用第一个AK获取VNC地址失败: {}", e.getMessage());
                    return null;
                }
            }, executorService);

            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                try {
                    return getVncUrlWithClient(ClientUtils.createEcsClient1(), instanceId);
                } catch (Exception e) {
                    log.warn("使用第二个AK获取VNC地址失败: {}", e.getMessage());
                    return null;
                }
            }, executorService);

            // 等待两个任务都完成
            CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
                if (result1 != null) {
                    log.info("使用第一个AK成功获取VNC地址");
                    return result1;
                }
                if (result2 != null) {
                    log.info("使用第二个AK成功获取VNC地址");
                    return result2;
                }
                return null;
            });

            String vncUrl = combinedFuture.get();

            if (vncUrl == null) {
                log.error("两个AK都无法获取VNC地址");
                return Result.error("获取VNC地址失败：实例可能不属于当前账号");
            }

            // 判断是否为Windows系统
            boolean isWindows = true; // 默认Windows系统
            
            // 构建Web管理终端URL
            String webVncUrl = String.format("https://g.alicdn.com/aliyun/ecs-console-vnc2/0.0.8/index.html?vncUrl=%s&instanceId=%s&isWindows=%s",
                    vncUrl, instanceId, isWindows);
            
            return Result.success(webVncUrl);
        } catch (Exception e) {
            log.error("获取VNC地址失败", e);
            return Result.error("获取VNC地址失败：" + e.getMessage());
        }
    }

    /**
     * 使用指定的客户端获取VNC地址
     */
    private String getVncUrlWithClient(com.aliyun.ecs20140526.Client client, String instanceId) throws Exception {
        // 构建VNC请求
        DescribeInstanceVncUrlRequest request = new DescribeInstanceVncUrlRequest()
                .setRegionId("cn-heyuan")
                .setInstanceId(instanceId);
        
        // 调用API获取VNC地址
        RuntimeOptions runtime = new RuntimeOptions();
        DescribeInstanceVncUrlResponse response = client.describeInstanceVncUrlWithOptions(request, runtime);
        return response.getBody().getVncUrl();
    }
}
