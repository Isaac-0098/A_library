<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import BookForm from './BookForm.vue'
import { bookApi } from '../api/book'

const props = defineProps({
  book: {
    type: Object,
    required: true
  },
  visible: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'refresh'])

const showEditForm = ref(false)

const handleEdit = () => {
  showEditForm.value = true
}

const handleClose = () => {
  showEditForm.value = false
  emit('update:visible', false)
}

const handleEditSuccess = async () => {
  showEditForm.value = false
  emit('update:visible', false)
  emit('refresh')
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="emit('update:visible', $event)"
    :title="showEditForm ? '编辑图书' : book.title"
    width="60%"
    @close="handleClose"
  >
    <div v-if="!showEditForm" class="book-detail">
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
      <div class="book-meta">
        <p class="book-code">{{ book.code }}</p>
        <h3>{{ book.title }}</h3>
        <p class="author">作者：{{ book.author }}</p>
        <p class="category">分类：{{ book.category }}</p>
        <div class="description">
          <h4>简介：</h4>
          <p>{{ book.description }}</p>
        </div>
      </div>
    </div>
    
    <BookForm
      v-else
      :book="book"
      mode="edit"
      @success="handleEditSuccess"
      @cancel="showEditForm = false"
    />

    <template #footer>
      <div v-if="!showEditForm">
        <el-button @click="handleEdit">编辑</el-button>
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.book-detail {
  display: flex;
  gap: 20px;
}

.book-cover {
  width: 200px;
  height: 300px;
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.book-meta {
  flex: 1;
}

.book-code {
  color: #999;
  font-size: 14px;
}

.author,
.category {
  margin: 10px 0;
}

.description {
  margin-top: 20px;
}

.description h4 {
  margin-bottom: 10px;
}
</style> 