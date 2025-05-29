<template>
  <div>
    <!-- 头部区域开始 -->
    <div style="height: 80px; display: flex;">
      <div style="width: 250px; display: flex; align-items: center; padding-left: 13px; background-color: #3a456b">
        <CloudLogo
            :size="45"
            :colors="{
          primaryStart: '#d4e2ec',
          primaryEnd: '#098ad9',
          module: '#34495E',
          dataFlow: '#27AE60'
        }"/>
        <span style="font-size: 20px; font-weight: bold; color: #f1f1f1; margin-left: 3px">云上运维管理平台</span>
      </div>
      <div style="flex: 1; display: flex; align-items: center; padding-left: 20px; border-bottom: 1px solid #ddd">
        <span style="font-size: 18px;margin-right: 5px; cursor: pointer" @click="router.push('/manager')">主页</span> / <span style="font-size:18px; margin-left: 5px">{{ router.currentRoute.value.meta.name}}</span>
      </div>
      <div style="width: fit-content; padding-right: 20px; display: flex; align-items: center; border-bottom: 1px solid #ddd">
        <el-dropdown>
          <div style="display: flex; align-items: center">
<!--            <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%" :src="data.user?.avatar" />-->
            <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%" :src="data.user?.avatar" />
            <img v-else style="width: 40px; height: 40px; border-radius: 50%" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="">
            <span style="margin-left: 5px">{{ data.user?.nickname }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!--  头部区域结束 -->

    <!--  下方区域开始 -->
    <div style="display: flex">
      <!--  菜单区域开始 -->
      <div style="width: 250px;">
        <el-menu router :default-active="router.currentRoute.value.path" style="min-height: calc(100vh - 60px)">
          <el-menu-item index="/manager/home">
            <svg-icon name="monitoring" :size="16" />
            <span>工作台</span>
          </el-menu-item>
          <el-sub-menu v-if="data.user.role === 'ADMIN'" index="1">
            <template #title>
              <svg-icon name="用户管理" :size="19" />
              <span>用户管理</span>
            </template>
            <el-menu-item index="/manager/admin">
              <el-icon><UserFilled /></el-icon>
              <span>超级管理员</span>
            </el-menu-item>
            <el-menu-item index="/manager/user">
              <el-icon><UserFilled /></el-icon>
              <span>管理员</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu>
            <template #title>
              <svg-icon name="ecs" :size="20" />
              <span>资源管理</span>
            </template>
            <el-menu-item index="/manager/rds">
<!--              <el-icon><UserFilled /></el-icon>-->
              <span>数据库管理</span>
            </el-menu-item>
            <el-menu-item index="/manager/ecs">
<!--              <el-icon><UserFilled /></el-icon>-->
              <span>云服务器管理</span>
            </el-menu-item>
<!--            <el-menu-item index="/manager/user">-->
<!--              <el-icon><UserFilled /></el-icon>-->
<!--              <span>管理员</span>-->
<!--            </el-menu-item>-->
          </el-sub-menu>
          <el-menu-item>
            <svg-icon name="告警信息" :size="18" />
            <span>监控告警</span>
          </el-menu-item>
          <el-menu-item>
            <svg-icon name="日志管理" :size="20" />
            <span>日志管理</span>
          </el-menu-item>
          <el-menu-item>
            <svg-icon name="任务调度" :size="19" />
            <span>任务调度</span>
          </el-menu-item>
        </el-menu>
      </div>
      <!--  菜单区域结束 -->

      <!--  数据渲染区域开始 -->
      <div style="flex: 1; width: 0; padding: 10px; background-color: #f2f4ff">
        <RouterView @updateUser="updateUser"/>
      </div>
      <!--  数据渲染区域结束 -->

    </div>
    <!--  下方区域结束 -->

  </div>
</template>

<script setup>

import router from "@/router/index.js";
import CloudLogo from "@/assets/CloudLogo.vue";
import { reactive } from "vue";
import SvgIcon from "@/components/SvgIcon/Index.vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}")
})


const logout = () => {
  localStorage.removeItem('code_user')
  location.href = '/login'
}
const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('code_user') || "{}")
}

// export default {
//   name: "Manager",
//   components: {
//     CustomIcon
//   }
// }
</script>

<style>
.el-menu {
  background-color: #3a456b;
  border: none;
}
.el-sub-menu__title {
  background-color: #3a456b;
  color: #ddd;
}
.el-menu-item {
  height: 50px;
  color: #ddd;
}
.el-menu .is-active {
  background-color: #537bee;
  color: #fff;
}
.el-sub-menu__title:hover {
  background-color: #3a456b;
}
.el-menu-item:not(.is-active):hover {
  background-color: #7a9fff;
  color: #333;
}
.el-dropdown {
  cursor: pointer;
}
.el-tooltip__trigger {
  outline: none;
}
.el-menu--inline .el-menu-item {
  padding-left: 48px !important;
}
</style>