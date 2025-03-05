<template>
  <div class="carousel-section">
    <el-carousel :interval="4000" type="card" height="300px">
      <el-carousel-item v-for="item in carouselItems" :key="item.id">
        <img 
          :src="item.imageUrl" 
          class="carousel-image"
          alt="carousel image"
        />
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const carouselItems = ref([
  { id: 1, imageUrl: '/api/photo/photo1.jpg' },
  { id: 2, imageUrl: '/api/photo/photo2.jpg' },
  { id: 3, imageUrl: '/api/photo/photo3.jpg' }
])
</script>

<style scoped>
.carousel-section {
  max-width: 1200px;
  margin: 5px auto 0;
  padding: 0 20px;
}

:deep(.el-carousel__item) {
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel-image {
  width: 75%;
  height: 75%;
  object-fit: cover;
  border-radius: 12px;
  display: block;
  margin: 0 auto;
}

:deep(.el-carousel--card .el-carousel__item) {
  border-radius: 12px;
}

:deep(.el-carousel__mask) {
  border-radius: 12px;
}
</style> 