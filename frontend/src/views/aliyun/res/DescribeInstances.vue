<template>
  <div class="describe-instances">
    <div class="describe-instances-header">
      <div class="describe-instances-header-title">
        <div class="describe-instances-header-title-text">
          阿里云ECS实例管理
        </div>
      </div>
      <div class="describe-instances-header-actions">
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
    <div class="describe-instances-body">
      <el-card class="describe-instances-body-card">
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          border
        >
          <el-table-column prop="instanceId" label="实例ID" width="220" />
          <el-table-column prop="instanceName" label="实例名称" width="180" />
          <el-table-column prop="hostName" label="主机名" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="instanceType" label="实例规格" width="120" />
          <el-table-column prop="cpu" label="CPU" width="80" />
          <el-table-column prop="memory" label="内存(MB)" width="100" />
          <el-table-column prop="osName" label="操作系统" width="180" />
          <el-table-column prop="publicIpAddress" label="公网IP" width="120" />
          <el-table-column prop="privateIpAddress" label="私网IP" width="120" />
          <el-table-column prop="regionId" label="地域" width="100" />
          <el-table-column prop="zoneId" label="可用区" width="120" />
          <el-table-column prop="creationTime" label="创建时间" width="180" />
          <el-table-column prop="startTime" label="启动时间" width="180" />
          <el-table-column prop="expiredTime" label="过期时间" width="180" />
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

export default {
  name: 'DescribeInstances',
  components: {
    Refresh
  },
  setup() {
    const loading = ref(false)
    const tableData = ref([])
    const isAutoSync = ref(false)
    let syncTimer = null

    // 获取ECS实例列表
    const getEcsInstances = async () => {
      try {
        loading.value = true
        const res = await request.get('/ecs/selectAll')
        if (res.code === '200') {
          tableData.value = res.data.list
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        ElMessage.error('获取数据失败：' + error.message)
      } finally {
        loading.value = false
      }
    }

    // 同步ECS数据
    const handleSync = async () => {
      try {
        loading.value = true
        const res = await request.get('/ecs/sync')
        if (res.code === '200') {
          ElMessage.success(res.data)
          // 同步成功后刷新列表
          await getEcsInstances()
        } else {
          ElMessage.error(res.msg || '同步失败')
        }
      } catch (error) {
        ElMessage.error('同步失败：' + error.message)
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
        'Starting': 'warning',
        'Stopping': 'warning',
        'Stopped': 'info',
        'Pending': 'warning',
        'Creating': 'warning',
        'Deleting': 'danger',
        'Rebooting': 'warning',
        'Expired': 'danger'
      }
      return statusMap[status] || 'info'
    }

    onMounted(() => {
      getEcsInstances()
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
      getStatusType
    }
  }
}
</script>

<style lang="less" scoped>
.describe-instances {
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
