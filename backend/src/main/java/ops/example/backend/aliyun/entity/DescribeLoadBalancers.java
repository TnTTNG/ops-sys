package ops.example.backend.aliyun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-15:54
 */
public class DescribeLoadBalancers {
    private List<LoadBalancer> loadBalancers;

    public List<LoadBalancer> getLoadBalancers() {
        return loadBalancers;
    }

    public void setLoadBalancers(List<LoadBalancer> loadBalancers) {
        this.loadBalancers = loadBalancers;
    }

    /**
     * 负载均衡实例信息
     */
    public static class LoadBalancer {
        /**
         * 主键ID
         */
        private Long id;

        /**
         * 负载均衡实例的ID
         * 示例值: lb-f8zjfvjsz3im85lgt3ysq
         */
        private String loadBalancerId;

        /**
         * 负载均衡实例的名称
         * 示例值: auto_test_slb
         */
        private String loadBalancerName;

        /**
         * 负载均衡实例状态
         * 示例值: active
         */
        private String loadBalancerStatus;

        /**
         * 负载均衡实例服务地址
         * 示例值: 8.155.48.158
         */
        private String address;

        /**
         * 地域ID
         * 示例值: cn-heyuan
         */
        private String regionId;

        /**
         * 地域ID别名
         * 示例值: cn-heyuan
         */
        private String regionIdAlias;

        /**
         * 主可用区ID
         * 示例值: cn-heyuan-b
         */
        private String masterZoneId;

        /**
         * 备可用区ID
         * 示例值: cn-heyuan-a
         */
        private String slaveZoneId;

        /**
         * 网络类型
         * 示例值: classic
         */
        private String networkType;

        /**
         * 带宽
         * 示例值: 5120
         */
        private Integer bandwidth;

        /**
         * 规格
         * 示例值: slb.lcu.elastic
         */
        private String loadBalancerSpec;

        /**
         * 付费类型
         * 示例值: PayOnDemand
         */
        private String payType;

        /**
         * 实例计费方式
         * 示例值: PayByCLCU
         */
        private String instanceChargeType;

        /**
         * 互联网计费类型
         * 示例值: 4
         */
        private String internetChargeType;

        /**
         * 互联网计费类型别名
         * 示例值: paybytraffic
         */
        private String internetChargeTypeAlias;

        /**
         * 资源组ID
         * 示例值: rg-acfm3s6onzq2tzq
         */
        private String resourceGroupId;

        /**
         * VPC ID
         * 示例值: 
         */
        private String vpcId;

        /**
         * 交换机ID
         * 示例值: 
         */
        private String vSwitchId;

        /**
         * 地址类型
         * 示例值: internet
         */
        private String addressType;

        /**
         * IP版本
         * 示例值: ipv4
         */
        private String addressIPVersion;

        /**
         * 删除保护
         * 示例值: off
         */
        private String deleteProtection;

        /**
         * 负载均衡实例创建时间
         * 示例值: 2025-05-30T17:34Z
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private String createTime;

        /**
         * 负载均衡实例创建时间戳
         * 示例值: 1748597644000
         */
        private Long createTimeStamp;

        /**
         * 记录创建时间
         */
        private LocalDateTime createdAt;

        /**
         * 记录更新时间
         */
        private LocalDateTime updatedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLoadBalancerId() {
            return loadBalancerId;
        }

        public void setLoadBalancerId(String loadBalancerId) {
            this.loadBalancerId = loadBalancerId;
        }

        public String getLoadBalancerName() {
            return loadBalancerName;
        }

        public void setLoadBalancerName(String loadBalancerName) {
            this.loadBalancerName = loadBalancerName;
        }

        public String getLoadBalancerStatus() {
            return loadBalancerStatus;
        }

