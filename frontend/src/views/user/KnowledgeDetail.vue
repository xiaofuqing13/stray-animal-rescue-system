<template>
  <div class="knowledge-detail">
    <div class="container" v-loading="loading">
      <div v-if="article" class="article-container">
        <div class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(article.createTime) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>{{ article.viewCount }} 阅读</span>
            </div>
            <div class="meta-item category">
              <el-tag size="small" effect="plain">{{ article.categoryName }}</el-tag>
            </div>
          </div>
        </div>
        
        <div class="article-cover" v-if="article.imageUrl">
          <img :src="article.imageUrl" :alt="article.title">
        </div>
        
        <div class="article-content">
          <div v-html="article.content" class="content-body"></div>
        </div>
        
        <div class="video-section" v-if="article.videoUrl">
          <h3>相关视频</h3>
          <div class="video-container">
            <video 
              :src="article.videoUrl" 
              :poster="article.videoCoverUrl || article.imageUrl"
              controls
              class="article-video"
            ></video>
          </div>
        </div>
        
        <div class="article-navigation">
          <div class="prev-next">
            <div v-if="prevArticle" class="prev">
              <span class="label">上一篇</span>
              <a @click="goToArticle(prevArticle.id)">{{ prevArticle.title }}</a>
            </div>
            <div v-if="nextArticle" class="next">
              <span class="label">下一篇</span>
              <a @click="goToArticle(nextArticle.id)">{{ nextArticle.title }}</a>
            </div>
          </div>
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </div>
      </div>
      
      <el-empty v-else-if="!loading" description="文章不存在或已被删除">
        <el-button type="primary" @click="goBack">返回列表</el-button>
      </el-empty>
    </div>
    
    <div class="related-articles" v-if="relatedArticles.length > 0">
      <h2 class="section-title">相关文章</h2>
      <div class="related-grid">
        <div 
          v-for="item in relatedArticles" 
          :key="item.id" 
          class="related-card"
          @click="goToArticle(item.id)"
        >
          <div class="related-image">
            <img :src="item.imageUrl || DEFAULT_IMAGE_PLACEHOLDER" :alt="item.title">
          </div>
          <div class="related-content">
            <h3 class="related-title">{{ item.title }}</h3>
            <div class="related-meta">
              <span class="category-tag">{{ item.categoryName }}</span>
              <span class="view-count">
                <el-icon><View /></el-icon> {{ item.viewCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Calendar, View } from '@element-plus/icons-vue'
import { getKnowledgeArticleDetail, getRelatedArticles } from '@/api/knowledge'
import { formatDate } from '@/utils/date'
import { DEFAULT_IMAGE_PLACEHOLDER } from '@/utils/index'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const article = ref(null)
const prevArticle = ref(null)
const nextArticle = ref(null)
const relatedArticles = ref([])

onMounted(() => {
  const articleId = route.params.id
  if (articleId) {
    fetchArticleDetail(articleId)
  } else {
    loading.value = false
    ElMessage.error('文章ID不能为空')
  }
})

// 获取文章详情
const fetchArticleDetail = (id) => {
  loading.value = true
  getKnowledgeArticleDetail(id)
    .then(response => {
      if (response.code === 200) {
        article.value = response.data.article
        prevArticle.value = response.data.prevArticle
        nextArticle.value = response.data.nextArticle
        
        // 获取相关文章
        fetchRelatedArticles(article.value.categoryId, id)
      } else {
        ElMessage.error(response.msg || '获取文章详情失败')
      }
    })
    .catch(error => {
      console.error('获取文章详情失败:', error)
      ElMessage.error('获取文章详情失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 获取相关文章
const fetchRelatedArticles = (categoryId, currentId) => {
  if (!categoryId) return
  
  getRelatedArticles(categoryId, currentId)
    .then(response => {
      if (response.code === 200) {
        relatedArticles.value = response.data
      }
    })
    .catch(error => {
      console.error('获取相关文章失败:', error)
    })
}

// 跳转到文章详情
const goToArticle = (id) => {
  if (id === route.params.id) return
  router.push(`/user/knowledge/detail/${id}`)
  // 页面滚动到顶部
  window.scrollTo(0, 0)
  // 重新加载文章详情
  fetchArticleDetail(id)
}

// 返回列表
const goBack = () => {
  router.push('/user/knowledge')
}
</script>

<style scoped>
.knowledge-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.article-header {
  margin-bottom: 20px;
}

.article-title {
  font-size: 32px;
  color: #333;
  margin-bottom: 16px;
  font-weight: 700;
}

.article-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item .el-icon {
  margin-right: 6px;
}

.category {
  margin-left: auto;
}

.article-cover {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
}

.article-content .content-body {
  white-space: pre-wrap;
  word-break: break-word;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
  margin: 10px 0;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3),
.article-content :deep(h4),
.article-content :deep(h5),
.article-content :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.article-content :deep(p) {
  margin-bottom: 16px;
  text-align: justify;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  margin-bottom: 16px;
  padding-left: 20px;
}

.article-content :deep(li) {
  margin-bottom: 8px;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #ddd;
  padding: 10px 15px;
  color: #666;
  background-color: #f8f8f8;
  margin: 16px 0;
}

.article-content :deep(pre) {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 16px 0;
}

.article-content :deep(code) {
  background-color: #f0f0f0;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: Consolas, Monaco, monospace;
}

.video-section {
  margin-bottom: 30px;
}

.video-section h3 {
  font-size: 24px;
  margin-bottom: 16px;
  font-weight: 600;
}

.video-container {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.article-video {
  width: 100%;
  max-height: 500px;
  background-color: #000;
}

.article-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.prev-next {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.prev, .next {
  max-width: 400px;
}

.prev .label, .next .label {
  font-size: 14px;
  color: #999;
  margin-right: 8px;
}

.prev a, .next a {
  color: #409eff;
  cursor: pointer;
  text-decoration: none;
}

.prev a:hover, .next a:hover {
  text-decoration: underline;
}

.section-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  font-weight: 600;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.related-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
  background-color: #fff;
}

.related-card:hover {
  transform: translateY(-5px);
}

.related-image {
  height: 150px;
  overflow: hidden;
}

.related-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.related-card:hover .related-image img {
  transform: scale(1.05);
}

.related-content {
  padding: 12px;
}

.related-title {
  font-size: 16px;
  margin-bottom: 8px;
  color: #333;
  /* 单行标题，超出省略 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.related-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.category-tag {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 1px 6px;
  border-radius: 4px;
}

.view-count {
  color: #909399;
  display: flex;
  align-items: center;
}

.view-count .el-icon {
  margin-right: 4px;
}

@media (max-width: 1200px) {
  .related-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 20px;
  }
  
  .article-title {
    font-size: 24px;
  }
  
  .related-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .related-grid {
    grid-template-columns: 1fr;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .category {
    margin-left: 0;
  }
}
</style> 