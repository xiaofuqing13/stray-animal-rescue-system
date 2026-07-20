<template>
  <div class="knowledge-container">
    <div class="page-header">
      <h1>养护知识</h1>
      <p>在这里您可以了解到关于流浪动物的养护知识和技巧</p>
    </div>

    <div class="filter-bar">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索知识文章"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="categoryId"
            placeholder="选择分类"
            clearable
            @change="handleSearch"
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-col>
      </el-row>
    </div>

    <div class="knowledge-list" v-loading="loading">
      <el-empty v-if="articles.length === 0" description="暂无相关文章" />
      
      <div v-else class="article-grid">
        <div 
          v-for="article in articles" 
          :key="article.id" 
          class="article-card"
          @click="viewArticle(article.id)"
        >
          <div class="article-image">
            <img 
              :src="article.imageUrl || DEFAULT_IMAGE_PLACEHOLDER" 
              :alt="article.title"
            />
          </div>
          <div class="article-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <div class="article-meta">
              <span class="category-tag">{{ article.categoryName }}</span>
              <span class="view-count">
                <el-icon><View /></el-icon> {{ article.viewCount }}
              </span>
            </div>
            <p class="article-summary">
              {{ getArticleSummary(article.content) }}
            </p>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getKnowledgeArticlePage, getAllKnowledgeCategories } from '@/api/knowledge'
import { DEFAULT_IMAGE_PLACEHOLDER } from '@/utils/index'

const router = useRouter()
const loading = ref(false)
const articles = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')
const categoryId = ref(null)
const categoryOptions = ref([])

// 初始化
onMounted(() => {
  fetchCategories()
  fetchArticles()
})

// 获取文章摘要
const getArticleSummary = (content) => {
  if (!content) return '暂无描述'
  // 移除HTML标签，截取前100个字符
  const plainText = content.replace(/<[^>]+>/g, '')
  return plainText.length > 100 ? plainText.substring(0, 100) + '...' : plainText
}

// 加载分类
const fetchCategories = () => {
  getAllKnowledgeCategories()
    .then(response => {
      if (response.code === 200) {
        categoryOptions.value = response.data
      }
    })
    .catch(error => {
      console.error('获取知识分类失败:', error)
      ElMessage.error('获取知识分类失败')
    })
}

// 加载文章
const fetchArticles = () => {
  loading.value = true
  getKnowledgeArticlePage(currentPage.value, pageSize.value, searchKeyword.value, categoryId.value)
    .then(response => {
      if (response.code === 200) {
        articles.value = response.data.records
        total.value = response.data.total
      }
    })
    .catch(error => {
      console.error('获取知识文章失败:', error)
      ElMessage.error('获取知识文章失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 搜索
const handleSearch = () => {
  // 确保categoryId不是字符串"all"
  if (categoryId.value === 'all') {
    categoryId.value = null;
  }
  currentPage.value = 1;
  fetchArticles();
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchArticles()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchArticles()
}

// 查看文章详情
const viewArticle = (id) => {
  router.push(`/user/knowledge/detail/${id}`)
}
</script>

<style scoped>
.knowledge-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 36px;
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 16px;
  color: #666;
}

.filter-bar {
  margin-bottom: 30px;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 30px;
}

.article-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  background-color: #fff;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.article-image {
  height: 200px;
  overflow: hidden;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.05);
}

.article-content {
  padding: 16px;
}

.article-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
  font-weight: 600;
  /* 两行标题，超出省略 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 48px;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.category-tag {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
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

.article-summary {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  /* 三行摘要，超出省略 */
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 66px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 1200px) {
  .article-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .article-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-bar .el-col {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style> 