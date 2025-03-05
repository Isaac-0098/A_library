import request from '../utils/request'
import { ElMessage } from 'element-plus'

export const bookApi = {
  // 获取图书列表
  getBookList() {
    return request({
      method: 'get',
      url: '/book/list'
    }).then(response => {
      if (response.code === 200) {
        return response.data || []
      } else {
        ElMessage.error(response.msg || '获取图书列表失败')
        return []
      }
    })
  },

  // 搜索图书
  searchBooks(title) {
    return request({
      method: 'get',
      url: '/book/search',
      params: { title }
    }).then(response => {
      return response || []
    }).catch(error => {
      ElMessage.error('搜索图书失败')
      console.error('搜索失败:', error)
      return []
    })
  },

  // 添加图书
  addBook(book) {
    const params = new URLSearchParams()
    params.append('id', book.id || 0)
    params.append('code', book.code)
    params.append('title', book.title)
    params.append('author', book.author)
    params.append('cover', book.cover)
    params.append('category', book.category)
    params.append('description', book.description)
    
    return request({
      method: 'post',
      url: `/book/add?${params.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // 更新图书
  updateBook(book) {
    const params = new URLSearchParams()
    params.append('id', book.id)
    params.append('code', book.code)
    params.append('title', book.title)
    params.append('author', book.author)
    params.append('cover', book.cover)
    params.append('category', book.category)
    params.append('description', book.description)
    
    return request({
      method: 'put',
      url: `/book/update?${params.toString()}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // 删除图书
  deleteBook(id) {
    return request({
      method: 'delete',
      url: `/book/delete/${id}`
    })
  },

  // 根据ID获取图书
  getBookById(id) {
    return request({
      method: 'get',
      url: `/book/get/${id}`
    })
  },

  // 获取所有图书（用于前台展示）
  getAllBooks() {
    return request({
      method: 'get',
      url: '/book/list'
    }).then(response => {
      console.log('API response:', response) // 添加日志
      if (response.code === 200 && response.data) {
        return response.data
      }
      throw new Error(response.msg || '获取图书列表失败')
    })
  },

  // 获取所有分类
  getCategories() {
    return request({
      method: 'get',
      url: '/book/categories'
    }).then(response => {
      if (response.code === 200) {
        return response.data // 返回分类数组
      } else {
        return [] // 如果失败返回空数组
      }
    })
  },

  // 按作者搜索
  searchByAuthor(author) {
    return request({
      method: 'get',
      url: '/book/search/author',
      params: { author }
    }).then(response => {
      if (response.code === 200) {
        return response.data || []
      } else {
        ElMessage.error(response.msg || '搜索失败')
        return []
      }
    })
  },

  // 全局搜索
  searchAll(keyword) {
    return request({
      method: 'get',
      url: '/book/search/all',
      params: { keyword }
    }).then(response => {
      if (response.code === 200) {
        return response.data || []
      } else {
        ElMessage.error(response.msg || '搜索失败')
        return []
      }
    })
  }
} 