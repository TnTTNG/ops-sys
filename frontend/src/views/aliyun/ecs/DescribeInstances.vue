<template>
  <div class="describe-instances">
    <div class="describe-instances-header">
      <div class="describe-instances-header-title">
        <div class="describe-instances-header-title-text">
          阿里云实例管理
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
          <el-table-column prop="instanceId" label="实例ID" width="184" />
          <el-table-column prop="instanceName" label="实例名称" width="180" />
<!--          <el-table-column prop="hostName" label="主机名" width="200" />-->
          <el-table-column prop="status" label="状态" width="95">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="instanceType" label="实例规格" width="120" />
          <el-table-column prop="cpu" label="CPU核心数" width="100" />
          <el-table-column prop="memory" label="内存(MB)" width="100" />
          <el-table-column prop="osName" label="操作系统" width="146" />
          <el-table-column prop="publicIpAddress" label="公网IP" width="130" />
          <el-table-column prop="privateIpAddress" label="私网IP" width="130" />
          <el-table-column prop="regionId" label="地域" width="100" />
          <el-table-column prop="zoneId" label="可用区" width="120" />
          <el-table-column prop="creationTime" label="创建时间" width="180" />
          <el-table-column prop="instanceSource" label="实例来源" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.instanceSource === 'ECS' ? 'primary' : 'success'">
                {{ scope.row.instanceSource }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                link
                :disabled="scope.row.status !== 'Running' || connectingInstance === scope.row.instanceId"
                @click="handleConnect(scope.row)"
                :loading="connectingInstance === scope.row.instanceId"
              >
                {{ connectingInstance === scope.row.instanceId ? '连接中' : '远程连接' }}
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleMonitor(scope.row)"
              >
                监控
              </el-button>
              <el-button
                type="warning"
                link
                :disabled="scope.row.status !== 'Running' || scope.row.restarting"
                @click="handleRestart(scope.row)"
                :loading="scope.row.restarting"
              >
                重启
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

export default {
  name: 'DescribeInstances',
  components: {
    Refresh
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const tableData = ref([])
    const isAutoSync = ref(false)
    let syncTimer = null

    // 获取实例列表
    const getInstances = async () => {
      try {
        loading.value = true
        // 并行请求ECS和SWAS实例数据
        const [ecsRes, swasRes] = await Promise.all([
          request.get('/ecs/selectAll', {
            params: {
              pageNum: 1,
              pageSize: 100
            }
          }),
          request.get('/swas/selectAll', {
            params: {
              pageNum: 1,
              pageSize: 100
            }
          })
        ])

        // 处理ECS实例数据
        const ecsInstances = ecsRes.code === '200' ? (ecsRes.data.list || []).map(instance => ({
          ...instance,
          instanceSource: 'ECS'
        })) : []

        // 处理SWAS实例数据
        const swasInstances = swasRes.code === '200' ? (swasRes.data.list || []).map(instance => ({
          ...instance,
          instanceSource: 'SWAS'
        })) : []

        // 合并数据并按创建时间排序
        tableData.value = [...ecsInstances, ...swasInstances].sort((a, b) => {
          return new Date(b.creationTime) - new Date(a.creationTime)
        })

        console.log('获取到的数据：', tableData.value)
      } catch (error) {
        console.error('获取数据失败：', error)
        ElMessage.error('获取数据失败：' + (error.response?.data?.msg || error.message))
      } finally {
        loading.value = false
      }
    }

    // 同步实例数据
    const handleSync = async () => {
      try {
        loading.value = true
        // 并行同步ECS和SWAS数据
        const [ecsRes, swasRes] = await Promise.all([
          request.get('/ecs/sync'),
          request.get('/swas/sync')
        ])

        if (ecsRes.code === '200' && swasRes.code === '200') {
          ElMessage.success('数据同步成功')
          // 同步成功后刷新列表
          await getInstances()
        } else {
          ElMessage.error('同步失败：' + (ecsRes.msg || swasRes.msg))
        }
      } catch (error) {
        console.error('同步失败：', error)
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
        'Starting': 'warning',
        'Stopping': 'warning',
        'Stopped': 'info',
        'Pending': 'warning',
        'Creating': 'warning',
        'Deleting': 'danger',
        'Rebooting': 'warning',
        'Expired': 'danger',
        'Normal': 'success',  // SWAS状态
        'Abnormal': 'danger'  // SWAS状态
      }
      return statusMap[status] || 'info'
    }

    // 处理远程连接
    const handleConnect = async (instance) => {
      // 检查实例状态
      if (instance.status !== 'Running') {
        ElMessage.warning('实例未运行，无法连接')
        return
      }
      
      try {
        let res
        // 根据实例来源调用不同的接口
        if (instance.instanceSource === 'SWAS') {
          // 调用SWAS登录接口
          res = await request.get('/swas/login', {
            params: {
              instanceId: instance.instanceId
            }
          })
          // 如果成功，直接使用redirectUrl
          if (res.code === '200' && res.data?.redirectUrl) {
            window.open(res.data.redirectUrl, '_blank')
            return
          }
        } else {
          // 调用ECS VNC接口
          res = await request.get('/vnc/open', {
            params: {
              instanceId: instance.instanceId
            }
          })
          // 如果成功，直接使用返回的URL
          if (res.code === '200' && res.data) {
            window.open(res.data, '_blank')
            return
          }
        }
        
        // 如果走到这里，说明接口调用失败
        ElMessage.error(res.msg || '获取连接地址失败')
      } catch (error) {
        console.error('获取连接地址失败:', error)
        ElMessage.error('获取连接地址失败：' + (error.response?.data?.msg || error.message))
      }
    }

    // 处理监控按钮点击
    const handleMonitor = (instance) => {
      // 将实例ID存储到localStorage
      localStorage.setItem('monitorInstanceId', instance.instanceId)
      // 跳转到监控页面
      router.push('/manager/monitor')
    }

    // 处理重启按钮点击
    const handleRestart = async (row) => {
      try {
        // 添加确认对话框
        await ElMessageBox.confirm(
          `确定要重启实例 ${row.instanceId} 吗？`,
          '重启确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        // 设置当前行的重启状态
        row.restarting = true

        const res = await request.post('/ecs/restart', {
          instanceId: row.instanceId
        })

        if (res.code === '200') {
          ElMessage.success(res.data)
          // 等待3秒后同步数据，给实例重启留出时间
          setTimeout(async () => {
            try {
              const syncRes = await request.get('/ecs/sync')
              if (syncRes.code === '200') {
                ElMessage.success(syncRes.data)
                // 同步成功后刷新列表
                await getInstances()
              } else {
                ElMessage.error(syncRes.msg || '同步失败')
              }
            } catch (error) {
              console.error('同步失败，详细错误：', error)
              ElMessage.error(error.response?.data?.msg || error.message || '同步失败')
            }
          }, 3000)
        } else {
          ElMessage.error(res.msg || '重启失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('重启失败，详细错误：', error)
          ElMessage.error(error.response?.data?.msg || error.message || '重启失败')
        }
      } finally {
        // 清除重启状态
        row.restarting = false
      }
    }

    onMounted(() => {
      getInstances()
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
      handleConnect,
      handleMonitor,
      handleRestart
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
