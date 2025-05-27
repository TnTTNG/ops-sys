<template>
  <div>
    <el-card style="width: 50%">
      <div class="person_info">
        <div class="general_avatar">
          <img v-if="data.user?.avatar" style="width: 92px; height: 92px; border-radius: 100%" :src="data.user?.avatar" />
          <img v-else style="width: 92px; height: 92px; border-radius: 100%" src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" alt="">
        </div>
        <div class="general_username">
          <span class="user_info">
            <span v-if="data.user?.username" style="font-size: x-large"> {{ data.user?.username }} </span>
<!--          <span v-if="data.user?.username" style="font-size: x-large"> USER </span>-->
            <div v-else class="username" style="margin-left: 10px; font-size: x-large"> USER </div>
            <span v-if="data.user?.nickname" style="font-size: x-large"> {{ data.user?.nickname }} </span>
            <div v-else class="nickname" style="margin-left: 10px; font-size: x-large"> NICKNAME </div>
          </span>
        </div>
      </div>
    </el-card>
    <el-card style="width: 50%; margin-top: 5px">
      <el-form :model="data.user" label-width="auto" style="max-width: 400px">
        <el-form-item label="用户名">
          <el-input v-model="data.user.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="data.user.email" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="data.user.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-input v-model="data.user.status" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.desc" type="textarea" />
        </el-form-item>
        <el-form-item prop="avatar" label="头像">
          <el-upload
              action="http://localhost:9999/files/upload"
              :on-success="handleFileSuccess"
              :headers="{ token: data.user.token }"
              :list-type="picture"
          >
            <el-button size="default" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
        <div style="text-align: center">
          <el-button type="primary" @click="update">保存</el-button>
          <el-button>取消</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import {reactive} from 'vue'
import {ElMessage} from "element-plus";
import request from "@/utils/request";
import {ref} from "vue";
// do not use same name with ref
const data = reactive( {
  user: JSON.parse(localStorage.getItem('code_user') || '{}'),
  form: {},
  tableData: [],
})

const form = reactive({
  name: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})

const imageUrl = ref()
const onSubmit = () => {
  console.log('submit!')
}

const load = () => {
  request.get('/user/selectAll', {
  }).then(res => {
      data.tableData = res.data.list
  })
}
load()
const handleFileSuccess = (res) => {
  data.user.avatar = res.data
};

const emit = defineEmits(['updateUser'])
const update = () => {
  let url
  if (data.user.role === 'ADMIN') {
    url = '/admin/update'
  } else {
    url = '/user/update'
  }
  request.put(url, data.user).then(res => {
    if (res.code === '200') {
      ElMessage.success('更新成功')
      localStorage.setItem('code_user', JSON.stringify(data.user))
      emit('updateUser')
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  }).catch(error => {
    console.error('更新失败:', error)
    ElMessage.error(error.response?.data?.msg || '更新失败')
  })
}
</script>

<style>

.person_info {
  display: flex;
  flex-direction: inherit;
  //justify-content: center;
}

.general_avatar {
  height: 96px;
  width: 96px;
  display: inline-block;
  flex: 0;
}
.general_username {
  margin-left: 10px;
  height: 96px;
  width: 200px;
  display: inline-block;
  flex: 0.5;
}

.user_info {
  height: 90px;
  display: inline-grid;
}

</style>
