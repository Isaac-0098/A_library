<script setup>
</script>

<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style>
.app-container {
  height: 100vh;
  margin: 0;
  padding: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 添加全局平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 添加全局过渡效果 */
* {
  transition: background-color 0.3s, color 0.3s, border-color 0.3s, box-shadow 0.3s;
}

/* 优化按钮点击效果 */
.el-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button:active {
  transform: scale(0.96);
}

/* 优化卡片悬停效果 */
.el-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 优化对话框过渡 */
.el-dialog {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-dialog__wrapper {
  transition: opacity 0.3s ease !important;
}
</style>
