<template>
  <div class="announcement-container">
    <h1 class="announcement-title">通知公告</h1>
    
    <div class="announcement-list" v-loading="loading">
      <div v-if="announcements.length === 0" class="empty-tip">
        暂无公告
      </div>
      
      <el-card v-for="(item, index) in announcements" :key="index" class="announcement-card">
        <div class="announcement-header">
          <h2>{{ item.title }}</h2>
          <span class="time">{{ formatDate(item.createTime) }}</span>
        </div>
        <div class="announcement-content" v-html="formatContent(item.content)">
        </div>
      </el-card>
      
      <div class="pagination" v-if="announcements.length > 0">
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取公告列表
const getAnnouncements = async () => {
  loading.value = true
  try {
    const res = await request.get('/announcement/list', {
      params: {
        limit: 1000 // 获取足够多的公告
      }
    })
    announcements.value = res.data || []
    total.value = announcements.value.length
  } catch (error) {
    console.error('获取公告列表失败', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 格式化内容，将文本中的换行符转换为HTML标签，保留原格式
const formatContent = (content) => {
  if (!content) return ''
  
  // 处理段落和换行
  return content
    .replace(/\n/g, '<br>')
    .replace(/►/g, '<span class="bullet">►</span>')
    .replace(/•/g, '<span class="bullet">•</span>')
    .replace(/✓/g, '<span class="bullet">✓</span>')
}

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  window.scrollTo(0, 0)
}

onMounted(() => {
  getAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.announcement-title {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.announcement-list {
  margin-bottom: 20px;
}

.announcement-card {
  margin-bottom: 20px;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.announcement-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.time {
  color: #999;
  font-size: 14px;
}

.announcement-content {
  color: #666;
  line-height: 1.8;
}

.announcement-content :deep(.bullet) {
  color: #409EFF;
  margin-right: 5px;
}

.empty-tip {
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style> 