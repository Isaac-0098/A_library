<template>
  <div class="login-container">
    <div class="login-box">
      <h1>
        A_<span class="l">L</span><span class="i">i</span><span class="b">b</span><span class="r">r</span><span class="a">a</span><span class="r2">r</span><span class="y">y</span>
      </h1>
      <div class="library-icon">
        <i class="fas fa-book-open"></i>
      </div>
      
      <div class="login-options">
        <transition name="fade" mode="out-in">
          <div v-if="!showUserLoginOptions" key="initial" class="initial-buttons">
            <button class="login-btn guest" @click="handleGuestLogin">
              <span class="icon">👥</span>
              游客登录
            </button>
            
            <button class="login-btn user" @click="showUserLoginOptions = true">
              <span class="icon">🔑</span>
              用户登录
            </button>
          </div>
          
          <div v-else key="user-options" class="user-options-container">
            <button class="back-arrow" @click="showUserLoginOptions = false">
              <i class="fas fa-arrow-left"></i>
            </button>
            <div class="user-options">
              <div class="login-card customer-card" 
                   @click="showUserDialog = true"
                   @mousedown="preventFocus"
                   tabindex="-1">
                <span class="icon">👤</span>
                <h3>普通用户</h3>
              </div>
              <div class="login-card admin-card" 
                   @click="handleAdminLogin"
                   @mousedown="preventFocus"
                   tabindex="-1">
                <span class="icon">👨‍💼</span>
                <h3>管理员</h3>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="管理员登录"
      width="400px"
      center
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="adminForm" @keyup.enter="confirmAdminLogin">
        <el-form-item>
          <el-input
            v-model="adminForm.username"
            placeholder="管理员账号"
            prefix-icon="User"
            clearable
            autofocus
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="adminForm.password"
            type="password"
            placeholder="管理员密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAdminLogin">
            登录
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showUserDialog"
      title="用户登录"
      width="400px"
      center
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="userForm" @keyup.enter="handleCustomerLogin">
        <el-form-item>
          <el-input
            v-model="userForm.username"
            placeholder="用户账号"
            prefix-icon="User"
            clearable
            autofocus
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="用户密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUserDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCustomerLogin">
            登录
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import '@/assets/styles/login.css'
import { userApi } from '../api/user'

const router = useRouter()
const showUserLoginOptions = ref(false)
const dialogVisible = ref(false)
const showUserDialog = ref(false)

const userForm = ref({
  username: '',
  password: ''
})

const adminForm = ref({
  username: '',
  password: ''
})

const handleGuestLogin = () => {
  document.activeElement?.blur()
  localStorage.setItem('isLoggedIn', 'true')
  localStorage.setItem('username', '游客')
  localStorage.setItem('role', 'guest')
  router.push('/home')
}

const handleCustomerLogin = async () => {
  document.activeElement?.blur()
  if (!userForm.value.username || !userForm.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  try {
    const res = await userApi.login(userForm.value.username, userForm.value.password)
    if (res.code === 200) {
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', userForm.value.username)
      localStorage.setItem('role', 'user')
      ElMessage.success('登录成功')
      showUserDialog.value = false
      router.push('/home')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('账号或密码错误')
  }
}

const handleAdminLogin = () => {
  document.activeElement?.blur()
  dialogVisible.value = true
}

const confirmAdminLogin = async () => {
  document.activeElement?.blur()
  if (!adminForm.value.username || !adminForm.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  try {
    const res = await userApi.login(adminForm.value.username, adminForm.value.password)
    if (res.code === 200) {
      if (res.data?.role !== 1 && res.data?.role !== 2) {
        ElMessage.error('非管理员账号，无法登录')
        return
      }
      
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', adminForm.value.username)
      localStorage.setItem('role', res.data.role === 2 ? 'super_admin' : 'admin')
      
      if (res.data?.lastLogin) {
        const lastLogin = new Date(res.data.lastLogin).toLocaleString()
        ElMessage({
          message: `欢迎回来${res.data.role === 2 ? '，超级管理员' : ''}！您上次登录时间：${lastLogin}`,
          type: 'success',
          duration: 5000
        })
      } else {
        ElMessage.success(res.data.role === 2 ? '超级管理员登录成功' : '管理员登录成功')
      }
      
      dialogVisible.value = false
      router.push(res.data.role === 2 ? '/super-admin' : '/admin')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('账号或密码错误')
  }
}

const preventFocus = (e) => {
  e.preventDefault()
  document.activeElement?.blur()
}
</script>