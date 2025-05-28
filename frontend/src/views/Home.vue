<template>
  <div class="page_content_container" style="overflow: auto">
    <div class="user_info">
      <el-card style="width: 368px;height: 200px">
<!--        <div class="user_avatar" style="margin: auto">-->
        <h2 title style="margin-bottom: -5px">
          <span style="font-size: 50px; position: relative; top: -20px">Hi,</span>
          <img v-if="data.user?.avatar" style="width: 92px; height: 92px; border-radius: 50%; margin-left: 10px" :src="data.user?.avatar" />
          <img v-else style="width: 92px; height: 92px; border-radius: 50%" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="">
        </h2>
<!--        </div>-->
        <div class="role_info">
          <span style="font-size: 24px; color: #8c939d">欢迎使用OPS-SYSTEM~</span>
        </div>
      </el-card>
    </div>
  </div>
</template>


<script setup>
import {reactive} from "vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}")
})

</script>

<style setup>

</style>