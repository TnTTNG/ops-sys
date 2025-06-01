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
          <el-table-column label="操作" width="160" fixed="right">
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
                v-if="terminalVisible && currentInstance && currentInstance.instanceId === scope.row.instanceId"
                type="danger"
                link
                @click="handleCloseTerminal"
              >
                断开连接
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleMonitor(scope.row)"
              >
                监控
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- SSH终端对话框 -->
    <el-dialog
      v-model="terminalVisible"
      title="SSH终端"
      width="80%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="true"
      :before-close="handleBeforeCloseTerminal"
      @close="handleCloseCompleted"
    >
      <div class="terminal-container">
        <div ref="terminalRef" class="terminal"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { Terminal } from '@xterm/xterm'
import { FitAddon } from '@xterm/addon-fit'
import '@xterm/xterm/css/xterm.css'
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

    // 终端相关
    const terminalVisible = ref(false)
    const terminalRef = ref(null)
    const terminal = ref(null)
    const fitAddon = ref(null)
    const ws = ref(null)
    const currentInstance = ref(null)
    const connectingInstance = ref(null)
    const commandHistory = ref([])
    const historyIndex = ref(-1)
    const currentCommand = ref('')

    // 初始化终端
    const initTerminal = () => {
      if (!terminalRef.value) return

      terminal.value = new Terminal({
        cursorBlink: true,
        fontSize: 14,
        fontFamily: 'Consolas, "Courier New", monospace',
        theme: {
          background: '#1e1e1e',
          foreground: '#ffffff'
        }
      })

      fitAddon.value = new FitAddon()
      terminal.value.loadAddon(fitAddon.value)
      terminal.value.open(terminalRef.value)
      fitAddon.value.fit()

      // 显示调试信息
      terminal.value.write('正在连接到终端...\r\n')

      // 监听终端输入
      terminal.value.onData(data => {
        console.log('终端输入:', data.charCodeAt(0), data)
        if (ws.value) {
          // 检查连接状态
          if (ws.value.readyState !== WebSocket.OPEN) {
            const stateMap = {
              0: 'CONNECTING',
              1: 'OPEN',
              2: 'CLOSING',
              3: 'CLOSED'
            }
            console.error(`WebSocket未连接，当前状态: ${stateMap[ws.value.readyState]}`)
            terminal.value.write(`\r\n当前WebSocket状态: ${stateMap[ws.value.readyState]}\r\n`)
            
            // 如果已关闭，尝试重连
            if (ws.value.readyState === WebSocket.CLOSED) {
              terminal.value.write('\r\n正在尝试重新连接...\r\n')
              connectWebSocket(currentInstance.value)
            } else {
              terminal.value.write('\r\n等待连接完成...\r\n')
            }
            return
          }
          
          // 处理特殊键
          if (data.charCodeAt(0) === 27 && data.length > 2) {
            // 处理方向键
            const thirdChar = data.charCodeAt(2)
            if (thirdChar === 65) {
              // 上键 - 向上浏览历史命令
              if (commandHistory.value.length > 0 && historyIndex.value < commandHistory.value.length - 1) {
                // 保存当前命令（如果在历史底部）
                if (historyIndex.value === -1) {
                  currentCommand.value = getCurrentCommand()  // 当前命令行内容
                }
                
                historyIndex.value++
                const prevCommand = commandHistory.value[commandHistory.value.length - 1 - historyIndex.value]
                
                // 清除当前行并写入历史命令
                ws.value.send(JSON.stringify({
                  type: 'ssh_input',
                  data: 'HISTORY_UP',
                  instanceId: currentInstance.value.instanceId,
                  historyCommand: prevCommand
                }))
              }
              return
            } else if (thirdChar === 66) {
              // 下键 - 向下浏览历史命令
              if (historyIndex.value > -1) {
                historyIndex.value--
                
                let nextCommand
                if (historyIndex.value === -1) {
                  nextCommand = currentCommand.value
                } else {
                  nextCommand = commandHistory.value[commandHistory.value.length - 1 - historyIndex.value]
                }
                
                // 清除当前行并写入历史命令或空行
                ws.value.send(JSON.stringify({
                  type: 'ssh_input',
                  data: 'HISTORY_DOWN',
                  instanceId: currentInstance.value.instanceId,
                  historyCommand: nextCommand || ''
                }))
              }
              return
            }
          }
          
          // 回车键，可能需要保存命令历史
          if (data === '\r') {
            // 重置历史浏览位置
            historyIndex.value = -1
            // 获取当前命令并发送完整命令信息
            const command = getCurrentCommand()
            try {
              ws.value.send(JSON.stringify({
                type: 'ssh_input',
                data: data,
                instanceId: currentInstance.value.instanceId,
                command: command // 发送完整命令
              }))
            } catch (error) {
              console.error('发送命令失败:', error)
              terminal.value.write(`\r\n发送命令失败: ${error.message}\r\n`)
            }
            return
          }
          
          // 发送普通输入
          try {
            ws.value.send(JSON.stringify({
              type: 'ssh_input',
              data: data,
              instanceId: currentInstance.value.instanceId
            }))
          } catch (error) {
            console.error('发送输入失败:', error)
            terminal.value.write(`\r\n发送输入失败: ${error.message}\r\n`)
          }
        } else {
          console.error('WebSocket未初始化')
          terminal.value.write('\r\nWebSocket未初始化，请重新连接\r\n')
        }
      })
      
      // 获取当前命令行内容
      const getCurrentCommand = () => {
        // 实际项目中可能需要从终端缓冲区获取
        // 简化处理，这里依赖后端命令缓冲区
        return ''
      }
      
      // 监听终端大小变化
      window.addEventListener('resize', () => {
        if (fitAddon.value) {
          setTimeout(() => {
            fitAddon.value.fit()
          }, 100)
        }
      })
    }

    // 连接WebSocket
    const connectWebSocket = (instance) => {
      const token = localStorage.getItem('token')
      
      try {
        // 修复连接地址，确保token正确传递
        const wsUrl = `ws://localhost:9999/websocket/user?token=${encodeURIComponent(token)}`
        
        console.log('连接WebSocket:', wsUrl)
        terminal.value.write(`尝试连接: ${wsUrl}\r\n`)
        
        ws.value = new WebSocket(wsUrl)

        // 设置超时检测
        const connectTimeout = setTimeout(() => {
          if (ws.value && ws.value.readyState === WebSocket.CONNECTING) {
            terminal.value.write('\r\n连接超时，请检查后端服务是否正常运行\r\n')
            ElMessage.error('WebSocket连接超时')
            ws.value.close()
          }
        }, 5000) // 5秒超时

        ws.value.onopen = () => {
          clearTimeout(connectTimeout) // 清除超时检测
          console.log('WebSocket连接已建立')
          terminal.value.write('WebSocket连接已建立\r\n')
          
          // 发送连接实例信息
          const connectData = {
            type: 'ssh_connect',
            data: {
              instanceId: instance.instanceId,
              publicIp: instance.publicIpAddress
            }
          }
          console.log('发送连接请求:', connectData)
          ws.value.send(JSON.stringify(connectData))
        }

        ws.value.onmessage = (event) => {
          try {
            const data = event.data;
            console.log('收到WebSocket消息:', data);
            
            // 增加更详细的日志
            try {
              const dataObj = JSON.parse(data);
              console.log('解析后的消息:', {
                type: dataObj.type,
                dataLength: dataObj.data ? dataObj.data.length : 0,
                command: dataObj.command,
                hasData: !!dataObj.data
              });
              
              // 如果是输出消息且有内容，打印前20个字符用于调试
              if (dataObj.type === 'ssh_output' && dataObj.data) {
                const preview = dataObj.data.substring(0, Math.min(dataObj.data.length, 20));
                console.log('输出预览:', preview.replace(/\r/g, '\\r').replace(/\n/g, '\\n'));
              }
            } catch (e) {
              console.error('解析消息详情失败:', e);
            }
            
            const message = JSON.parse(data);
            
            // 处理SSH输出
            if (message.type === 'ssh_output' && terminal.value) {
              // 确保输出内容是字符串
              const outputData = message.data || '';
              console.log('写入终端数据, 长度:', outputData.length);
              
              // 写入终端
              terminal.value.write(outputData);
              
              // 检查是否是命令执行完成的消息（含有提示符）
              if (outputData.includes('\r\n$ ') && message.command) {
                // 如果有执行的命令，添加到历史
                const cmd = message.command.trim();
                if (cmd && !commandHistory.value.includes(cmd)) {
                  commandHistory.value.push(cmd);
                  // 限制历史数量
                  if (commandHistory.value.length > 50) {
                    commandHistory.value.shift();
                  }
                  console.log('添加命令到历史:', cmd);
                }
              }
            } 
            // 处理错误消息
            else if (message.type === 'error') {
              const errorMsg = message.message || '未知错误';
              console.error('收到错误消息:', errorMsg);
              terminal.value.write(`\r\n错误: ${errorMsg}\r\n`);
              ElMessage.error(errorMsg);
            }
          } catch (error) {
            console.error('处理WebSocket消息失败:', error);
            if (terminal.value) {
              terminal.value.write(`\r\n处理消息失败: ${error.message}\r\n`);
            }
          }
        }

        ws.value.onerror = (error) => {
          console.error('WebSocket错误:', error)
          terminal.value.write(`\r\nWebSocket错误: ${error}\r\n`)
          ElMessage.error('连接失败，请重试')
        }

        ws.value.onclose = (event) => {
          console.log('WebSocket连接已关闭:', event.code, event.reason)
          terminal.value.write(`\r\nWebSocket连接已关闭: ${event.code} ${event.reason}\r\n`)
        }
      } catch (error) {
        console.error('创建WebSocket连接失败:', error)
        terminal.value.write(`\r\n创建WebSocket连接失败: ${error.message}\r\n`)
        ElMessage.error(`创建WebSocket连接失败: ${error.message}`)
      }
    }

    // 处理远程连接
    const handleConnect = (instance) => {
      // 检查实例状态
      if (instance.status !== 'Running') {
        ElMessage.warning('实例未运行，无法连接')
        return
      }
      
      // 检查是否有公网IP
      if (!instance.publicIpAddress) {
        ElMessage.warning('实例没有公网IP，无法连接')
        return
      }
      
      // 检查是否已经在连接中
      if (connectingInstance.value) {
        ElMessage.warning('正在连接其他实例，请等待或断开当前连接')
        return
      }
      
      // 如果WebSocket仍然存在，先关闭
      if (ws.value) {
        try {
          ws.value.close()
          ws.value = null
        } catch (error) {
          console.error('关闭旧WebSocket连接失败:', error)
        }
      }
      
      // 如果终端仍然存在，先释放
      if (terminal.value) {
        try {
          terminal.value.dispose()
          terminal.value = null
        } catch (error) {
          console.error('释放旧终端资源失败:', error)
        }
      }

      currentInstance.value = instance
      terminalVisible.value = true
      connectingInstance.value = instance.instanceId
      
      console.log(`准备连接实例: ${instance.instanceId}, IP: ${instance.publicIpAddress}`)
      
      // 等待DOM更新后初始化终端
      setTimeout(() => {
        if (terminalRef.value) {
          initTerminal()
          connectWebSocket(instance)
        } else {
          console.error('终端DOM元素未找到')
          ElMessage.error('终端初始化失败，请重试')
          connectingInstance.value = null
        }
      }, 300) // 增加延迟，确保DOM已更新
    }

    // 关闭终端
    const handleCloseTerminal = () => {
      try {
        if (ws.value) {
          // 只有当连接已经建立时才发送断开连接消息
          if (ws.value.readyState === WebSocket.OPEN) {
            try {
              ws.value.send(JSON.stringify({
                type: 'ssh_disconnect',
                data: {
                  instanceId: currentInstance.value?.instanceId
                }
              }))
            } catch (error) {
              console.error('发送断开连接消息失败:', error)
            }
          }
          
          // 关闭WebSocket连接
          try {
            ws.value.close()
          } catch (error) {
            console.error('关闭WebSocket连接失败:', error)
          }
          ws.value = null
        }
        
        // 释放终端资源
        if (terminal.value) {
          try {
            terminal.value.dispose()
          } catch (error) {
            console.error('释放终端资源失败:', error)
          }
          terminal.value = null
        }
        
        terminalVisible.value = false
        currentInstance.value = null
        connectingInstance.value = null
      } catch (error) {
        console.error('关闭终端时发生错误:', error)
        // 强制重置状态
        ws.value = null
        terminal.value = null
        terminalVisible.value = false
        currentInstance.value = null
        connectingInstance.value = null
      }
    }

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

    // 对话框关闭前处理
    const handleBeforeCloseTerminal = (done) => {
      console.log('准备关闭终端对话框')
      
      // 先关闭WebSocket连接和终端
      if (ws.value) {
        try {
          if (ws.value.readyState === WebSocket.OPEN) {
            // 发送断开连接消息
            ws.value.send(JSON.stringify({
              type: 'ssh_disconnect',
              data: {
                instanceId: currentInstance.value?.instanceId
              }
            }))
          }
          ws.value.close()
        } catch (error) {
          console.error('关闭WebSocket连接失败:', error)
        }
        ws.value = null
      }
      
      // 释放终端资源
      if (terminal.value) {
        try {
          terminal.value.dispose()
        } catch (error) {
          console.error('释放终端资源失败:', error)
        }
        terminal.value = null
      }
      
      // 继续关闭对话框
      done()
    }
    
    // 对话框关闭完成后的处理
    const handleCloseCompleted = () => {
      console.log('终端对话框已关闭')
      
      // 重置状态
      currentInstance.value = null
      connectingInstance.value = null
      
      // 重置命令历史浏览
      historyIndex.value = -1
      currentCommand.value = ''
    }

    // 处理监控按钮点击
    const handleMonitor = (instance) => {
      // 将实例ID存储到localStorage
      localStorage.setItem('monitorInstanceId', instance.instanceId)
      // 跳转到监控页面
      router.push('/manager/monitor')
    }

    onMounted(() => {
      getInstances()
    })

    onUnmounted(() => {
      // 组件卸载时清除定时器和关闭WebSocket
      if (syncTimer) {
        clearInterval(syncTimer)
        syncTimer = null
      }
      handleCloseTerminal()
    })

    return {
      loading,
      tableData,
      isAutoSync,
      terminalVisible,
      terminalRef,
      handleSync,
      handleAutoSyncChange,
      getStatusType,
      handleConnect,
      handleCloseTerminal,
      connectingInstance,
      handleBeforeCloseTerminal,
      handleCloseCompleted,
      handleMonitor
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

.terminal-container {
  width: 100%;
  height: 500px;
  background-color: #1e1e1e;
  border-radius: 4px;
  overflow: hidden;

  .terminal {
    width: 100%;
    height: 100%;
    padding: 10px;
  }
}

:deep(.el-dialog__body) {
  padding: 10px;
}
</style>
