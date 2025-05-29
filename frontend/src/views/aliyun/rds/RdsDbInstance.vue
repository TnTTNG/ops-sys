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
          <el-table-column prop="dbInstanceId" label="实例ID" width="220" />
          <el-table-column prop="description" label="描述" width="180" />
          <el-table-column prop="regionId" label="地域" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="engine" label="数据库类型" width="120" />
          <el-table-column prop="engineVersion" label="数据库版本" width="120" />
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column prop="updatedAt" label="更新时间" width="180" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
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

    // const request = axios.create({
    //   baseURL: 'http://localhost:8888',
    //   timeout:30000 // 后台接口请求时间
    // })
    // 获取RDS实例列表
    const getRdsInstances = async () => {
      try {
        loading.value = true
        const res = await request.get('/rds/selectAll')
        if (res.code === '200') {
          tableData.value = res.data
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        ElMessage.error('获取数据失败：' + error.message)
      } finally {
        loading.value = false
      }
    }

    // 同步RDS数据
    const handleSync = async () => {
      try {
        loading.value = true
        const res = await request.get('/rds/sync')
        if (res.code === '200') {
          ElMessage.success(res.data)
          // 同步成功后刷新列表
          await getRdsInstances()
        } else {
          ElMessage.error(res.msg || '同步失败')
        }
      } catch (error) {
        ElMessage.error('同步失败：' + error.message)
      } finally {
        loading.value = false
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

    onMounted(() => {
      getRdsInstances()
    })

    return {
      loading,
      tableData,
      handleSync,
      getStatusType
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