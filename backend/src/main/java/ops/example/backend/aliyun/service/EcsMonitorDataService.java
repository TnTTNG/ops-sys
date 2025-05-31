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

    /**
     * 获取实例监控数据
     * @param instanceId 实例ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 监控数据列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<EcsMonitorData> getInstanceMonitorData(String instanceId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Client client = ClientUtils.createEcsClient();
            DescribeInstanceMonitorDataRequest request = new DescribeInstanceMonitorDataRequest()
                    .setInstanceId(instanceId)
                    .setStartTime(startTime.format(DateTimeFormatter.ISO_DATE_TIME))
                    .setEndTime(endTime.format(DateTimeFormatter.ISO_DATE_TIME));

            RuntimeOptions runtime = new RuntimeOptions();
            DescribeInstanceMonitorDataResponse response = client.describeInstanceMonitorDataWithOptions(request, runtime);

            List<EcsMonitorData> monitorDataList = new ArrayList<>();
            if (response != null && response.getBody() != null && response.getBody().getMonitorData() != null) {
                DescribeInstanceMonitorDataResponseBody.DescribeInstanceMonitorDataResponseBodyMonitorData monitorDataContainer = 
                    response.getBody().getMonitorData();
                List<DescribeInstanceMonitorDataResponseBody.DescribeInstanceMonitorDataResponseBodyMonitorDataInstanceMonitorData> dataList = 
                    monitorDataContainer.getInstanceMonitorData();
                
                if (dataList != null) {
                    for (DescribeInstanceMonitorDataResponseBody.DescribeInstanceMonitorDataResponseBodyMonitorDataInstanceMonitorData data : dataList) {
                        EcsMonitorData monitorData = new EcsMonitorData();
                        monitorData.setInstanceId(instanceId);
                        monitorData.setCpuUsage(BigDecimal.valueOf(data.getCPU()));
                        monitorData.setCpuCreditBalance(BigDecimal.valueOf(data.getCPUCreditBalance()));
                        monitorData.setCpuCreditUsage(BigDecimal.valueOf(data.getCPUCreditUsage()));
                        monitorData.setCpuNotpaidSurplusCreditUsage(BigDecimal.valueOf(data.getCPUNotpaidSurplusCreditUsage()));
                        monitorData.setCpuAdvanceCreditBalance(BigDecimal.valueOf(data.getCPUAdvanceCreditBalance()));
                        monitorData.setBpsRead(Long.valueOf(data.getBPSRead()));
                        monitorData.setBpsWrite(Long.valueOf(data.getBPSWrite()));
                        monitorData.setIopsRead(Long.valueOf(data.getIOPSRead()));
                        monitorData.setIopsWrite(Long.valueOf(data.getIOPSWrite()));
                        monitorData.setInternetBandwidth(Long.valueOf(data.getInternetBandwidth()));
                        monitorData.setIntranetBandwidth(Long.valueOf(data.getIntranetBandwidth()));
                        monitorData.setInternetRx(Long.valueOf(data.getInternetRX()));
                        monitorData.setInternetTx(Long.valueOf(data.getInternetTX()));
                        monitorData.setIntranetRx(Long.valueOf(data.getIntranetRX()));
                        monitorData.setIntranetTx(Long.valueOf(data.getIntranetTX()));
                        monitorData.setMonitorTime(LocalDateTime.parse(data.getTimeStamp(), DateTimeFormatter.ISO_DATE_TIME));
                        monitorDataList.add(monitorData);
                    }
                }
            }

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
}
