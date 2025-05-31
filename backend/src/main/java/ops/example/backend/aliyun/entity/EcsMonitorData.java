package ops.example.backend.aliyun.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ECS实例监控数据实体类
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-31-19:36
 */
@Data
public class EcsMonitorData {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * CPU使用率(%)
     */
    private BigDecimal cpuUsage;

    /**
     * 突发性能实例积分总数
     */
    private BigDecimal cpuCreditBalance;

    /**
     * 突发性能实例已使用的积分数
     */
    private BigDecimal cpuCreditUsage;

    /**
     * 超额未支付积分
     */
    private BigDecimal cpuNotpaidSurplusCreditUsage;

    /**
     * 超额积分
     */
    private BigDecimal cpuAdvanceCreditBalance;

    /**
     * 云盘读带宽(Byte/s)
     */
    private Long bpsRead;

    /**
     * 云盘写带宽(Byte/s)
     */
    private Long bpsWrite;

    /**
     * 云盘读IOPS(次/s)
     */
    private Long iopsRead;

    /**
     * 云盘写IOPS(次/s)
     */
    private Long iopsWrite;

    /**
     * 公网带宽(kbits/s)
     */
    private Long internetBandwidth;

    /**
     * 内网带宽(kbits/s)
     */
    private Long intranetBandwidth;

    /**
     * 公网入流量(kbits)
     */
    private Long internetRx;

    /**
     * 公网出流量(kbits)
     */
    private Long internetTx;

    /**
     * 内网入流量(kbits)
     */
    private Long intranetRx;

    /**
     * 内网出流量(kbits)
     */
    private Long intranetTx;

    /**
     * 监控时间
     */
    private LocalDateTime monitorTime;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     */
    private LocalDateTime updatedAt;

    private Integer period;

    /**
     * 关联的ECS实例信息
     */
    private DescribeInstances describeInstances;
}
