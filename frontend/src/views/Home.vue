<template>
  <div class="home">
    <!-- 登录按钮 -->
    <div class="login-section">
      <el-button type="primary" @click="goToLogin" v-if="!isLoggedIn">
        登录
      </el-button>
      <template v-else>
        <span class="username">{{ username }}</span>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </template>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-container">
        <h1 class="search-title">A_Library</h1>
        <p class="search-subtitle">
          <span class="gradient-text">发现</span>
          <span class="separator"> </span>
          <span class="gradient-text-2">探索</span>
          <span class="separator"> </span>
          <span class="gradient-text-3">阅读</span>
        </p>

        <!-- 添加分类按钮组 -->
        <div class="category-buttons">
          <el-button
            v-for="category in categories"
            :key="category"
            :class="{ active: selectedCategory === category }"
            @click="handleCategoryClick(category)"
          >
            {{ category }}
          </el-button>
        </div>

        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="搜索你感兴趣的图书..."
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>
    

    <div class="carousel-section">
      <el-carousel :interval="4000" type="card" height="340px">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <img 
            :src="item.imageUrl" 
            class="carousel-image"
            alt="carousel image"
          />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 图书展示区域 -->
    <div class="books-section">
      <div class="section-title">
        <h2>{{ selectedCategory === '全部' ? '推荐图书' : selectedCategory }}</h2>
        <el-button text>更多图书</el-button>
      </div>
      <el-row :gutter="30">
        <el-col 
          v-for="book in books" 
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
                :preview-src-list="[getImageUrl(book.cover)]"
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
    </div>

    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="showDetail"
      :title="selectedBook?.title"
      width="60%"
    >
      <div v-if="selectedBook" class="book-detail">
        <div class="detail-content">
          <div class="detail-cover">
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
          <div class="detail-info">
            <p><strong>编号：</strong>{{ selectedBook.code }}</p>
            <p><strong>作者：</strong>{{ selectedBook.author }}</p>
            <p><strong>分类：</strong>{{ selectedBook.category }}</p>
            <p><strong>描述：</strong>{{ selectedBook.description }}</p>
          </div>
        </div>
        <div class="detail-actions">
          <el-button type="primary" @click="downloadBook(selectedBook)">
            下载
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Picture } from '@element-plus/icons-vue'
import { bookApi } from '../api/book'

const router = useRouter()
const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true')
const username = ref(localStorage.getItem('username') || '')
const searchQuery = ref('')
const books = ref([])

// 添加分类相关的数据
const categories = ref(['全部'])
const selectedCategory = ref('全部')

// 添加详情相关的数据
const showDetail = ref(false)
const selectedBook = ref(null)

// 获取图书列表
const fetchBooks = async () => {
  try {
    const response = await bookApi.getBookList()
    console.log('Books response:', response)
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
    console.log('Categories response:', response)
    if (Array.isArray(response)) {
      categories.value = ['全部', ...response]
      console.log('Categories loaded:', categories.value)
    } else {
      console.error('获取分类失败：返回数据格式错误', response)
      categories.value = ['全部']
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    categories.value = ['全部']
  }
}

const carouselItems = ref([
  { id: 1, imageUrl: '/api/photo/photo1.jpg' },
  { id: 2, imageUrl: '/api/photo/photo2.jpg' },
  { id: 3, imageUrl: '/api/photo/photo3.jpg' }
])

// 处理分类点击
const handleCategoryClick = async (category) => {
  selectedCategory.value = category
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
const handleSearch = () => {
  console.log('搜索:', searchQuery.value)
  // 实现搜索逻辑
}

// 显示图书详情
const showBookDetail = (book) => {
  selectedBook.value = book
  showDetail.value = true
}

// 下载图书
const downloadBook = (book) => {
  // TODO: 实现下载功能
  console.log('下载图书:', book)
  ElMessage.success('开始下载...')
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
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 获取图片完整URL
const getImageUrl = (path) => {
  if (!path) return ''
  // 如果path已经是完整URL，直接返回
  if (path.startsWith('http')) return path
  // 根据后端配置的urlPrefix来拼接
  return `/api${path}`  // 或者直接使用 path，取决于后端存储的路径格式
}

// 确保在页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchBooks(),
    fetchCategories()
  ])
})
</script>

