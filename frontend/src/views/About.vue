<template>
  <div>
    关于的页面
  </div>
</template>

<script setup>
import {reactive} from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js"

const data = reactive({
  name: null,
  pageNum: 1,
  pageSize: 5,
  total: 6,
  tableData: []
})
const load = () => {
  request.get('/admin/selectPage').then(res => {
    console.log(res.data)
  })
}

load()
</script>