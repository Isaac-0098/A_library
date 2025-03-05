import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('API请求错误:', error)
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

// 用户相关的API接口
export const userApi = {
  // 用户登录
  login(username, password) {
    const params = new URLSearchParams()
    params.append('username', username)
    params.append('password', password)
    
    return request({
      method: 'post',
      url: `/user/login?${params.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    }).then(response => {
      // 如果返回的是字符串，尝试解析为JSON
      if (typeof response === 'string') {
        try {
          return JSON.parse(response)
        } catch (e) {
          return { code: 200, message: response }
        }
      }
      return response
    })
  },

  // 用户注册
  register(params) {
    const searchParams = new URLSearchParams()
    for (const [key, value] of Object.entries(params)) {
      if (value !== null && value !== undefined) {
        searchParams.append(key, value)
      }
    }
    
    return request({
      method: 'post',
      url: `/user/register?${searchParams.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // 添加修改密码的方法
  updatePassword(oldPassword, newPassword) {
    const params = new URLSearchParams()
    params.append('username', localStorage.getItem('username'))
    params.append('oldPassword', oldPassword)
    params.append('newPassword', newPassword)
    
    return request({
      method: 'put',
      url: `/user/password?${params.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // 获取用户列表
  getUserList(role = 'all') {
    return request({
      method: 'get',
      url: role === 'all' ? '/user/list/all' : `/user/list/${role}`
    })
  },

  // 更新用户信息
  updateUser(user) {
    const params = new URLSearchParams()
    Object.keys(user).forEach(key => {
      if (user[key] !== null && user[key] !== undefined) {
        params.append(key, user[key])
      }
    })
    
    return request({
      method: 'put',
      url: `/user/update?${params.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // 删除用户
  deleteUser(userId) {
    return request({
      method: 'delete',
      url: `/user/delete/${userId}`
    })
  },

  // 更新用户状态
  updateUserStatus(userId, status) {
    const params = new URLSearchParams()
    params.append('userId', userId)
    params.append('status', status)
    
    return request({
      method: 'put',
      url: `/user/status?${params.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }
} 