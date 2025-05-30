<template>
  <div class="describe-load-balancers">
    <div class="describe-load-balancers-header">
      <div class="describe-load-balancers-header-title">
        <div class="describe-load-balancers-header-title-text">
          阿里云SLB负载均衡实例管理
        </div>
      </div>
      <div class="describe-load-balancers-header-actions">
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
    <div class="describe-load-balancers-body">
      <el-card class="describe-load-balancers-body-card">
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          border
        >
          <el-table-column prop="loadBalancerId" label="实例ID" width="180" />
          <el-table-column prop="loadBalancerName" label="实例名称" width="120" />
          <el-table-column prop="loadBalancerStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.loadBalancerStatus)">
                {{ scope.row.loadBalancerStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="服务地址" width="120" />
          <el-table-column prop="regionId" label="地域ID" width="100" />
<!--          <el-table-column prop="regionIdAlias" label="地域别名" width="100" />-->
          <el-table-column prop="masterZoneId" label="主可用区" width="110" />
          <el-table-column prop="slaveZoneId" label="备可用区" width="110" />
<!--          <el-table-column prop="networkType" label="网络类型" width="100" />-->
          <el-table-column prop="bandwidth" label="带宽(Mbps)" width="120" />
          <el-table-column prop="loadBalancerSpec" label="规格" width="120" />
<!--          <el-table-column prop="payType" label="付费类型" width="120" />-->
          <el-table-column prop="instanceChargeType" label="实例付费类型" width="120" />
<!--          <el-table-column prop="internetChargeType" label="公网付费类型" width="120" />-->
          <el-table-column prop="internetChargeTypeAlias" label="公网付费类型别名" width="120" />
          <el-table-column prop="resourceGroupId" label="资源组ID" width="180" />
          <el-table-column prop="vpcId" label="专有网络ID" width="180" />
          <el-table-column prop="vSwitchId" label="交换机ID" width="180" />
<!--          <el-table-column prop="addressType" label="地址类型" width="120" />-->
          <el-table-column prop="addressIPVersion" label="IP版本" width="120" />
          <el-table-column prop="deleteProtection" label="删除保护" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              {{ formatCreateTime(scope.row.createTime) }}
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

export default {
  name: 'DescribeLoadBalancers',
  components: {
    Refresh
  },
  setup() {
    const loading = ref(false)
    const tableData = ref([])
    const isAutoSync = ref(false)
    let syncTimer = null

    // 获取负载均衡实例列表
    const getLoadBalancers = async () => {
      try {
        loading.value = true
        const res = await request.get('/slb/selectAll', {
          params: {
            pageNum: 1,
            pageSize: 100
          }
        })
        if (res.code === '200' && res.data) {
          tableData.value = res.data.list || []
          console.log('获取到的数据：', tableData.value) // 添加日志
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取数据失败：', error) // 添加错误日志
        ElMessage.error('获取数据失败：' + (error.response?.data?.msg || error.message))
      } finally {
        loading.value = false
      }
    }

    // 同步负载均衡数据
    const handleSync = async () => {
      try {
        loading.value = true
        const res = await request.get('/slb/sync')
        if (res.code === '200') {
          ElMessage.success(res.data)
          // 同步成功后刷新列表
          await getLoadBalancers()
        } else {
          ElMessage.error(res.msg || '同步失败')
        }
      } catch (error) {
        console.error('同步失败：', error) // 添加错误日志
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
        'active': 'success',
        'inactive': 'info',
        'locked': 'warning',
        'deleted': 'danger'
      }
      return statusMap[status] || 'info'
    }

    // 格式化创建时间
    const formatCreateTime = (time) => {
      if (!time) return ''
      return time.replace('T', ' ').replace('Z', '')
    }

    onMounted(() => {
      getLoadBalancers()
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
      formatCreateTime
    }
  }
}
</script>

<style lang="less" scoped>
.describe-load-balancers {
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
