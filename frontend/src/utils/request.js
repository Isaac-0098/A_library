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
    console.log('API Response:', response.data)
    return response.data
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export default request 