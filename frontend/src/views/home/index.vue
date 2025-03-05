<template>
  <div class="home">
    <!-- 图书总数标签 -->
    <div class="book-count-tag">
      <el-tag type="info" size="large" effect="dark">
        <span>馆藏图书：{{ totalBooks }} 本</span>
      </el-tag>
    </div>

    <!-- 登录按钮 -->
    <div class="header-section">
      <el-button type="primary" @click="goToLogin" v-if="!isLoggedIn">
        登录
      </el-button>
      <template v-else>
        <div class="user-info">
          <el-tag size="small" effect="light">{{ username }}</el-tag>
          <el-divider direction="vertical" />
          <el-button type="text" size="small" @click="handleLogout">退出</el-button>
        </div>
      </template>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-container">
        <div class="brand">
          <h1 class="search-title">A_Library</h1>
          <div class="subtitle-wrapper">
            <p class="search-subtitle">
              <span class="gradient-text">发现</span>
              <span class="separator">·</span>
              <span class="gradient-text-2">探索</span>
              <span class="separator">·</span>
              <span class="gradient-text-3">阅读</span>
            </p>
          </div>
        </div>

        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="搜索图书..."
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleClear"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <div class="category-segment">
          <el-segmented
            v-model="selectedCategory"
            :options="categories"
            @change="handleCategoryClick"
          />
        </div>
      </div>
    </div>

    <!-- 轮播图组件 -->
    <Carousel />

    <!-- 图书展示区域 -->
    <div class="books-section">
      <div class="section-title">
        <h2>{{ selectedCategory === '全部' ? '推荐图书' : selectedCategory }}</h2>
      </div>
      <el-row :gutter="30">
        <el-col 
          v-for="book in displayBooks" 
          :key="book.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6" 
          :xl="4"
        >
          <el-card class="book-card" @click="showBookDetail(book)">
            <div class="book-cover">
              <el-image 
                :src="getImageUrl(book.cover)" 
                fit="cover"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">作者：{{ book.author }}</p>
              <p class="book-category">分类：{{ book.category }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="pagination">
        <el-pagination
          v-model="currentPage"
          :page-size="12"
          :total="books.length"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="showDetail"
      :title="selectedBook?.title"
      width="60%"
    >
      <div class="book-detail" v-if="selectedBook">
        <div class="book-cover">
          <el-image 
            :src="getImageUrl(selectedBook.cover)" 
            fit="cover"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="book-meta">
          <p class="book-code">编号：{{ selectedBook.code }}</p>
          <h3>{{ selectedBook.title }}</h3>
          <p class="author">作者：{{ selectedBook.author }}</p>
          <p class="category">分类：{{ selectedBook.category }}</p>
          <div class="download-url">
            <span>网盘链接：</span>
            <el-input
              v-model="selectedBook.downloadUrl"
              readonly
              class="url-input"
            >
              <template #append>
                <el-button @click="copyUrl">
                  <el-icon><DocumentCopy /></el-icon>
                  复制
                </el-button>
              </template>
            </el-input>
          </div>
          <div class="description">
            <h4>简介：</h4>
            <p>{{ selectedBook.description }}</p>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="quote-container">
          <div class="quote">
            <p>"读书破万卷，下笔如有神"</p>
            <small>— 杜甫</small>
          </div>
          <div class="quote">
            <p>"书籍是人类进步的阶梯"</p>
            <small>— 高尔基</small>
          </div>
          <div class="quote">
            <p>"读一本好书，就是和许多高尚的人谈话"</p>
            <small>— 歌德</small>
          </div>
        </div>
        <div class="footer-text">
          <p>A_Library - 让阅读成为一种习惯</p>
          <small>知识的海洋，等你探索</small>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { Search, Picture, DocumentCopy } from '@element-plus/icons-vue'
import { bookApi } from '../../api/book'
import Carousel from './Carousel.vue'

import '@/assets/styles/index.css'
const router = useRouter()
const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true')
const username = ref(localStorage.getItem('username') || '')
const searchQuery = ref('')
const books = ref([])
const categories = ref(['全部'])
const selectedCategory = ref('全部')
const currentPage = ref(1)
const pageSize = 12
const showDetail = ref(false)
const selectedBook = ref(null)

// 计算当前页显示的图书
const displayBooks = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return books.value.slice(start, end)
})

