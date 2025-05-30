package ops.example.backend.aliyun.service;

import com.aliyun.slb20140515.Client;
import com.aliyun.slb20140515.models.DescribeLoadBalancersRequest;
import com.aliyun.slb20140515.models.DescribeLoadBalancersResponse;
import com.aliyun.slb20140515.models.DescribeLoadBalancersResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import jakarta.annotation.Resource;
import ops.example.backend.aliyun.entity.DescribeLoadBalancers;
import ops.example.backend.aliyun.mapper.DescribeLoadBalancersMapper;
import ops.example.backend.aliyun.utils.ClientUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-15:54
 */
@Service
public class DescribeLoadBalancersService {

    @Resource
    DescribeLoadBalancersMapper describeLoadBalancersMapper;

    /**
     * 查询负载均衡实例列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param loadBalancerId 负载均衡实例ID
     * @param status 实例状态
     * @return 负载均衡实例列表
     */
    public List<DescribeLoadBalancers.LoadBalancer> selectAll(Integer pageNum, Integer pageSize, String loadBalancerId, String status) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        return describeLoadBalancersMapper.selectAll(loadBalancerId, status, pageSize, offset);
    }

    /**
     * 查询负载均衡实例总数
     * @param loadBalancerId 负载均衡实例ID
     * @param status 实例状态
     * @return 实例总数
     */
    public int selectTotal(String loadBalancerId, String status) {
        return describeLoadBalancersMapper.selectTotal(loadBalancerId, status);
    }

    /**
     * 同步阿里云SLB负载均衡实例数据到数据库
     * @return 数据库中的实例总数
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncSlbInstances() throws Exception {
        // 创建SLB客户端
        Client client = ClientUtils.createSlbClient();
        DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
        RuntimeOptions runtime = new RuntimeOptions();
        // 调用API获取数据
        DescribeLoadBalancersResponse response = client.describeLoadBalancersWithOptions(request, runtime);
        List<DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer> items =
                response.getBody().getLoadBalancers().getLoadBalancer();
        
        // 转换为实体对象
        List<DescribeLoadBalancers.LoadBalancer> loadBalancers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer item : items) {
            // 先查询数据库是否存在该实例
            List<DescribeLoadBalancers.LoadBalancer> existingLoadBalancers = describeLoadBalancersMapper.selectAll(item.getLoadBalancerId(), null, 1, 0);
            DescribeLoadBalancers.LoadBalancer loadBalancer;
            
            if (existingLoadBalancers != null && !existingLoadBalancers.isEmpty()) {
                // 如果存在，获取现有实例
                loadBalancer = existingLoadBalancers.get(0);
                // 更新更新时间
                loadBalancer.setUpdatedAt(now);
            } else {
                // 如果不存在，创建新实例
                loadBalancer = new DescribeLoadBalancers.LoadBalancer();
                loadBalancer.setLoadBalancerId(item.getLoadBalancerId());
                // 设置创建时间和更新时间
                loadBalancer.setCreatedAt(now);
                loadBalancer.setUpdatedAt(now);
            }
            
            // 更新实例信息
            loadBalancer.setLoadBalancerName(item.getLoadBalancerName());
            loadBalancer.setLoadBalancerStatus(item.getLoadBalancerStatus());
            loadBalancer.setAddress(item.getAddress());
            loadBalancer.setRegionId(item.getRegionId());
            // 地域ID别名可能不存在，需要判断
            if (item.getRegionIdAlias() != null) {
                loadBalancer.setRegionIdAlias(item.getRegionIdAlias());
            }
            loadBalancer.setMasterZoneId(item.getMasterZoneId());
            loadBalancer.setSlaveZoneId(item.getSlaveZoneId());
            loadBalancer.setNetworkType(item.getNetworkType());
            loadBalancer.setBandwidth(item.getBandwidth());
            loadBalancer.setLoadBalancerSpec(item.getLoadBalancerSpec());
            loadBalancer.setPayType(item.getPayType());
            loadBalancer.setInstanceChargeType(item.getInstanceChargeType());
            loadBalancer.setInternetChargeType(item.getInternetChargeType());
            // 互联网计费类型别名可能不存在，需要判断
            if (item.getInternetChargeTypeAlias() != null) {
                loadBalancer.setInternetChargeTypeAlias(item.getInternetChargeTypeAlias());
            }
            loadBalancer.setResourceGroupId(item.getResourceGroupId());
            loadBalancer.setVpcId(item.getVpcId());
            loadBalancer.setVSwitchId(item.getVSwitchId());
            loadBalancer.setAddressType(item.getAddressType());
            loadBalancer.setAddressIPVersion(item.getAddressIPVersion());
            loadBalancer.setDeleteProtection(item.getDeleteProtection());
            loadBalancer.setCreateTime(item.getCreateTime());
            loadBalancer.setCreateTimeStamp(item.getCreateTimeStamp());
            
            loadBalancers.add(loadBalancer);
        }
        
        // 批量插入或更新数据库
        if (!loadBalancers.isEmpty()) {
            describeLoadBalancersMapper.batchInsert(loadBalancers);
        }
        
        // 返回数据库中的实例总数
        return describeLoadBalancersMapper.selectTotal(null, null);
    }
}
