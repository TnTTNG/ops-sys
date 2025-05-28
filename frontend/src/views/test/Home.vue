<template>
  <div class="home">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>WebSocket测试</span>
        </div>
      </template>
      <div class="text item">
        <el-button type="primary" @click="testWebSocket">发送测试消息</el-button>
        <div class="message-box" v-if="message">
          <p>最新消息: {{ message }}</p>
          <p>时间: {{ timestamp }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { websocketClient } from '@/utils/websocket'

const message = ref('')
const timestamp = ref('')

const testWebSocket = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('code_user') || '{}')
    if (!user.token) {
      ElMessage.warning('请先登录')
      return
    }

    const response = await request.post('/websocket/broadcastUserInfo', {
      userInfo: user
    })
    console.log('测试消息发送成功:', response.data)
    ElMessage.success('测试消息发送成功')
  } catch (error) {
    console.error('发送测试消息失败:', error)
    if (error.response) {
      ElMessage.error(error.response.data?.msg || '发送测试消息失败')
    } else {
      ElMessage.error('发送测试消息失败')
    }
  }
}

// 处理WebSocket消息
const handleWebSocketMessage = (data) => {
  console.log('收到WebSocket消息:', data)
  if (data) {
    message.value = JSON.stringify(data, null, 2)
    timestamp.value = new Date().toLocaleString()
  }
}

onMounted(() => {
  // 添加消息处理器
  websocketClient.addMessageHandler(handleWebSocketMessage)
})

onUnmounted(() => {
  // 移除消息处理器
  websocketClient.removeMessageHandler(handleWebSocketMessage)
})
</script>

<style scoped>
.home {
  padding: 20px;
}
.box-card {
  width: 480px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.message-box {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>