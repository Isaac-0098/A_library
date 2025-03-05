<script setup>
import { ref } from 'vue'
import { bookApi } from '../api/book'
import { ElMessage } from 'element-plus'

const props = defineProps({
  book: {
    type: Object,
    default: () => ({})
  },
  mode: {
    type: String,
    default: 'add' // 'add' 或 'edit'
  }
})

const emit = defineEmits(['success', 'cancel'])

const formData = ref({
  id: props.book.id || '',
  code: props.book.code || '',
  title: props.book.title || '',
  author: props.book.author || '',
  cover: props.book.cover || '',
  category: props.book.category || '',
  description: props.book.description || ''
})

// 添加表单验证规则
const rules = {
  code: [{ required: true, message: '请输入图书编号', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }]
}

const formRef = ref(null)

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 构造参数对象
    const params = {
      id: props.mode === 'edit' ? formData.value.id : 0, // 添加时使用0作为默认id
      code: formData.value.code || '',
      title: formData.value.title || '',
      author: formData.value.author || '',
      cover: formData.value.cover || '',
      category: formData.value.category || '',
      description: formData.value.description || ''
    }
    
    // 确保所有值都是字符串类型
    Object.keys(params).forEach(key => {
      params[key] = String(params[key])
    })
    
    if (props.mode === 'add') {
      await bookApi.addBook(params)
      ElMessage.success('添加成功')
    } else {
      await bookApi.updateBook(params)
      ElMessage.success('更新成功')
    }
    emit('success')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error(props.mode === 'add' ? '添加失败' : '更新失败')
    }
    console.error(error)
  }
}

const handleCancel = () => {
  formRef.value?.resetFields()
  emit('cancel')
}
</script>

<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="rules"
    label-width="100px"
  >
    <el-form-item v-if="mode === 'edit'" label="ID">
      <el-input v-model="formData.id" disabled />
    </el-form-item>
    <el-form-item label="编号" prop="code">
      <el-input v-model="formData.code" />
    </el-form-item>
    <el-form-item label="书名" prop="title">
      <el-input v-model="formData.title" />
    </el-form-item>
    <el-form-item label="作者" prop="author">
      <el-input v-model="formData.author" />
    </el-form-item>
    <el-form-item label="封面" prop="cover">
      <el-input v-model="formData.cover" />
    </el-form-item>
    <el-form-item label="分类" prop="category">
      <el-input v-model="formData.category" />
    </el-form-item>
    <el-form-item label="描述" prop="description">
      <el-input
        type="textarea"
        v-model="formData.description"
        :rows="4"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit">
        {{ mode === 'add' ? '添加' : '更新' }}
      </el-button>
      <el-button @click="handleCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template> 