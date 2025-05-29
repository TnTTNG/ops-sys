<template>
  <div>
<!--    <div class="card" style="margin-bottom: 5px; margin-left: 3px">-->
    <el-card style="margin-bottom: 5px;" :body-style="{ padding: '10px'}">
      <el-input clearable @clear="load" style="width: 260px; margin-left: 10px" v-model="data.username" placeholder="请输入用户名查询" :prefix-icon="Search"></el-input>
<!--      <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.status" placeholder="请输入状态查询" :prefix-icon="Search"></el-input>-->
      <el-select v-model="data.status" clearable @clear="load" placeholder="状态" style="width: 90px; margin-left: 5px">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            :disabled="item.disabled"
        />
      </el-select>
      <el-button style="margin-left: 10px" @click="load">查 询</el-button>
      <el-button style="margin-left: 5px" @click="reset">重 置</el-button>
<!--    </div>-->
    </el-card>

    <el-card style="margin-bottom: 5px;" :body-style="{ padding: '10px'}">
    <div class="card" style="margin-bottom: 5px; margin-left: 10px">
      <el-button class="button" type="primary" @click="handleAdd">新 增</el-button>
      <el-button class="button" type="danger" @click="deleteBatch">批量删除</el-button>
    </div>
    </el-card>

    <el-card :body-style="{ padding: '10px'}">
      <div class="card" style="margin-bottom: 5px; margin-left: 3px">
        <el-table :data="data.tableData" border style="width: 100%" @selection-change="handleSelectionChange"
                  :header-cell-style="{ color: '#333', backgroundColor: '#eaf4ff' }">
          <el-table-column type="selection" width="60" />
          <el-table-column prop="id" label="编号" width="60"/>
          <el-table-column label="头像" width="100">
            <template #default="scope">
              <el-image v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" :preview-teleported="true"
                        style="width: 40px; height: 40px; border-radius: 50%; display: block "/>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名" width="180"/>
          <el-table-column prop="nickname" label="权限" width="180"/>
          <el-table-column prop="phone" label="电话"/>
          <el-table-column prop="email" label="邮箱"/>
          <el-table-column prop="createTime" label="创建时间"/>
          <el-table-column prop="status" label="状态" width="100"/>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" icon="Edit" circle @click="handleEdit(scope.row)"></el-button>
              <el-button type="danger" icon="Delete" circle @click="del(scope.row.id)"></el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="card">
        <el-pagination
            v-model:current-page="data.pageNum"
            v-model:page-size="data.pageSize"
            layout="total, prev, pager, next"
            :page-sizes="[20]"
            :total="data.total"
            @current-change="load"
            @size-change="load"
            style="margin-top: 10px; margin-left: 5px"
            class="mt-4"
        />
      </div>
    </el-card>

    <el-dialog title="超级管理员信息" v-model="data.FormVisible" width="500" destroy-on-close>
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">
        <div v-if="data.errorMessage" class="error-message">{{ data.errorMessage }}</div>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="data.form.username" autocomplete="off" />
        </el-form-item>
<!--        <el-form-item label="权限" prop="nickname">-->
<!--          <el-input v-model="data.form.nickname" autocomplete="off" />-->
<!--        </el-form-item>-->
        <el-form-item label="电话" prop="phone">
          <el-input v-model="data.form.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="data.form.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="权限" prop="nickname">
          <el-select v-model="data.form.nickname" placeholder="权限" style="width: 120px">
            <el-option
                v-for="item in options1"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                :disabled="item.disabled"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="data.form.status" placeholder="状态" style="width: 90px">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                :disabled="item.disabled"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
              action="http://localhost:9999/files/upload"
              :headers="{ token: data.user.token }"
              :on-success="handleFileSuccess"
              :list-type="picture"
          >
            <el-button >上传头像</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDialog">取 消</el-button>
          <el-button type="primary" @click="save">保 存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import { ref } from 'vue';

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || '{}'),
  username: null,
  status: null,
  pageNum: 1,
  pageSize: 10,
  total: 0,
  tableData: [],
  FormVisible: false,
  form: {},
  rules: {
    username: [
      { required: true, message: "请填写账号", trigger: 'blur'}
    ],
    nickname: [
      { required: true, message: "请填写权限", trigger: 'blur'}
    ],
    phone: [
      { required: true, message: "请填写电话", trigger: 'blur'}
    ],
    status: [
      { required: true, message: "请选择状态", trigger: 'blur'}
    ]
  },
  errorMessage: '',
  rows: []
})

const formRef = ref()

const load = () => {
  request.get('/admin/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      username: data.username,
      status: data.status
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data.list
      data.total = res.data.total
    } else {
      ElMessage.error(res.msg)
    }
  })
}
load()

const reset = () => {
  data.username = null
  data.status = null
  load()
}

const value = ref('')
const options = [
  {
    value: '启用',
    label: '启用',
  },
  {
    value: '禁用',
    label: '禁用',
    // disabled: true,
  },
]
const options1 = [
  {
    value: '超级管理员',
    label: '超级管理员',
    // disabled: true,
  },
]
const handleAdd = () => {
  data.FormVisible = true
  data.form = {}
  data.errorMessage = ''
}

