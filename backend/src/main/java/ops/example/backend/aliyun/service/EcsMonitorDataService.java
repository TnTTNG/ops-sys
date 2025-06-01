package ops.example.backend.aliyun.service;

import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.DescribeInstanceMonitorDataRequest;
import com.aliyun.ecs20140526.models.DescribeInstanceMonitorDataResponse;
import com.aliyun.ecs20140526.models.DescribeInstanceMonitorDataResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ops.example.backend.aliyun.entity.EcsMonitorData;
import ops.example.backend.aliyun.mapper.EcsMonitorDataMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ECS实例监控数据服务类
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-31-19:37
 */
@Slf4j
@Service
public class EcsMonitorDataService {

    @Resource
    private EcsMonitorDataMapper ecsMonitorDataMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * 获取实例监控数据
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<EcsMonitorData> getInstanceMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime, Integer period) {
        if (instanceId == null || instanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("实例ID不能为空");
        }

        if (!instanceId.matches("^i-[a-z0-9]+$")) {
            throw new IllegalArgumentException("实例ID格式不正确，应以 i- 开头");
        }

        // 验证并调整period参数
        if (period == null || period < 60 || period > 86400 || period % 60 != 0) {
            period = 60; // 默认使用60秒
        }

        try {
            // 转换为 UTC 时间并格式化
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String startTimeStr = startTime.atZone(java.time.ZoneId.systemDefault())
                    .withZoneSameInstant(java.time.ZoneOffset.UTC)
                    .format(formatter);
            String endTimeStr = endTime.atZone(java.time.ZoneId.systemDefault())
                    .withZoneSameInstant(java.time.ZoneOffset.UTC)
                    .format(formatter);

            // 创建两个异步任务
            Integer finalPeriod = period;
            CompletableFuture<List<EcsMonitorData>> future1 = CompletableFuture.supplyAsync(() -> {
                try {
                    return getMonitorDataWithClient(ClientUtils.createEcsClient(), instanceId, startTimeStr, endTimeStr, finalPeriod, formatter);
                } catch (Exception e) {
                    log.warn("使用第一个AK获取监控数据失败: {}", e.getMessage());
                    return new ArrayList<>();
                }
            }, executorService);

            Integer finalPeriod1 = period;
            CompletableFuture<List<EcsMonitorData>> future2 = CompletableFuture.supplyAsync(() -> {
                try {
                    return getMonitorDataWithClient(ClientUtils.createEcsClient1(), instanceId, startTimeStr, endTimeStr, finalPeriod1, formatter);
                } catch (Exception e) {
                    log.warn("使用第二个AK获取监控数据失败: {}", e.getMessage());
                    return new ArrayList<>();
                }
            }, executorService);

            // 等待两个任务都完成并合并结果
            CompletableFuture<List<EcsMonitorData>> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
                if (!result1.isEmpty()) {
                    log.info("使用第一个AK成功获取监控数据，共{}条记录", result1.size());
                    return result1;
                }
                if (!result2.isEmpty()) {
                    log.info("使用第二个AK成功获取监控数据，共{}条记录", result2.size());
                    return result2;
                }
                log.warn("两个AK都无法获取监控数据");
                return new ArrayList<>();
            });

            List<EcsMonitorData> monitorDataList = combinedFuture.get();

            // 保存监控数据到数据库
            if (!monitorDataList.isEmpty()) {
                saveMonitorData(monitorDataList);
            }

            return monitorDataList;
        } catch (Exception e) {
            log.error("获取实例监控数据失败", e);
            throw new RuntimeException("获取实例监控数据失败: " + e.getMessage());
        }
    }

    /**
     * 使用指定的客户端获取监控数据
     */
    private List<EcsMonitorData> getMonitorDataWithClient(Client client, String instanceId, String startTimeStr,
                                                          String endTimeStr, int period, DateTimeFormatter formatter) throws Exception {
        DescribeInstanceMonitorDataRequest request = new DescribeInstanceMonitorDataRequest()
                .setInstanceId(instanceId)
                .setStartTime(startTimeStr)
                .setEndTime(endTimeStr)
                .setPeriod(period);

        RuntimeOptions runtime = new RuntimeOptions();
        DescribeInstanceMonitorDataResponse response = client.describeInstanceMonitorDataWithOptions(request, runtime);

        if (response == null || response.getBody() == null) {
            throw new RuntimeException("获取监控数据失败：API 响应为空");
        }

        List<EcsMonitorData> monitorDataList = new ArrayList<>();
        DescribeInstanceMonitorDataResponseBody.DescribeInstanceMonitorDataResponseBodyMonitorData monitorDataContainer =
                response.getBody().getMonitorData();

        if (monitorDataContainer == null) {
            log.warn("实例 {} 没有监控数据", instanceId);
            return monitorDataList;
        }

        List<DescribeInstanceMonitorDataResponseBody.DescribeInstanceMonitorDataResponseBodyMonitorDataInstanceMonitorData> dataList =
                monitorDataContainer.getInstanceMonitorData();

        if (dataList == null || dataList.isEmpty()) {
            log.warn("实例 {} 在指定时间范围内没有监控数据", instanceId);
            return monitorDataList;
        }

        for (DescribeInstanceMonitorDataResponseBody.DescribeInstanceMonitorDataResponseBodyMonitorDataInstanceMonitorData data : dataList) {
            EcsMonitorData monitorData = new EcsMonitorData();
            monitorData.setInstanceId(instanceId);

            // 添加空值检查，如果为 null 则使用默认值 0
            monitorData.setCpuUsage(BigDecimal.valueOf(data.getCPU() != null ? data.getCPU() : 0.0f));
            monitorData.setCpuCreditBalance(BigDecimal.valueOf(data.getCPUCreditBalance() != null ? data.getCPUCreditBalance() : 0.0f));
            monitorData.setCpuCreditUsage(BigDecimal.valueOf(data.getCPUCreditUsage() != null ? data.getCPUCreditUsage() : 0.0f));
            monitorData.setCpuNotpaidSurplusCreditUsage(BigDecimal.valueOf(data.getCPUNotpaidSurplusCreditUsage() != null ? data.getCPUNotpaidSurplusCreditUsage() : 0.0f));
            monitorData.setCpuAdvanceCreditBalance(BigDecimal.valueOf(data.getCPUAdvanceCreditBalance() != null ? data.getCPUAdvanceCreditBalance() : 0.0f));

            // 网络和磁盘指标也添加空值检查
            monitorData.setBpsRead(Long.valueOf(data.getBPSRead() != null ? data.getBPSRead() : 0));
            monitorData.setBpsWrite(Long.valueOf(data.getBPSWrite() != null ? data.getBPSWrite() : 0));
            monitorData.setIopsRead(Long.valueOf(data.getIOPSRead() != null ? data.getIOPSRead() : 0));
            monitorData.setIopsWrite(Long.valueOf(data.getIOPSWrite() != null ? data.getIOPSWrite() : 0));
            monitorData.setInternetBandwidth(Long.valueOf(data.getInternetBandwidth() != null ? data.getInternetBandwidth() : 0));
            monitorData.setIntranetBandwidth(Long.valueOf(data.getIntranetBandwidth() != null ? data.getIntranetBandwidth() : 0));
            monitorData.setInternetRx(Long.valueOf(data.getInternetRX() != null ? data.getInternetRX() : 0));
            monitorData.setInternetTx(Long.valueOf(data.getInternetTX() != null ? data.getInternetTX() : 0));
            monitorData.setIntranetRx(Long.valueOf(data.getIntranetRX() != null ? data.getIntranetRX() : 0));
            monitorData.setIntranetTx(Long.valueOf(data.getIntranetTX() != null ? data.getIntranetTX() : 0));

            // 解析返回的时间戳（UTC 时间）
            String timestamp = data.getTimeStamp();
            if (timestamp != null) {
                LocalDateTime monitorTime = LocalDateTime.parse(timestamp, formatter)
                        .atZone(java.time.ZoneOffset.UTC)
                        .withZoneSameInstant(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                monitorData.setMonitorTime(monitorTime);
            } else {
                // 如果时间戳为空，使用当前时间
                monitorData.setMonitorTime(LocalDateTime.now());
            }

            monitorDataList.add(monitorData);
        }

        return monitorDataList;
    }

    /**
     * 保存监控数据
     * @param monitorDataList 监控数据列表
     */
    public void saveMonitorData(List<EcsMonitorData> monitorDataList) {
        if (monitorDataList != null && !monitorDataList.isEmpty()) {
            ecsMonitorDataMapper.batchInsert(monitorDataList);
        }
    }

    /**
     * 查询历史监控数据
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    public List<EcsMonitorData> queryHistoryData(String instanceId, LocalDateTime startTime, LocalDateTime endTime) {
        return ecsMonitorDataMapper.selectByInstanceIdAndTimeRange(instanceId, startTime, endTime, null, null);
    }

    /**
     * 获取最新监控数据
     * @param instanceId 实例ID
     * @return 最新的监控数据
     */
    public EcsMonitorData getLatestData(String instanceId) {
        return ecsMonitorDataMapper.selectLatestByInstanceId(instanceId);
    }

    /**
     * 清理历史数据
     * @param beforeTime 清理该时间点之前的数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void cleanHistoryData(LocalDateTime beforeTime) {
        ecsMonitorDataMapper.deleteBeforeTime(beforeTime);
    }

    /**
     * 获取监控数据总记录数
     * @return 记录总数
     */
    public int getTotalCount() {
        return ecsMonitorDataMapper.selectTotalCount();
    }
}