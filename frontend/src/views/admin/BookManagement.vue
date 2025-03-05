<template>
  <div class="book-management-container">
    <div class="book-management">
      <div class="header">
        <h2>图书管理</h2>
        <el-button type="primary" @click="handleAdd">
          添加图书
        </el-button>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="输入书名搜索..."
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

      <!-- 图书列表 -->
      <el-table 
        :data="books" 
        v-loading="loading"
        class="book-table"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column prop="title" label="书名" width="200" />
        <el-table-column prop="author" label="作者" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="downloadUrl" label="网盘链接" min-width="200">
          <template #default="{ row }">
            <div class="download-url-cell">
              <el-input 
                v-model="row.downloadUrl" 
                readonly
                size="small"
              >
                <template #append>
                  <el-button @click.stop="copyUrl(row.downloadUrl)">
                    <el-icon><DocumentCopy /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="封面" width="100">
          <template #default="scope">
            <el-image
              style="width: 50px; height: 70px"
              :src="getImageUrl(scope.row.cover)"
              :preview-src-list="[getImageUrl(scope.row.cover)]"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleEdit(scope.row)"
            >
              编辑
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

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          @update:page-size="val => pageSize = val"
        />
      </div>

      <!-- 添加/编辑图书对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加图书' : '编辑图书'"
        width="600px"
      >
        <el-form
          ref="bookFormRef"
          :model="bookForm"
          :rules="bookRules"
          label-width="80px"
        >
          <el-form-item label="ID" prop="id">
            <el-input v-model="bookForm.id" disabled />
          </el-form-item>
          <el-form-item label="编号" prop="code">
            <el-input v-model="bookForm.code" disabled />
          </el-form-item>
          <el-form-item label="书名" prop="title">
            <el-input v-model="bookForm.title" />
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="bookForm.author" />
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-select v-model="bookForm.category" style="width: 100%">
              <el-option
                v-for="category in categories"
                :key="category"
                :label="category"
                :value="category"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input
              v-model="bookForm.description"
              type="textarea"
              :rows="3"
            />
          </el-form-item>
          <el-form-item label="封面路径" prop="cover">
            <el-input v-model="bookForm.cover" placeholder="请输入图片路径" />
          </el-form-item>
          <el-form-item label="网盘链接" prop="downloadUrl">
            <el-input v-model="bookForm.downloadUrl" placeholder="请输入网盘链接" />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { Search, Picture, DocumentCopy } from '@element-plus/icons-vue'
import { bookApi } from '../../api/book'

const loading = ref(false)
const books = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('add')
const bookFormRef = ref(null)
const searchQuery = ref('')
const categoryFilter = ref('')

// 图书分类
const categories = ref([])

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await bookApi.getCategories()
    console.log('Categories response:', response) // 检查返回的数据
    categories.value = response // 直接使用返回的数组
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

const bookForm = reactive({
  id: '',
  code: '',
  title: '',
  author: '',
  category: '',
  description: '',
  cover: '',
  downloadUrl: ''
})

const bookRules = {
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
}

// 获取图书列表
const fetchBooks = async () => {
  loading.value = true
  try {
    let result
    if (searchQuery.value) {
      result = await bookApi.searchBooks(searchQuery.value)
    } else {
      result = await bookApi.getBookList()
    }
    
    total.value = result.length // 设置总数
    // 手动处理分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    books.value = result.slice(start, end) // 只显示当前页的数据
  } catch (error) {
    console.error('获取图书列表失败:', error)
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
}

// 监听分类筛选变化
watch(categoryFilter, () => {
  handleSearch()
})

// 添加图书
const handleAdd = async () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  
  try {
    // 获取最后一本书的信息
    const books = await bookApi.getBookList()
    const lastBook = books[books.length - 1]
    
    // 生成新的ID和编号
    const newId = lastBook ? lastBook.id + 1 : 1
    const lastCode = lastBook ? lastBook.code : 'BK000'
    const lastNumber = parseInt(lastCode.slice(2)) || 0
    const newCode = `BK${String(lastNumber + 1).padStart(3, '0')}`
    
    Object.assign(bookForm, {
      id: newId,
      code: newCode,
      title: '',  // 书名需要管理员填写
      author: '',
      category: '',
      description: '',
      cover: '',
      downloadUrl: ''
    })
  } catch (error) {
    ElMessage.error('获取图书信息失败')
  }
}

// 编辑图书
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(bookForm, row)
}

// 提交表单
const handleSubmit = async () => {
  if (!bookFormRef.value) return
  
  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await bookApi.addBook(bookForm)
          ElMessage.success('添加成功')
        } else {
          await bookApi.updateBook(bookForm)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchBooks()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}

// 删除图书
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该图书吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await bookApi.deleteBook(row.id)
      ElMessage.success('删除成功')
      fetchBooks()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchBooks()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchBooks()
}

// 获取图片完整URL
const getImageUrl = (path) => {
  if (!path) return ''
  // 如果path已经是完整URL，直接返回
  if (path.startsWith('http')) return path
  // 根据后端配置的urlPrefix来拼接
  return `/api${path}`  // 或者直接使用 path，取决于后端存储的路径格式
}

// 添加复制网盘链接功能
const copyUrl = async (url) => {
  if (!url) {
    ElMessage.warning('暂无下载链接')
    return
  }
  
  try {
    await navigator.clipboard.writeText(url)
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

onMounted(async () => {
  await fetchCategories() // 先获取分类
  fetchBooks() // 再获取图书列表
})
</script>

<style scoped>
.book-management-container {
  display: flex;
  justify-content: center;
  min-height: 100%;
  padding: 20px;
}

.book-management {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cover-uploader,
.cover-uploader-icon,
.cover {
  display: none;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.book-table {
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

.download-url-cell {
  max-width: 300px;
}

.download-url-cell :deep(.el-input-group__append) {
  padding: 0;
}

.download-url-cell :deep(.el-button) {
  border: none;
  height: 28px;
  padding: 0 8px;
}
</style> 