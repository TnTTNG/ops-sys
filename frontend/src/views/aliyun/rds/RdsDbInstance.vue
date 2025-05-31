<template>
  <div class="rds-db-instance">
    <div class="rds-db-instance-header">
      <div class="rds-db-instance-header-title">
        <div class="rds-db-instance-header-title-text">
          阿里云RDS实例管理
        </div>
      </div>
      <div class="rds-db-instance-header-actions">
        <el-button type="primary" @click="handleSync" :loading="loading">
          <el-icon><Refresh /></el-icon>
          同步数据
        </el-button>
        <el-switch
          v-model="isAutoSync"
          active-text="自动同步"
          @change="handleAutoSyncChange"
        />
      </div>
    </div>
    <div class="rds-db-instance-body">
      <el-card class="rds-db-instance-body-card">
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          border
        >
          <el-table-column prop="dbInstanceId" label="实例ID" width="180" />
          <el-table-column prop="dbInstanceDescription" label="实例描述" width="120" />
          <el-table-column prop="dbInstanceStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.dbInstanceStatus)">
                {{ scope.row.dbInstanceStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="engine" label="数据库类型" width="100" />
          <el-table-column prop="engineVersion" label="数据库版本" width="100" />
          <el-table-column prop="zoneId" label="可用区" width="100" />
          <el-table-column prop="dbInstanceClass" label="实例规格" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              {{ formatCreateTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="vSwitchId" label="交换机ID" width="180" />
          <el-table-column prop="tipsLevel" label="提示级别" width="100" />
          <el-table-column prop="deletionProtection" label="删除保护" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.deletionProtection ? 'success' : 'info'">
                {{ scope.row.deletionProtection ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lockMode" label="锁定模式" width="100" />
          <el-table-column prop="payType" label="付费类型" width="100" />
          <el-table-column prop="dbInstanceStorageType" label="存储类型" width="120" />
          <el-table-column prop="vpcId" label="专有网络ID" width="180" />
          <el-table-column prop="connectionMode" label="连接模式" width="100" />
          <el-table-column prop="connectionString" label="连接地址" width="180" />
          <el-table-column prop="expireTime" label="过期时间" width="180">
            <template #default="scope">
              {{ formatCreateTime(scope.row.expireTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="dbInstanceMemory" label="内存(MB)" width="100" />
          <el-table-column prop="resourceGroupId" label="资源组ID" width="180" />
          <el-table-column prop="dbInstanceNetType" label="网络类型" width="100" />
          <el-table-column prop="dbInstanceType" label="实例类型" width="100" />
          <el-table-column prop="mutriORsignle" label="多可用区" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.mutriORsignle ? 'success' : 'info'">
                {{ scope.row.mutriORsignle ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="instanceNetworkType" label="实例网络类型" width="120" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                link
                @click="handleOpenDMS(scope.row)"
              >
                管理数据库
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
// import axios from "axios";

export default {
  name: 'RdsDbInstance',
  components: {
    Refresh
  },
  setup() {
    const loading = ref(false)
    const tableData = ref([])
    const isAutoSync = ref(false)
    let syncTimer = null

    // const request = axios.create({
    //   baseURL: 'http://localhost:8888',
    //   timeout:30000 // 后台接口请求时间
    // })
    // 获取RDS实例列表
    const getRdsInstances = async () => {
      try {
        loading.value = true
        console.log('开始获取RDS实例数据...')
        const res = await request.get('/rds/selectAll')
        console.log('获取到的原始响应：', res)
        
        if (res.code === '200') {
          // 直接使用res.data，因为后端返回的是数组
          tableData.value = Array.isArray(res.data) ? res.data : []
          console.log('处理后的表格数据：', tableData.value)
        } else {
          console.error('获取数据失败，错误码：', res.code)
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取数据失败，详细错误：', error)
        ElMessage.error('获取数据失败：' + (error.response?.data?.msg || error.message))
      } finally {
        loading.value = false
      }
    }

    // 同步RDS数据
    const handleSync = async () => {
      try {
        loading.value = true
        console.log('开始同步RDS数据...')
        const res = await request.get('/rds/sync')
        console.log('同步响应：', res)
        if (res.code === '200') {
          ElMessage.success(res.data)
          // 同步成功后刷新列表
          await getRdsInstances()
        } else {
          console.error('同步失败，错误码：', res.code)
          ElMessage.error(res.msg || '同步失败')
        }
      } catch (error) {
        console.error('同步失败，详细错误：', error)
        ElMessage.error('同步失败：' + (error.response?.data?.msg || error.message))
      } finally {
        loading.value = false
      }
    }

    // 处理自动同步开关变化
    const handleAutoSyncChange = (value) => {
      if (value) {
        // 开启自动同步
        syncTimer = setInterval(() => {
          handleSync()
        }, 5000) // 每5秒同步一次
      } else {
        // 关闭自动同步
        if (syncTimer) {
          clearInterval(syncTimer)
          syncTimer = null
        }
      }
    }

    // 根据状态返回不同的标签类型
    const getStatusType = (status) => {
      const statusMap = {
        'Running': 'success',
        'Creating': 'warning',
        'Deleting': 'danger',
        'Restarting': 'warning',
        'Upgrading': 'warning'
      }
      return statusMap[status] || 'info'
    }

    // 格式化创建时间
    const formatCreateTime = (time) => {
      if (!time) return ''
      return time.replace('T', ' ').replace('Z', '')
    }

    // 打开DMS控制台
    const handleOpenDMS = (row) => {
      const dmsUrl = `http://47.113.207.208/adminer?server=${row.connectionString}`
      window.open(dmsUrl, '_blank')
    }

    onMounted(() => {
      getRdsInstances()
    })

    onUnmounted(() => {
      // 组件卸载时清除定时器
      if (syncTimer) {
        clearInterval(syncTimer)
        syncTimer = null
      }
    })

    return {
      loading,
      tableData,
      isAutoSync,
      handleSync,
      handleAutoSyncChange,
      getStatusType,
      formatCreateTime,
      handleOpenDMS
    }
  }
}
</script>

<style lang="less" scoped>
.rds-db-instance {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  &-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    &-title {
      &-text {
        font-size: 20px;
        font-weight: bold;
        color: #303133;
      }
    }

    &-actions {
      display: flex;
      gap: 10px;
      align-items: center;
    }
  }

  &-body {
    flex: 1;
    overflow: hidden;

    &-card {
      height: 100%;
      display: flex;
      flex-direction: column;

      :deep(.el-card__body) {
        flex: 1;
        overflow: auto;
        padding: 0;
      }
    }
  }
}

:deep(.el-table) {
  .el-table__header-wrapper {
    th {
      background-color: #f5f7fa;
      color: #606266;
      font-weight: bold;
    }
  }
}
</style>