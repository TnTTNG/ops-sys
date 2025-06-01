<template>
  <div class="ecs-monitor">
    <div class="monitor-header">
      <input v-model="selectedInstanceId" placeholder="请输入实例 ID" />
      <el-select v-model="timeInterval" placeholder="请选择时间间隔" style="width: 100px">
        <el-option label="1小时" value="1h" />
        <el-option label="12小时" value="12h" />
        <el-option label="24小时" value="24h" />
      </el-select>
      <el-button type="primary" @click="fetchMonitorData">查询</el-button>
      <el-button type="danger" @click="clearData">清除数据</el-button>
    </div>

    <div class="monitor-charts">
      <div ref="cpuChart" class="chart"></div>
      <div ref="networkChart" class="chart"></div>
      <div ref="diskChart" class="chart"></div>
      <div ref="trafficChart" class="chart"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import request from "@/utils/request.js";
// import axios from 'axios'

const instanceList = ref([])
const selectedInstanceId = ref('')
const cpuChart = ref(null)
const networkChart = ref(null)
const diskChart = ref(null)
const trafficChart = ref(null)
let cpuChartInstance = null
let networkChartInstance = null
let diskChartInstance = null
let trafficChartInstance = null
const timeInterval = ref('1h')
const tableData = ref([])

// 初始化图表
const initCharts = () => {
  cpuChartInstance = echarts.init(cpuChart.value)
  networkChartInstance = echarts.init(networkChart.value)
  diskChartInstance = echarts.init(diskChart.value)
  trafficChartInstance = echarts.init(trafficChart.value)

  // 设置图表基础配置
  const baseOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'time',
      boundaryGap: false
    },
    yAxis: {
      type: 'value'
    }
  }

  // CPU 使用率图表配置
  cpuChartInstance.setOption({
    ...baseOption,
    title: {
      text: 'CPU 使用率'
    },
    series: [{
      name: 'CPU 使用率',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      }
    }]
  })

  // 网络带宽图表配置
  networkChartInstance.setOption({
    ...baseOption,
    title: {
      text: '网络带宽'
    },
    series: [
      {
        name: '公网流入带宽',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '公网流出带宽',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  })

  // 磁盘 IO 图表配置
  diskChartInstance.setOption({
    ...baseOption,
    title: {
      text: '磁盘 IO'
    },
    series: [
      {
        name: '磁盘读取 BPS',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#F56C6C'
        }
      },
      {
        name: '磁盘写入 BPS',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#909399'
        }
      },
      {
        name: '磁盘读取 IOPS',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#9B59B6'
        }
      },
      {
        name: '磁盘写入 IOPS',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#34495E'
        }
      }
    ]
  })

  // 网络流量图表配置
  trafficChartInstance.setOption({
    ...baseOption,
    title: {
      text: '网络流量'
    },
    series: [
      {
        name: '公网入流量',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '公网出流量',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#E6A23C'
        }
      },
      {
        name: '内网入流量',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '内网出流量',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: {
          color: '#909399'
        }
      }
    ]
  })

  // After initializing charts, fetch the monitor data
  fetchMonitorData()
}

// 获取实例列表
const fetchInstanceList = async () => {
  try {
    const response = await request.get('/ecs/selectAll')
    instanceList.value = response.data || []
  } catch (error) {
    console.error('获取实例列表失败:', error)
    ElMessage.error('获取实例列表失败')
  }
}

// 处理实例选择变化
const handleInstanceChange = (instanceId) => {
  if (instanceId) {
    fetchMonitorData()
  }
}