// 处理页码改变
const handlePageChange = (page) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取图书列表
const fetchBooks = async () => {
  try {
    const response = await bookApi.getBookList()
    if (Array.isArray(response)) {
      books.value = response
    } else {
      console.error('获取图书列表失败:', response)
      books.value = []
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
    books.value = []
  }
}

// 获取所有分类
const fetchCategories = async () => {
  try {
    const response = await bookApi.getCategories()
    if (Array.isArray(response)) {
      categories.value = ['全部', ...response]
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 处理分类点击
const handleCategoryClick = async (category) => {
  selectedCategory.value = category
  currentPage.value = 1  // 重置页码
  try {
    const response = await bookApi.getBookList()
    if (Array.isArray(response)) {
      if (category === '全部') {
        books.value = response
      } else {
        books.value = response.filter(book => book.category === category)
      }
    }
  } catch (error) {
    console.error('获取图书失败:', error)
  }
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1  // 重置页码
  if (!searchQuery.value.trim()) {
    return fetchBooks()
  }
  
  try {
    const response = await bookApi.searchBooks(searchQuery.value)
    
    if (Array.isArray(response)) {
      books.value = response
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请重试')
  }
}

// 获取图片完整URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}

// 处理退出登录
const handleLogout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  isLoggedIn.value = false
  username.value = ''
  ElMessage({
    message: '退出登录成功',
    type: 'success',
    showClose: true,
    center: true
  })
  router.push('/login')
}

// 显示图书详情
const showBookDetail = (book) => {
  selectedBook.value = book
  showDetail.value = true
}

// 复制网盘链接
const copyUrl = async () => {
  if (!selectedBook.value?.downloadUrl) {
    ElMessage.warning('暂无下载链接')
    return
  }
  
  try {
    await navigator.clipboard.writeText(selectedBook.value.downloadUrl)
    ElNotification({
      title: '成功',
      message: '网盘链接已复制到剪贴板',
      type: 'success',
      duration: 2000
    })
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 处理清除搜索框
const handleClear = () => {
  fetchBooks()  // 重新获取所有图书
  selectedCategory.value = '全部'  // 重置分类选择
}

// 计算图书总数
const totalBooks = computed(() => books.value.length)

onMounted(async () => {
  await Promise.all([
    fetchBooks(),
    fetchCategories()
  ])
})
</script>

<style scoped>
.books-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 5px 20px;
}

.section-title {
  margin-bottom: 15px;
}

.section-title h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
  padding: 3px 0;
}

.search-section {
  padding: 40px 20px 25px;
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  position: relative;
  overflow: hidden;
}

.search-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at top right, rgba(255,255,255,0.4) 0%, transparent 70%),
    radial-gradient(circle at bottom left, rgba(255,255,255,0.4) 0%, transparent 70%);
  pointer-events: none;
}

.search-title {
  color: #2c3e50;
  text-shadow: 1px 1px 2px rgb(7, 7, 7);
  font-size: 3.2em;
}

.search-subtitle {
  color: #343a40;
  font-size: 1.2em;
  margin-top: 5px;
}

.brand {
  margin-bottom: 25px;
}

.subtitle-wrapper {
  margin-bottom: 5px;
}

.category-segment {
  margin: 15px 0 5px;
  text-align: center;
}

.category-segment :deep(.el-segmented) {
  transition: all 0.3s ease;
}

/* 图书总数标签样式 */
.book-count-tag {
  position: fixed;
  top: 20px;
  left: 30px;
  z-index: 10;
}

.book-count-tag :deep(.el-tag) {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  font-size: 14px;
  border-radius: 20px;
  background-color: rgba(64, 64, 64, 0.9);
  height: 32px;
}

.book-count-tag span {
  display: inline-flex;
  align-items: center;
  height: 16px;
  line-height: 16px;
}

.search-box {
  max-width: 800px;
  margin: 0 auto;
  padding: 10px 0 0;
}

.search-input :deep(.el-input__wrapper) {
  width: 100%;
  height: 40px;
  line-height: 40px;
}

.search-input :deep(.el-input-group__append .el-button) {
  height: 40px;
  padding: 0 15px;
}

.search-input :deep(.el-input__inner) {
  line-height: 40px;
  height: 40px;
}
</style>
