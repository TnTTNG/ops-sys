<template>
  <div class="loginInfo">
    <div style="width: 350px; background-color: white; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 40px 20px;">
      <el-form status-icon ref="formRef" :model="data.form" :rules="data.rules">
        <div style="margin-bottom: 40px; text-align: center; font-weight: bold; font-size: 24px"> 注 册</div>
        <el-form-item prop="username">
          <el-input  v-model="data.form.username" autocomplete="off" prefix-icon="User" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input  show-password v-model="data.form.password" autocomplete="off" prefix-icon="Lock" placeholder="请输入密码"/>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input  show-password v-model="data.form.confirmPassword" autocomplete="off" prefix-icon="Lock" placeholder="请再次确认密码"/>
        </el-form-item>
        <div style="margin-bottom: 10px">
          <el-button style="width: 100%" size="large" type="primary" @click="register">注 册</el-button>
        </div>
        <div style="text-align: right">
          已有账号？前往<a style="color: #317fd9" href="/login">登录</a>
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

const validatePass = (rule, value, callback) => {
  // value 表示需要确认的密码
  if (value !== data.form.password) {
    callback(new Error('两次输入的密码不匹配'))
  } else {
    callback()
  }
}

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
    confirmPassword: [
      { required: true, message: '请再次填写密码', trigger: 'blur'},
      { validator: validatePass, trigger: 'blur'}
    ]
  }
})

const register = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post('/register', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('注册成功')
          router.push('/login')
        } else {
          ElMessage.error(res.msg)
        }
      })
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