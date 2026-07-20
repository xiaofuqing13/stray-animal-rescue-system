<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <el-result
        icon="error"
        title="404"
        sub-title="抱歉，您访问的页面不存在">
        <template #extra>
          <el-button type="primary" @click="goBack">返回上一页</el-button>
          <el-button type="default" @click="goHome">返回首页</el-button>
        </template>
      </el-result>
      <div class="not-found-image">
        <el-icon :size="120" color="#909399"><PictureFilled /></el-icon>
        <h3>页面走丢啦~</h3>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { PictureFilled } from '@element-plus/icons-vue'

const router = useRouter()

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 返回首页
const goHome = () => {
  const userRole = localStorage.getItem('userRole')
  if (userRole === 'admin') {
    router.push('/admin/dashboard')
  } else if (userRole === 'user') {
    router.push('/user/home')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  max-width: 600px;
}

.not-found-image {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.not-found-image h3 {
  margin-top: 20px;
  font-weight: normal;
}
</style> 