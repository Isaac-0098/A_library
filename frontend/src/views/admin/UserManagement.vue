<template>
  <div class="user-management-container">
    <div class="user-management">
      <div class="header">
        <h2>用户管理</h2>
        <el-button type="primary" @click="handleAdd">
          添加用户
        </el-button>
      </div>

      <!-- 用户列表 -->
      <el-table 
        :data="displayUsers" 
        v-loading="loading"
        class="user-table"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 1 ? 'danger' : 'primary'">
              {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination">
        <el-pagination
          v-model="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20]"
          :total="totalUsers"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 添加/编辑用户对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
        width="500px"
      >
        <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-width="80px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" />
          </el-form-item>
          <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
            <el-input v-model="userForm.password" type="password" show-password />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" />
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="userForm.phone" />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" style="width: 100%">
              <el-option label="普通用户" :value="0" />
              <el-option label="管理员" :value="1" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">
              确定
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'

const loading = ref(false)
const users = ref([])
const currentPage = ref(1)
const pageSize = ref(20)  // 默认每页20条

// 计算当前页显示的用户
const displayUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return users.value.slice(start, end)
})

// 计算总用户数
const totalUsers = computed(() => users.value.length)

// 处理页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 处理每页条数改变
const handleSizeChange = (val) => {
  pageSize.value = val
  // 如果当前页码超出范围，重置为第一页
  if (currentPage.value * val > users.value.length) {
    currentPage.value = 1
  }
}

const dialogVisible = ref(false)
const dialogType = ref('add')
const userFormRef = ref(null)
const searchQuery = ref('')

const userForm = reactive({
  id: '',
  username: '',
  password: '',
  email: '',
  phone: '',
  role: 0
})

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getUserList(0)
    if (res.code === 200) {
      users.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  userForm.id = ''
  userForm.username = ''
  userForm.password = ''
  userForm.email = ''
  userForm.phone = ''
  userForm.role = 0
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(userForm, row)
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await userApi.register(userForm)
          ElMessage.success('添加成功')
        } else {
          await userApi.updateUser(userForm)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchUsers()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}

// 修改用户状态
const handleStatusChange = async (row) => {
  try {
    await userApi.updateUserStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('状态修改成功')
    fetchUsers()
  } catch (error) {
    ElMessage.error('状态修改失败')
  }
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该用户吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await userApi.deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchUsers()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 添加搜索方法
const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management-container {
  display: flex;
  justify-content: center;
  min-height: 100%;
  padding: 20px;
}

.user-management {
  width: 100%;
  max-width: 1500px;
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.user-table {
  margin-top: 16px;
}

/* 优化表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

/* 分页器样式优化 */
:deep(.el-pagination) {
  padding: 0;
  font-weight: normal;
}

:deep(.el-pagination .el-select .el-input) {
  width: 100px;
}
</style> 