<template>
  <div>
    <el-card style="width: 50%; margin: 20px auto">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入原密码"/>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updatePassword">确认修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const formRef = ref()
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (form.confirmPassword !== '') {
      formRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

const updatePassword = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const user = JSON.parse(localStorage.getItem('code_user') || '{}')
      // let url
      // if (user.role === 'ADMIN') {
      //   url = '/admin/updatePassword'
      // } else {
      //   url = '/user/updatePassword'
      // }
      request.put('/user/updatePassword', {
        id: user.id,
        oldPassword: form.oldPassword,
        password: form.newPassword
      }).then(res => {
        if (res.code === '200') {
          ElMessage.success('密码修改成功，请重新登录')
          localStorage.removeItem('code_user')
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
        } else {
          ElMessage.error(res.msg || '密码修改失败')
        }
      }).catch(error => {
        console.error('密码修改失败:', error)
        ElMessage.error(error.response?.data?.msg || '密码修改失败')
      })
    }
  })
}

const resetForm = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>