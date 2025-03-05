<template>
  <div class="user-management-container">
    <div class="user-management">
      <div class="header">
        <h2>用户权限管理</h2>
        <el-button type="primary" @click="handleAdd">
          添加用户
        </el-button>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="输入用户名搜索..."
          style="width: 300px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          搜索
        </el-button>
      </div>

      <!-- 用户列表 -->
      <el-table 
        :data="displayUsers" 
        v-loading="loading"
        class="user-table"
      >
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="email" label="邮箱" width="250" />
        <el-table-column prop="phone" label="手机号" width="180" />
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最后登录时间" width="220">
          <template #default="{ row }">
            {{ row.lastLogin ? new Date(row.lastLogin).toLocaleString() : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button-group>
              <el-button 
                type="primary" 
                size="small" 
                @click="handleEdit(row)"
                :disabled="isSelf(row)"
                :title="isSelf(row) ? '不能修改自己的信息' : ''"
              >
                编辑
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleChangeRole(row)"
                :disabled="isSelf(row)"
                :title="isSelf(row) ? '不能修改自己的权限' : ''"
              >
                修改权限
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDelete(row)"
                :disabled="isSelf(row)"
                :title="isSelf(row) ? '不能删除自己的账号' : ''"
              >
                删除
              </el-button>
            </el-button-group>
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

      <!-- 编辑用户对话框 -->
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
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="userForm.password" 
              type="password"
              show-password
            />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="userForm.phone" />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" style="width: 100%">
              <el-option :value="0" label="普通用户" />
              <el-option :value="1" label="管理员" />
              <el-option :value="2" label="超级用户" />
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

      <!-- 修改权限对话框 -->
      <el-dialog
        v-model="roleDialogVisible"
        title="修改用户权限"
        width="400px"
      >
        <el-form v-if="selectedUser">
          <el-form-item label="当前角色">
            <el-tag :type="getRoleType(selectedUser.role)">
              {{ getRoleName(selectedUser.role) }}
            </el-tag>
          </el-form-item>
          <el-form-item label="新角色">
            <el-select v-model="newRole" style="width: 100%">
              <el-option :value="0" label="普通用户" />
              <el-option :value="1" label="管理员" />
              <el-option :value="2" label="超级用户" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="roleDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleRoleSubmit">
              确定
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'

const loading = ref(false)
const users = ref([])
const currentPage = ref(1)
const pageSize = ref(20)  // 默认每页20条
const searchQuery = ref('')
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const dialogType = ref('add')
const selectedUser = ref(null)
const newRole = ref(0)

const userForm = ref({
  username: '',
  password: '',
  email: '',
  phone: '',
  role: 0
})

const userFormRef = ref(null)

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

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

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getUserList('all')
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

// 获取角色类型
const getRoleType = (role) => {
  switch (role) {
    case 2: return 'danger'
    case 1: return 'warning'
    default: return 'info'
  }
}

// 获取角色名称
const getRoleName = (role) => {
  switch (role) {
    case 2: return '超级用户'
    case 1: return '管理员'
    default: return '普通用户'
  }
}

// 处理添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  userForm.value = {
    username: '',
    password: '',
    email: '',
    phone: '',
    role: 0
  }
  dialogVisible.value = true
}

// 检查是否是当前登录的超级用户
const isSelf = (user) => {
  return user.username === localStorage.getItem('username')
}

// 处理编辑用户
const handleEdit = (row) => {
  if (isSelf(row)) {
    ElMessage.warning('不能修改自己的信息')
    return
  }
  dialogType.value = 'edit'
  userForm.value = { ...row }
  dialogVisible.value = true
}

// 处理修改权限
const handleChangeRole = (row) => {
  if (isSelf(row)) {
    ElMessage.warning('不能修改自己的权限')
    return
  }
  selectedUser.value = row
  newRole.value = row.role
  roleDialogVisible.value = true
}

// 处理删除用户
const handleDelete = (row) => {
  if (isSelf(row)) {
    ElMessage.warning('不能删除自己的账号')
    return
  }
  ElMessageBox.confirm(
    '确定要删除该用户吗？此操作不可恢复',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await userApi.deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchUsers()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 处理状态改变
const handleStatusChange = async (row) => {
  if (isSelf(row)) {
    ElMessage.warning('不能修改自己的状态')
    row.status = row.status === 1 ? 0 : 1 // 恢复状态
    return
  }
  try {
    await userApi.updateUserStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复状态
    ElMessage.error('状态修改失败')
  }
}

// 处理搜索
const handleSearch = () => {
  if (!searchQuery.value) {
    fetchUsers()
    currentPage.value = 1  // 重置页码
    return
  }
  
  const query = searchQuery.value.toLowerCase()
  users.value = users.value.filter(user => 
    user.username.toLowerCase().includes(query) ||
    user.email?.toLowerCase().includes(query) ||
    user.phone?.includes(query)
  )
  currentPage.value = 1  // 搜索后重置页码
}

// 处理表单提交
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await userApi.register(userForm.value)
          ElMessage.success('添加成功')
        } else {
          await userApi.updateUser(userForm.value)
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

// 处理角色修改提交
const handleRoleSubmit = async () => {
  if (!selectedUser.value) return
  
  try {
    await userApi.updateUser({
      ...selectedUser.value,
      role: newRole.value
    })
    ElMessage.success('权限修改成功')
    roleDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error('权限修改失败')
  }
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
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.header h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
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

/* 禁用按钮的提示样式 */
:deep(.el-button.is-disabled) {
  cursor: not-allowed;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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