<style scoped>
.home {
  background-color: #e8e8e8;
  min-height: 100vh;
  width: 100%;
  position: relative;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

/* 登录区域样式 */
.login-section {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 100;
}

.username {
  color: #1890ff;
  font-weight: bold;
}

/* 搜索区域样式 */
.search-section {
  background: linear-gradient(90deg, #e6f7ff 0%, #91d5ff 100%);
  padding: 40px 20px;
  text-align: center;
}

.search-container {
  max-width: 800px;
  margin: 0 auto;
}

.search-title {
  font-size: 2.5em;
  color: #1890ff;
  margin: 0 0 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  cursor: default;
}

.search-subtitle {
  font-size: 1.2em;
  margin: 0 0 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  cursor: default;
}

.separator {
  color: #666;
}

.gradient-text {
  background: linear-gradient(270deg, 
    #1890ff, #39c5bb, #4cd964, #39c5bb, #1890ff
  );
  background-size: 300% auto;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: bold;
  animation: flow 4s linear infinite;
}

.gradient-text-2 {
  background: linear-gradient(270deg, 
    #ff6b6b, #ffd93d, #ff9f43, #ffd93d, #ff6b6b
  );
  background-size: 300% auto;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: bold;
  animation: flow 4s linear infinite;
  animation-delay: 0.3s;
}

.gradient-text-3 {
  background: linear-gradient(270deg, 
    #6c5ce7, #a8e6cf, #00d2d3, #a8e6cf, #6c5ce7
  );
  background-size: 300% auto;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: bold;
  animation: flow 4s linear infinite;
  animation-delay: 0.6s;
}

@keyframes flow {
  0% {
    background-position: 0% center;
  }
  50% {
    background-position: 150% center;
  }
  100% {
    background-position: 300% center;
  }
}

/* 保持悬停效果但调整动画 */
.gradient-text,
.gradient-text-2,
.gradient-text-3 {
  transition: transform 0.3s ease;
}

.gradient-text:hover,
.gradient-text-2:hover,
.gradient-text-3:hover {
  transform: scale(1.1);
  animation-play-state: paused; /* 悬停时暂停动画 */
}

.search-box {
  max-width: 600px;
  margin: 0 auto;
}

.search-input :deep(.el-input__wrapper) {
  padding-right: 0;
}

.search-input :deep(.el-input-group__append) {
  padding: 0;
}

.search-input :deep(.el-input-group__append .el-button) {
  margin: 0;
  border: none;
  height: 100%;
  padding: 0 20px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px; /* 图片也添加圆角 */
}

/* 调整卡片式轮播图的样式 */
:deep(.el-carousel--card .el-carousel__item) {
  border-radius: 12px;
}

:deep(.el-carousel__mask) {
  border-radius: 12px;
}

/* 图书展示区域样式 */
.books-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  cursor: default;
}

.book-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover {
  height: 240px;
  overflow: hidden;
}

.image-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
}

.book-info {
  padding: 10px;
}

.book-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: default;
}

.book-author,
.book-category {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
  cursor: default;
}

/* 添加分类按钮组样式 */
.category-buttons {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
}

.category-buttons .el-button {
  margin: 0;
  transition: all 0.3s;
  cursor: pointer;
}

.category-buttons .el-button.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.category-buttons .el-button:hover {
  transform: translateY(-2px);
}

/* 调整搜索框上边距 */
.search-box {
  margin-top: 20px;
}

/* 响应式布局调整 */
@media (max-width: 768px) {
  .login-section {
    position: static;
    justify-content: flex-end;
    padding: 10px;
  }

  .search-section {
    padding: 20px;
  }

  .search-title {
    font-size: 2em;
  }

  .search-subtitle {
    font-size: 1em;
  }

  .category-buttons {
    gap: 8px;
  }
  
  .category-buttons .el-button {
    padding: 8px 15px;
    font-size: 14px;
  }
}

/* 禁用文本选择和输入光标 */
.home {
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.search-title,
.search-subtitle,
.section-title,
.book-title,
.book-author,
.book-category {
  cursor: default;
}

/* 保持搜索框可以输入 */
.search-input {
  user-select: text;
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  cursor: text;
}

/* 保持按钮的手型光标 */
.el-button {
  cursor: pointer;
}

/* 详情对话框样式 */
.book-detail {
  padding: 20px;
}

.detail-content {
  display: flex;
  gap: 30px;
}

.detail-cover {
  width: 200px;
  height: 280px;
  overflow: hidden;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-info {
  flex: 1;
}

.detail-info p {
  margin: 10px 0;
  line-height: 1.6;
}

.detail-actions {
  margin-top: 20px;
  text-align: right;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
    align-items: center;
  }
  
  .detail-cover {
    margin-bottom: 20px;
  }
  
  .detail-info {
    width: 100%;
  }
}

/* 轮播图区域样式 */
.carousel-section {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

/* 添加轮播图圆角样式 */
:deep(.el-carousel__item) {
  border-radius: 12px;
  overflow: hidden;
}
</style>
