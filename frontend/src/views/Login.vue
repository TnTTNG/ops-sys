<template>
  <div class="loginInfo">
    <div style="width: 350px; background-color: white; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 40px 20px;">
      <el-form ref="formRef" :model="data.form" :rules="data.rules">
        <div style="margin-bottom: 40px; text-align: center; font-weight: bold; font-size: 24px"> 欢迎登录云上运维</div>
        <el-form-item prop="username">
          <el-input  v-model="data.form.username" autocomplete="off" prefix-icon="User" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input  show-password v-model="data.form.password" autocomplete="off" prefix-icon="Lock" placeholder="请输入密码"/>
        </el-form-item>
        <div style="margin-bottom: 10px">
          <el-button style="width: 100%" size="large" type="primary" @click="login">登 录</el-button>
        </div>
        <div style="text-align: right">
          <a style="color: #3f3c3c" href="/register">用户注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const formRef = ref() // 表单检验
const data = reactive({
  form: {},
  rules: {
    username: [
      { required: true, message: '请填写用户名', trigger: 'blur'}
    ],
    password: [
      { required: true, message: '请填写密码', trigger: 'blur'},
      { min: 6, message: '最少六位', trigger: 'blur'},
    ],
  }
})

const login = () => {
  request.post('/login', data.form).then(res => {
    if (res.code === '200') {
      // 存储用户信息
      localStorage.setItem("code_user", JSON.stringify(res.data || {}))
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style>
.loginInfo {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}
</style>