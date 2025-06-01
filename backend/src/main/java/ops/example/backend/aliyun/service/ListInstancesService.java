package ops.example.backend.aliyun.service;

import com.aliyun.credentials.Client;
import com.aliyun.swas_open20200601.models.ListInstancesRequest;
import com.aliyun.swas_open20200601.models.ListInstancesResponse;
import com.aliyun.swas_open20200601.models.ListInstancesResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.aliyun.entity.ListInstances;
import ops.example.backend.aliyun.mapper.ListInstanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 轻量应用服务器实例服务接口
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-01-19:44
 */
@Slf4j
@Service
public class ListInstancesService {

    @Resource
    ListInstanceMapper listInstanceMapper;

    /**
     * 根据实例ID查询实例信息
     * @param instanceId 实例ID
     * @return 实例信息
     */
    public ListInstances selectId(String instanceId) {
        return listInstanceMapper.selectId(instanceId);
    }

    /**
     * 分页查询实例列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param instanceId 实例ID（可选）
     * @param status 实例状态（可选）
     * @return 实例列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ListInstances> selectAll(Integer pageNum, Integer pageSize, String instanceId, String status) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        return listInstanceMapper.selectAll(instanceId, status, pageSize, offset);
    }

    /**
     * 查询实例总数
     * @param instanceId 实例ID（可选）
     * @param status 实例状态（可选）
     * @return 实例总数
     */
    @Transactional(rollbackFor = Exception.class)
    public int selectTotal(String instanceId, String status) {
        return listInstanceMapper.selectTotal(instanceId, status);
    }

    /**
     * 同步阿里云轻量应用服务器实例数据到数据库
     * @return 数据库中的实例总数
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncSwasInstances() throws Exception {
        try {
            log.info("开始同步轻量应用服务器实例数据");
            // 创建SWAS客户端
            Client credential = new Client();
            Config config = new Config()
                    .setCredential(credential);
            config.endpoint = "swas.cn-heyuan.aliyuncs.com";
            com.aliyun.swas_open20200601.Client client = new com.aliyun.swas_open20200601.Client(config);
            log.info("SWAS客户端创建成功");

            // 构建请求
            ListInstancesRequest request = new ListInstancesRequest()
                    .setRegionId("cn-heyuan")
                    .setPageSize(50)
                    .setPageNumber(1);
            log.info("请求参数构建完成: {}", request);

            // 调用API
            RuntimeOptions runtime = new RuntimeOptions();
            ListInstancesResponse response = client.listInstancesWithOptions(request, runtime);
            log.info("API调用成功，获取到{}个实例", response.getBody().getTotalCount());

            List<ListInstances> instances = new ArrayList<>();
            for (ListInstancesResponseBody.ListInstancesResponseBodyInstances item : response.getBody().getInstances()) {
                try {
                    ListInstances instance = new ListInstances();
                    instance.setInstanceId(item.getInstanceId());
                    instance.setHostName(item.getInstanceName());
                    // 将GiB转换为MB (1 GiB = 1024 MB)
                    instance.setMemory((int)(item.getResourceSpec().getMemory() * 1024));
                    instance.setCpu(item.getResourceSpec().getCpu());
                    instance.setInternetMaxBandwidthOut(item.getResourceSpec().getBandwidth());
                    instance.setOsName(item.getImage().getImageName());
                    instance.setImageId(item.getImageId());
                    instance.setStatus(item.getStatus());
                    instance.setPublicIpAddress(item.getPublicIpAddress());
                    
                    // 处理过期时间
                    try {
                        if (item.getExpiredTime() != null) {
                            instance.setExpiredTime(LocalDateTime.parse(
                                item.getExpiredTime(),
                                DateTimeFormatter.ISO_DATE_TIME
                            ));
                        }
                    } catch (Exception e) {
                        log.error("解析过期时间失败: {}", item.getExpiredTime(), e);
                    }

                    instance.setInstanceType(item.getPlanId());
                    instance.setRegionId(item.getRegionId());
                    instance.setZoneId(item.getRegionId());
                    instance.setInstanceName(item.getInstanceName());
                    instance.setDescription("");
                    instance.setInstanceTypeFamily(item.getPlanType());
                    instance.setOsType(item.getImage().getOsType());
                    instance.setInternetChargeType(item.getChargeType());
                    instance.setInternetMaxBandwidthIn(item.getResourceSpec().getBandwidth());
                    instance.setVpcId("");
                    instance.setVSwitchId("");
                    instance.setPrivateIpAddress(item.getInnerIpAddress());
                    
                    // 处理创建时间
                    try {
                        if (item.getCreationTime() != null) {
                            instance.setCreationTime(LocalDateTime.parse(
                                item.getCreationTime(),
                                DateTimeFormatter.ISO_DATE_TIME
                            ));
                        }
                    } catch (Exception e) {
                        log.error("解析创建时间失败: {}", item.getCreationTime(), e);
                    }

                    instance.setStartTime(LocalDateTime.now());
                    instance.setCreatedAt(LocalDateTime.now());
                    instance.setUpdatedAt(LocalDateTime.now());

                    instances.add(instance);
                    log.info("成功处理实例: {}", item.getInstanceId());
                } catch (Exception e) {
                    log.error("处理实例数据失败: {}", item.getInstanceId(), e);
                }
            }

            if (!instances.isEmpty()) {
                try {
                    int result = listInstanceMapper.batchInsert(instances);
                    log.info("成功同步{}个实例到数据库", result);
                } catch (Exception e) {
                    log.error("批量插入数据失败", e);
                    throw e;
                }
            } else {
                log.warn("没有找到需要同步的实例");
            }
        } catch (Exception e) {
            log.error("同步轻量应用服务器实例数据失败", e);
            throw new RuntimeException("同步轻量应用服务器实例数据失败", e);
        }
        
        // 返回数据库中的实例总数
        int total = listInstanceMapper.selectTotal(null, null);
        log.info("同步完成，当前数据库中共有 {} 个实例", total);
        return total;
    }

    /**
     * 获取第一个实例ID
     * @return 实例ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String selectFirstInstanceId() {
        return listInstanceMapper.selectFirstInstanceId();
    }
} 