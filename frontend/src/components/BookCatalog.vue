<script setup>
import { ref, onMounted } from 'vue'
import { bookApi } from '../api/book'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import BookDetail from './BookDetail.vue'
import BookForm from './BookForm.vue'

const books = ref([])
const originalBooks = ref([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const selectedBook = ref(null)
const showDetail = ref(false)
const showAddForm = ref(false)

// 获取图书列表
const fetchBooks = async () => {
  try {
    const response = await bookApi.getAllBooks()
    if (Array.isArray(response)) {
      originalBooks.value = response
      books.value = [...response]
    } else {
      console.error('返回数据格式错误:', response)
      ElMessage.error('获取图书列表失败：数据格式错误')
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
    ElMessage.error(`获取图书列表失败: ${error.message || '未知错误'}`)
  }
}

// 搜索图书
const handleSearch = async () => {
  const query = searchQuery.value.toLowerCase().trim()
  try {
    if (query) {
      const response = await bookApi.searchBooks(query)
      books.value = response
    } else {
      books.value = [...originalBooks.value]
    }
    currentPage.value = 1
  } catch (error) {
    ElMessage.error('搜索图书失败')
    console.error(error)
  }
}

// 删除图书
const handleDeleteBook = async (id) => {
  try {
    await bookApi.deleteBook(id)
    ElMessage.success('删除成功')
    await fetchBooks()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error(error)
  }
}

const handleShowDetail = (book) => {
  selectedBook.value = book
  showDetail.value = true
}

const handleDetailClose = () => {
  showDetail.value = false
  selectedBook.value = null
}

const handleAddBook = () => {
  showAddForm.value = true
}

const handleAddSuccess = async () => {
  showAddForm.value = false
  await fetchBooks()
  ElMessage.success('添加图书成功')
}

onMounted(() => {
  fetchBooks()
})
</script>

<template>
  <div class="book-catalog">
    <!-- 搜索栏 -->
    <div class="top-bar">
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索图书..."
          class="search-input"
          clearable
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      <el-button type="primary" @click="handleAddBook">
        新增图书
      </el-button>
    </div>

    <!-- 图书列表 -->
    <div class="books-grid">
      <el-card 
        v-for="book in books" 
        :key="book.id" 
        class="book-card"
      >
        <div class="book-cover">
          <el-image 
            :src="book.cover" 
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
          <div class="book-actions">
            <el-button type="primary" size="small" @click="handleShowDetail(book)">
              查看详情
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteBook(book.id)">
              删除
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        @update:current-page="currentPage = $event"
        :page-size="pageSize"
        :total="books.length"
        layout="prev, pager, next"
      />
    </div>

    <!-- 添加详情对话框 -->
    <BookDetail
      v-if="selectedBook"
      :visible="showDetail"
      @update:visible="showDetail = $event"
      :book="selectedBook"
      @refresh="fetchBooks"
    />

    <!-- 添加新增图书的对话框 -->
    <el-dialog
      v-model="showAddForm"
      title="新增图书"
      width="60%"
    >
      <BookForm
        mode="add"
        @success="handleAddSuccess"
        @cancel="showAddForm = false"
      />
    </el-dialog>
  </div>
</template>

<style scoped>
.book-catalog {
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 300px;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.book-card {
  height: 100%;
}

.book-cover {
  height: 200px;
  overflow: hidden;
}

.book-info {
  padding: 10px;
}

.book-title {
  margin: 10px 0;
  font-size: 16px;
}

.book-author,
.book-category {
  margin: 5px 0;
  color: #666;
}

.book-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 