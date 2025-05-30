-- 删除旧表
DROP TABLE IF EXISTS rds_db_instance;

-- 创建新表
CREATE TABLE rds_db_instance (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    db_instance_id VARCHAR(64) NOT NULL COMMENT 'RDS实例ID',
    db_instance_description VARCHAR(256) COMMENT 'RDS实例描述',
    region_id VARCHAR(32) COMMENT '地域ID',
    db_instance_status VARCHAR(32) COMMENT 'RDS实例状态',
    engine VARCHAR(32) COMMENT '数据库引擎',
    engine_version VARCHAR(32) COMMENT '数据库引擎版本',
    zone_id VARCHAR(32) COMMENT '可用区ID',
    db_instance_class VARCHAR(64) COMMENT '实例规格',
    create_time VARCHAR(32) COMMENT '创建时间',
    v_switch_id VARCHAR(64) COMMENT '交换机ID',
    tips_level INT COMMENT '提示级别',
    deletion_protection BOOLEAN COMMENT '删除保护',
    lock_mode VARCHAR(32) COMMENT '锁定模式',
    pay_type VARCHAR(32) COMMENT '付费类型',
    db_instance_storage_type VARCHAR(32) COMMENT '存储类型',
    vpc_id VARCHAR(64) COMMENT '专有网络ID',
    connection_mode VARCHAR(32) COMMENT '连接模式',
    connection_string VARCHAR(128) COMMENT '连接地址',
    expire_time VARCHAR(32) COMMENT '过期时间',
    db_instance_memory INT COMMENT '实例内存(MB)',
    resource_group_id VARCHAR(64) COMMENT '资源组ID',
    db_instance_net_type VARCHAR(32) COMMENT '网络类型',
    db_instance_type VARCHAR(32) COMMENT '实例类型',
    mutri_orsignle BOOLEAN COMMENT '是否多可用区',
    instance_network_type VARCHAR(32) COMMENT '实例网络类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_db_instance_id (db_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RDS数据库实例表'; 