const getEcsInstancesId = async () => {
  try {
    loading.value = true
    const res = await request.get('/ecs/selectAll', {
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

// 获取监控数据
const fetchMonitorData = async () => {
  if (!selectedInstanceId.value) {
    ElMessage.warning('请输入实例ID')
    return
  }

  // 确保图表实例已初始化
  if (!cpuChartInstance || !networkChartInstance || !diskChartInstance || !trafficChartInstance) {
    initCharts()
  }

  try {
    let period = '60'
    const endTime = new Date()
    let startTime = new Date() // 默认查询最近一小时的数据

    switch (timeInterval.value) {
      case '1h':
        startTime = new Date(endTime.getTime() - 3600000)
        period = '60'
        break
      case '12h':
        startTime = new Date(endTime.getTime() - 12 * 3600000)
        period = '600'
        break
      case '24h':
        startTime = new Date(endTime.getTime() - 24 * 3600000)
        period = '600'
        break
      default:
        period = '60'
    }
    // 格式化时间为 ISO 8601 格式，并移除毫秒
    const formatTime = (date) => {
      return date.toISOString().replace(/\.\d{3}Z$/, 'Z')
    }

    // 先获取记录总数
    const countResponse = await request.get('/monitor/count')
    if (countResponse.code === '200' && countResponse.data >= 1000) {
      // 如果记录数超过1000，调用清理接口
    await request.delete('/monitor/clean', {
      params: {
          beforeTime: formatTime(new Date(endTime.getTime() - 24 * 3600000)) // 清理24小时前的数据
      }
    })
      ElMessage.success('已自动清理历史监控数据')
    }

    const response = await request.get('/monitor/data', {
      params: {
        instanceId: selectedInstanceId.value,
        startTime: formatTime(startTime),
        endTime: formatTime(endTime),
        period: period
      }
    })

    console.log('监控数据响应:', response)

    // 检查响应状态码
    if (response.code !== '200') {
      console.warn('请求未成功:', response)
      ElMessage.warning(response.msg || '获取监控数据失败')
      return
    }

    // 检查响应数据结构
    const responseData = response.data
    console.log('响应数据内容:', responseData)

    let monitorData = null
    if (Array.isArray(responseData)) {
      monitorData = responseData
    } else if (responseData && Array.isArray(responseData.data)) {
      monitorData = responseData.data
    } else if (responseData && Array.isArray(responseData.records)) {
      monitorData = responseData.records
    } else if (responseData && typeof responseData === 'object') {
      // 如果数据是对象，尝试获取其中的数组数据
      const possibleArrays = Object.values(responseData).filter(val => Array.isArray(val))
      if (possibleArrays.length > 0) {
        monitorData = possibleArrays[0]
      }
    }

    if (monitorData) {
      console.log('找到监控数据:', monitorData)
      updateCharts(monitorData)
    } else {
      console.warn('未找到有效的监控数据，响应数据:', responseData)
      ElMessage.warning('未获取到监控数据')
    }
  } catch (error) {
    console.error('获取监控数据失败:', error)
    ElMessage.error(error.response?.data?.message || '获取监控数据失败')
  }
}
const clearData = () => {
  const endTime = new Date()
  const formatTime = (date) => {
    return date.toISOString().replace(/\.\d{3}Z$/, 'Z')
  }
  request.delete('/monitor/clean', {
    params: {
      beforeTime: formatTime(endTime)
    }
  })
}
// 更新图表数据
const updateCharts = (data) => {
  if (!Array.isArray(data) || data.length === 0) {
    console.warn('没有可用的监控数据:', data)
    ElMessage.warning('没有可用的监控数据')
    return
  }

  // 检查图表实例是否都已初始化
  if (!cpuChartInstance || !networkChartInstance || !diskChartInstance || !trafficChartInstance) {
    console.warn('图表实例未初始化，正在重新初始化...')
    initCharts()
    // 如果初始化后仍然失败，则返回
    if (!cpuChartInstance || !networkChartInstance || !diskChartInstance || !trafficChartInstance) {
      ElMessage.error('图表初始化失败，请刷新页面重试')
      return
    }
  }

  try {
    console.log('处理监控数据:', data)

    // 确保数据中的时间戳是有效的
    const validData = data.filter(item => item.monitorTime)
    if (validData.length === 0) {
      console.warn('没有有效的时间戳数据')
      ElMessage.warning('数据格式不正确')
      return
    }

    // 添加CPU数据调试日志
    console.log('CPU使用率数据:', validData.map(item => ({
      time: item.monitorTime,
      cpuUsage: item.cpuUsage,
      rawData: item
    })))

    const cpuData = validData.map(item => [new Date(item.monitorTime), item.cpuUsage])
    const networkData = {
      rx: validData.map(item => [new Date(item.monitorTime), item.internetRx]),
      tx: validData.map(item => [new Date(item.monitorTime), item.internetTx])
    }
    const diskData = {
      readBps: validData.map(item => [new Date(item.monitorTime), item.bpsRead]),
      writeBps: validData.map(item => [new Date(item.monitorTime), item.bpsWrite]),
      readIops: validData.map(item => [new Date(item.monitorTime), item.iopsRead]),
      writeIops: validData.map(item => [new Date(item.monitorTime), item.iopsWrite])
    }
    const trafficData = {
      internetRx: validData.map(item => [new Date(item.monitorTime), item.internetRx]),
      internetTx: validData.map(item => [new Date(item.monitorTime), item.internetTx]),
      intranetRx: validData.map(item => [new Date(item.monitorTime), item.intranetRx]),
      intranetTx: validData.map(item => [new Date(item.monitorTime), item.intranetTx])
    }

    console.log('处理后的图表数据:', { cpuData, networkData, diskData, trafficData })

    cpuChartInstance.setOption({
      series: [{
        data: cpuData
      }]
    })

    networkChartInstance.setOption({
      series: [
        { data: networkData.rx },
        { data: networkData.tx }
      ]
    })

    diskChartInstance.setOption({
      series: [
        { data: diskData.readBps },
        { data: diskData.writeBps },
        { data: diskData.readIops },
        { data: diskData.writeIops }
      ]
    })

    trafficChartInstance.setOption({
      series: [
        { data: trafficData.internetRx },
        { data: trafficData.internetTx },
        { data: trafficData.intranetRx },
        { data: trafficData.intranetTx }
      ]
    })

    // 强制图表重新渲染
    cpuChartInstance.resize()
    networkChartInstance.resize()
    diskChartInstance.resize()
    trafficChartInstance.resize()
  } catch (error) {
    console.error('更新图表数据失败:', error)
    ElMessage.error('更新图表数据失败')
  }
}

// 监听窗口大小变化
const handleResize = () => {
  cpuChartInstance?.resize()
  networkChartInstance?.resize()
  diskChartInstance?.resize()
  trafficChartInstance?.resize()
}

// 在组件挂载时初始化
onMounted(() => {
  // 从localStorage获取实例ID
  const storedInstanceId = localStorage.getItem('monitorInstanceId')
  if (storedInstanceId) {
    selectedInstanceId.value = storedInstanceId
  }
    // 初始化图表
    initCharts()
    // 清除存储的实例ID
    localStorage.removeItem('monitorInstanceId')
  window.addEventListener('resize', handleResize)
  fetchInstanceList() // 获取实例列表
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  cpuChartInstance?.dispose()
  networkChartInstance?.dispose()
  diskChartInstance?.dispose()
  trafficChartInstance?.dispose()
})
</script>

<style scoped>
.ecs-monitor {
  padding: 20px;
  max-width: 1000px;
  margin: 0;
  box-sizing: border-box;
  margin-bottom: 50px;
}

.monitor-header {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.monitor-charts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  width: 100%;
  box-sizing: border-box;
}

.chart {
  width: 780px;
  height: 350px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  overflow: hidden;
}
</style>