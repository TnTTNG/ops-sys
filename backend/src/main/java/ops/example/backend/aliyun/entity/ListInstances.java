package ops.example.backend.aliyun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 轻量应用服务器实例列表实体类
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-06-01-19:44
 */
@Data
public class ListInstances {
    private Integer id;
    private String instanceId;           // 实例ID
    private String hostName;             // 实例主机名
    private Integer memory;              // 内存大小(MB)
    private Integer cpu;                 // vCPU数
    private String cpuOptions;           // CPU配置详情
    private Integer internetMaxBandwidthOut; // 公网出带宽(Mbps)
    private String osName;               // 操作系统名称
    private String imageId;              // 镜像ID
    private String status;               // 实例状态
    private String publicIpAddress;      // 公网IP
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expiredTime;   // 过期时间
    private String instanceType;         // 实例规格
    private String regionId;             // 地域ID
    private String zoneId;               // 可用区ID
    private String instanceName;         // 实例名称
    private String description;          // 实例描述
    private String instanceTypeFamily;   // 实例规格族
    private String osType;               // 操作系统类型
    private String internetChargeType;   // 网络计费类型
    private Integer internetMaxBandwidthIn; // 公网入带宽(Mbps)
    private String vpcId;                // VPC ID
    private String vSwitchId;            // 交换机ID
    private String privateIpAddress;     // 私网IP
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime creationTime;   // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;      // 启动时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;      // 记录创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;      // 记录更新时间

    /**
     * 实例列表
     */
    private List<Instance> instances;
    
    /**
     * 总记录数
     */
    private Integer totalCount;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 每页记录数
     */
    private Integer pageSize;
    
    /**
     * 当前页码
     */
    private Integer pageNumber;

    /**
     * 实例信息
     */
    @Data
    public static class Instance {
        /**
         * 实例状态
         */
        private String status;
        
        /**
         * 禁用原因
         */
        private String disableReason;
        
        /**
         * 资源组ID
         */
        private String resourceGroupId;
        
        /**
         * 实例ID
         */
        private String instanceId;
        
        /**
         * 套餐ID
         */
        private String planId;
        
        /**
         * 资源配置
         */
        private ResourceSpec resourceSpec;
        
        /**
         * DDoS状态
         */
        private String ddosStatus;
        
        /**
         * 镜像信息
         */
        private Image image;
        
        /**
         * 业务状态
         */
        private String businessStatus;
        
        /**
         * 公网IP地址
         */
        private String publicIpAddress;
        
        /**
         * 实例名称
         */
        private String instanceName;
        
        /**
         * 内网IP地址
         */
        private String innerIpAddress;
        
        /**
         * 网络属性
         */
        private List<NetworkAttribute> networkAttributes;
        
        /**
         * UUID
         */
        private String uuid;
        
        /**
         * 付费类型
         */
        private String chargeType;
        
        /**
         * 是否组合
         */
        private Boolean combination;
        
        /**
         * 过期时间
         */
        private LocalDateTime expiredTime;
        
        /**
         * 创建时间
         */
        private LocalDateTime creationTime;
        
        /**
         * 镜像ID
         */
        private String imageId;
        
        /**
         * 套餐类型
         */
        private String planType;
        
        /**
         * 磁盘信息
         */
        private List<Disk> disks;
        
        /**
         * 地域ID
         */
        private String regionId;
        
        /**
         * 标签
         */
        private List<Tag> tags;
    }

    /**
     * 资源配置
     */
    @Data
    public static class ResourceSpec {
        /**
         * 内存大小(GB)
         */
        private Integer memory;
        
        /**
         * 带宽(Mbps)
         */
        private Integer bandwidth;
        
        /**
         * 磁盘大小(GB)
         */
        private Integer diskSize;
        
        /**
         * 磁盘类型
         */
        private String diskCategory;
        
        /**
         * CPU核心数
         */
        private Integer cpu;
    }

    /**
     * 镜像信息
     */
    @Data
    public static class Image {
        /**
         * 镜像名称
         */
        private String imageName;
        
        /**
         * 镜像版本
         */
        private String imageVersion;
        
        /**
         * 操作系统类型
         */
        private String osType;
        
        /**
         * 镜像图标URL
         */
        private String imageIconUrl;
        
        /**
         * 镜像联系信息
         */
        private String imageContact;
        
        /**
         * 镜像类型
         */
        private String imageType;
    }

    /**
     * 网络属性
     */
    @Data
    public static class NetworkAttribute {
        /**
         * 公网IP地址
         */
        private String publicIpAddress;
        
        /**
         * 私网IP地址
         */
        private String privateIpAddress;
        
        /**
         * 峰值带宽
         */
        private Integer peakBandwidth;
        
        /**
         * 公网IP DDoS状态
         */
        private String publicIpDdosStatus;
    }

    /**
     * 磁盘信息
     */
    @Data
    public static class Disk {
        /**
         * 状态
         */
        private String status;
        
        /**
         * 磁盘类型
         */
        private String diskType;
        
        /**
         * 磁盘类别
         */
        private String category;
        
        /**
         * 设备名
         */
        private String device;
        
        /**
         * 磁盘大小(GB)
         */
        private Integer size;
        
        /**
         * 创建时间
         */
        private LocalDateTime creationTime;
        
        /**
         * 磁盘付费类型
         */
        private String diskChargeType;
        
        /**
         * 地域ID
         */
        private String regionId;
        
        /**
         * 磁盘名称
         */
        private String diskName;
        
        /**
         * 磁盘ID
         */
        private String diskId;
    }

    /**
     * 标签
     */
    @Data
    public static class Tag {
        /**
         * 标签值
         */
        private String value;
        
        /**
         * 标签键
         */
        private String key;
    }

    /**
     * 实例登录响应
     */
    @Data
    public static class LoginResponse {
        /**
         * 请求ID
         */
        private String requestId;
        
        /**
         * 重定向URL
         */
        private String redirectUrl;
    }
}