const closeDialog = () => {
  data.FormVisible = false
  data.errorMessage = ''
}

const add = () => {
  formRef.value.validate((valid) => {
    if (valid) { // 验证通过
      request.post('/admin/add', data.form).then(res => {
        if (res.code === '200') {
          closeDialog()
          ElMessage.success('新增成功')

          // 更新localStorage中的用户信息
          const user = JSON.parse(localStorage.getItem('code_user') || '{}');
          if (user.id === data.form.id) {  // 如果是当前用户
            // 更新所有修改的字段
            Object.keys(data.form).forEach(key => {
              if (data.form[key] !== undefined) {
                user[key] = data.form[key];
              }
            });
            localStorage.setItem('code_user', JSON.stringify(user));
          }

          load()
        } else {
          // 根据错误码显示不同提示
          if (res.code === '400') {
            // 检查error信息可能存在于data或msg字段
            const errorMsg = res.msg || res.data || '添加失败';
            data.errorMessage = errorMsg
          } else {
            ElMessage.error(res.msg || res.data || '系统错误')
          }
        }
      })
    }
  })
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row)) // 深度拷贝数据
  data.FormVisible = true
}

const update = () => {
  formRef.value.validate((valid) => {
    if (valid) { // 验证通过
      request.put('/admin/update', data.form).then(res => {
        if (res.code === '200') {
          closeDialog()
          ElMessage.success('修改成功')

          // 更新localStorage中的用户信息
          const user = JSON.parse(localStorage.getItem('code_user') || '{}');
          if (user.id === data.form.id) {  // 如果是当前用户
            // 更新所有修改的字段
            Object.keys(data.form).forEach(key => {
              if (data.form[key] !== undefined) {
                user[key] = data.form[key];
              }
            });
            localStorage.setItem('code_user', JSON.stringify(user));
          }

          load()
        } else {
          // 根据错误码显示不同提示
          if (res.code === '400') {
            // 检查error信息可能存在于data或msg字段
            const errorMsg = res.msg || res.data || '修改失败';
            data.errorMessage = errorMsg
          } else {
            ElMessage.error(res.msg || res.data || '系统错误')
          }
        }
      })
    }
  })
}

const save = () => {
  data.form.id ? update() : add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后无法恢复数据，请确认是否删除该用户', '删除确认', { type: 'warning'}).then(res => {
    request.delete('/admin/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        // 根据错误码显示不同提示
        if (res.code === '400') {
          // 检查error信息可能存在于data或msg字段
          const errorMsg = res.msg || res.data || '删除失败';
          data.errorMessage = errorMsg
        } else {
          ElMessage.error(res.msg || res.data || '系统错误')
        }
      }
    })
  }).catch(err => {})
}

const handleSelectionChange = (rows) => {
  data.rows = rows
}

const deleteBatch = () => {
  if (data.rows.length === 0) {
    ElMessage.warning('请选择需要删除的用户')
    return
  }
  ElMessageBox.confirm('删除后无法恢复数据，请确认是否删除该用户', '删除确认', { type: 'warning'}).then(res => {
    request.delete('/admin/deleteBatch', { data: data.rows}).then(res => {
      if (res.code === '200') {

        ElMessage.success('删除成功')
        load()
      } else {
        // 根据错误码显示不同提示
        if (res.code === '400') {
          // 检查error信息可能存在于data或msg字段
          const errorMsg = res.msg || res.data || '删除失败';
          data.errorMessage = errorMsg
        } else {
          ElMessage.error(res.msg || res.data || '系统错误')
        }
      }
    })
  }).catch(err => {})
}

const handleFileSuccess = (res) => {
  data.form.avatar = res.data
}
</script>

<style scoped>

.button {
  margin-bottom: auto;
  margin-top: auto;
}

.error-message {
  color: #f56c6c;
  font-size: 14px;
  margin-bottom: 15px;
  padding: 8px 12px;
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
}

.pagination-container {
  position: relative;
  bottom: 5;
  left: 10;
  right: 10;
  z-index: 999;
  background-color: #fff;
  padding: 10px 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #e6e6e6;
  max-width: 100%;
  height: 50px;
  align-items: center;
  /* margin: 5 5 5 auto; */
}

/* 移除之前添加的底部内边距，因为不再需要 */
:deep(.el-main) {
  padding-bottom: 0;
}

/* 移除表格的底部外边距 */
.card:last-of-type {
  margin-bottom: 0;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .pagination-container {
    justify-content: center;
    padding: 10px;
  }
  
  :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}

/* 调整分页组件的样式 */
:deep(.el-pagination) {
  height: 30px;
  line-height: 30px;
}

:deep(.el-pagination .el-pagination__total),
:deep(.el-pagination .el-pagination__sizes),
:deep(.el-pagination .el-pager li),
:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  height: 30px;
  line-height: 30px;
}
</style>
