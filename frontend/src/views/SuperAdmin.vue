<template>
  <el-container class="admin-container">
    <!-- 顶部导航 -->
    <el-header class="admin-header">
      <div class="header-left">
        <h2>超级管理后台</h2>
        <div class="nav-buttons">
          <el-button 
            :type="route.path === '/super-admin/users' ? 'primary' : 'default'"
            @click="router.push('/super-admin/users')"
          >
            用户管理
          </el-button>
          <el-button 
            :type="route.path === '/super-admin/books' ? 'primary' : 'default'"
            @click="router.push('/super-admin/books')"
          >
            图书管理
          </el-button>
        </div>
      </div>
      <div class="header-right">
        <div class="clock">
          {{ currentTime }}
        </div>
        <span class="username no-select">
          <el-tag type="danger" effect="dark">超级管理员</el-tag>
          {{ username }}
        </span>
        <el-button 
          type="text" 
          @click="handleLogout"
          class="logout-btn"
        >
          退出登录
        </el-button>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || '')
const currentTime = ref('')
let timer = null

// 更新时间的函数
const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const date = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${year}-${month}-${date} ${hours}:${minutes}:${seconds}`
}

// 组件挂载时启动定时器
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      closeOnClickModal: false,
      showClose: false,
    }
  ).then(() => {
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
}

.admin-header {
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.header-left h2 {
  margin: 0;
  white-space: nowrap;
  color: #409eff;
  font-weight: bold;
}

.nav-buttons {
  display: flex;
  gap: 15px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.username:hover {
  background-color: #f5f7fa;
}

.logout-btn {
  color: #909399;
  transition: all 0.3s;
}

.logout-btn:hover {
  color: #f56c6c;
}

.no-select {
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  outline: none;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.clock {
  background: rgba(0, 0, 0, 0.06);
  padding: 6px 15px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 1.1em;
  color: #606266;
  margin-right: 15px;
  user-select: none;
  transition: all 0.3s;
  min-width: 200px;
  text-align: center;
}

.clock:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #409eff;
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style> 