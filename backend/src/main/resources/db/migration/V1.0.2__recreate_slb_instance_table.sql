-- 删除旧表
DROP TABLE IF EXISTS slb_instance;

-- 创建新表
CREATE TABLE slb_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    load_balancer_id VARCHAR(64) NOT NULL COMMENT '负载均衡实例ID',
    load_balancer_name VARCHAR(128) COMMENT '负载均衡实例名称',
    load_balancer_status VARCHAR(32) COMMENT '负载均衡实例状态',
    address VARCHAR(128) COMMENT '负载均衡实例服务地址',
    region_id VARCHAR(32) COMMENT '地域ID',
    region_id_alias VARCHAR(32) COMMENT '地域别名',
    master_zone_id VARCHAR(32) COMMENT '主可用区ID',
    slave_zone_id VARCHAR(32) COMMENT '备可用区ID',
    network_type VARCHAR(32) COMMENT '网络类型',
    bandwidth INT COMMENT '带宽',
    load_balancer_spec VARCHAR(32) COMMENT '负载均衡实例规格',
    pay_type VARCHAR(32) COMMENT '付费类型',
    instance_charge_type VARCHAR(32) COMMENT '实例付费类型',
    internet_charge_type VARCHAR(32) COMMENT '公网付费类型',
    internet_charge_type_alias VARCHAR(32) COMMENT '公网付费类型别名',
    resource_group_id VARCHAR(32) COMMENT '资源组ID',
    vpc_id VARCHAR(32) COMMENT '专有网络ID',
    v_switch_id VARCHAR(32) COMMENT '交换机ID',
    address_type VARCHAR(32) COMMENT '地址类型',
    address_ip_version VARCHAR(32) COMMENT 'IP版本',
    delete_protection VARCHAR(32) COMMENT '删除保护',
    create_time VARCHAR(32) COMMENT '创建时间',
    create_time_stamp BIGINT COMMENT '创建时间戳',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    UNIQUE KEY uk_load_balancer_id (load_balancer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阿里云SLB负载均衡实例表'; 