        public void setLoadBalancerStatus(String loadBalancerStatus) {
            this.loadBalancerStatus = loadBalancerStatus;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public String getRegionIdAlias() {
            return regionIdAlias;
        }

        public void setRegionIdAlias(String regionIdAlias) {
            this.regionIdAlias = regionIdAlias;
        }

        public String getMasterZoneId() {
            return masterZoneId;
        }

        public void setMasterZoneId(String masterZoneId) {
            this.masterZoneId = masterZoneId;
        }

        public String getSlaveZoneId() {
            return slaveZoneId;
        }

        public void setSlaveZoneId(String slaveZoneId) {
            this.slaveZoneId = slaveZoneId;
        }

        public String getNetworkType() {
            return networkType;
        }

        public void setNetworkType(String networkType) {
            this.networkType = networkType;
        }

        public Integer getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Integer bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getLoadBalancerSpec() {
            return loadBalancerSpec;
        }

        public void setLoadBalancerSpec(String loadBalancerSpec) {
            this.loadBalancerSpec = loadBalancerSpec;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getInstanceChargeType() {
            return instanceChargeType;
        }

        public void setInstanceChargeType(String instanceChargeType) {
            this.instanceChargeType = instanceChargeType;
        }

        public String getInternetChargeType() {
            return internetChargeType;
        }

        public void setInternetChargeType(String internetChargeType) {
            this.internetChargeType = internetChargeType;
        }

        public String getInternetChargeTypeAlias() {
            return internetChargeTypeAlias;
        }

        public void setInternetChargeTypeAlias(String internetChargeTypeAlias) {
            this.internetChargeTypeAlias = internetChargeTypeAlias;
        }

        public String getResourceGroupId() {
            return resourceGroupId;
        }

        public void setResourceGroupId(String resourceGroupId) {
            this.resourceGroupId = resourceGroupId;
        }

        public String getVpcId() {
            return vpcId;
        }

        public void setVpcId(String vpcId) {
            this.vpcId = vpcId;
        }

        public String getVSwitchId() {
            return vSwitchId;
        }

        public void setVSwitchId(String vSwitchId) {
            this.vSwitchId = vSwitchId;
        }

        public String getAddressType() {
            return addressType;
        }

        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }

        public String getAddressIPVersion() {
            return addressIPVersion;
        }

        public void setAddressIPVersion(String addressIPVersion) {
            this.addressIPVersion = addressIPVersion;
        }

        public String getDeleteProtection() {
            return deleteProtection;
        }

        public void setDeleteProtection(String deleteProtection) {
            this.deleteProtection = deleteProtection;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Long getCreateTimeStamp() {
            return createTimeStamp;
        }

        public void setCreateTimeStamp(Long createTimeStamp) {
            this.createTimeStamp = createTimeStamp;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("LoadBalancer{");
            sb.append("id=").append(id);
            sb.append(", loadBalancerId='").append(loadBalancerId).append('\'');
            sb.append(", loadBalancerName='").append(loadBalancerName).append('\'');
            sb.append(", loadBalancerStatus='").append(loadBalancerStatus).append('\'');
            sb.append(", address='").append(address).append('\'');
            sb.append(", regionId='").append(regionId).append('\'');
            sb.append(", regionIdAlias='").append(regionIdAlias).append('\'');
            sb.append(", masterZoneId='").append(masterZoneId).append('\'');
            sb.append(", slaveZoneId='").append(slaveZoneId).append('\'');
            sb.append(", networkType='").append(networkType).append('\'');
            sb.append(", bandwidth=").append(bandwidth);
            sb.append(", loadBalancerSpec='").append(loadBalancerSpec).append('\'');
            sb.append(", payType='").append(payType).append('\'');
            sb.append(", instanceChargeType='").append(instanceChargeType).append('\'');
            sb.append(", internetChargeType='").append(internetChargeType).append('\'');
            sb.append(", internetChargeTypeAlias='").append(internetChargeTypeAlias).append('\'');
            sb.append(", resourceGroupId='").append(resourceGroupId).append('\'');
            sb.append(", vpcId='").append(vpcId).append('\'');
            sb.append(", vSwitchId='").append(vSwitchId).append('\'');
            sb.append(", addressType='").append(addressType).append('\'');
            sb.append(", addressIPVersion='").append(addressIPVersion).append('\'');
            sb.append(", deleteProtection='").append(deleteProtection).append('\'');
            sb.append(", createTime='").append(createTime).append('\'');
            sb.append(", createTimeStamp=").append(createTimeStamp);
            sb.append(", createdAt=").append(createdAt);
            sb.append(", updatedAt=").append(updatedAt);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DescribeLoadBalancers{");
        sb.append("loadBalancers=").append(loadBalancers);
        sb.append('}');
        return sb.toString();
    }
}
