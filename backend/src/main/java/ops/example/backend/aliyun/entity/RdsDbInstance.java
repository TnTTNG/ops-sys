// 文件路径: src/main/java/ops/example/backend/entity/RdsDbInstance.java
package ops.example.backend.aliyun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-21-10:00
 * @role 阿里云 RDS 数据库实例信息
 */
public class RdsDbInstance {
    private Integer id;
    private String dbInstanceId;
    private String dbInstanceDescription;
    private String regionId;
    private String dbInstanceStatus;
    private String engine;
    private String engineVersion;
    private String zoneId;
    private String dbInstanceClass;
    private String createTime;
    private String vSwitchId;
    private Integer tipsLevel;
    private Boolean deletionProtection;
    private String lockMode;
    private String payType;
    private String dbInstanceStorageType;
    private String vpcId;
    private String connectionMode;
    private String connectionString;
    private String expireTime;
    private Integer dbInstanceMemory;
    private String resourceGroupId;
    private String dbInstanceNetType;
    private String dbInstanceType;
    private Boolean mutriORsignle;
    private String instanceNetworkType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDbInstanceId() {
        return dbInstanceId;
    }

    public void setDbInstanceId(String dbInstanceId) {
        this.dbInstanceId = dbInstanceId;
    }

    public String getDbInstanceDescription() {
        return dbInstanceDescription;
    }

    public void setDbInstanceDescription(String dbInstanceDescription) {
        this.dbInstanceDescription = dbInstanceDescription;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getDbInstanceStatus() {
        return dbInstanceStatus;
    }

    public void setDbInstanceStatus(String dbInstanceStatus) {
        this.dbInstanceStatus = dbInstanceStatus;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getDbInstanceClass() {
        return dbInstanceClass;
    }

    public void setDbInstanceClass(String dbInstanceClass) {
        this.dbInstanceClass = dbInstanceClass;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVSwitchId() {
        return vSwitchId;
    }

    public void setVSwitchId(String vSwitchId) {
        this.vSwitchId = vSwitchId;
    }

    public Integer getTipsLevel() {
        return tipsLevel;
    }

    public void setTipsLevel(Integer tipsLevel) {
        this.tipsLevel = tipsLevel;
    }

    public Boolean getDeletionProtection() {
        return deletionProtection;
    }

    public void setDeletionProtection(Boolean deletionProtection) {
        this.deletionProtection = deletionProtection;
    }

    public String getLockMode() {
        return lockMode;
    }

    public void setLockMode(String lockMode) {
        this.lockMode = lockMode;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getDbInstanceStorageType() {
        return dbInstanceStorageType;
    }

    public void setDbInstanceStorageType(String dbInstanceStorageType) {
        this.dbInstanceStorageType = dbInstanceStorageType;
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public String getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(String connectionMode) {
        this.connectionMode = connectionMode;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getDbInstanceMemory() {
        return dbInstanceMemory;
    }

    public void setDbInstanceMemory(Integer dbInstanceMemory) {
        this.dbInstanceMemory = dbInstanceMemory;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    public String getDbInstanceNetType() {
        return dbInstanceNetType;
    }

    public void setDbInstanceNetType(String dbInstanceNetType) {
        this.dbInstanceNetType = dbInstanceNetType;
    }

    public String getDbInstanceType() {
        return dbInstanceType;
    }

    public void setDbInstanceType(String dbInstanceType) {
        this.dbInstanceType = dbInstanceType;
    }

    public Boolean getMutriORsignle() {
        return mutriORsignle;
    }

    public void setMutriORsignle(Boolean mutriORsignle) {
        this.mutriORsignle = mutriORsignle;
    }

    public String getInstanceNetworkType() {
        return instanceNetworkType;
    }

    public void setInstanceNetworkType(String instanceNetworkType) {
        this.instanceNetworkType = instanceNetworkType;
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
}
