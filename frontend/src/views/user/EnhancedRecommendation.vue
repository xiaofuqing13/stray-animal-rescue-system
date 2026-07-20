<template>
  <div class="recommendation-page">
    <el-card class="header-card">
      <template #header>
        <div class="header">
          <div>
            <h2>智能推荐</h2>
            <p class="subtitle">基于您的偏好为您推荐合适的宠物</p>
          </div>
          <el-button type="primary" @click="goToPreference">
            <el-icon><Setting /></el-icon>
            偏好设置
          </el-button>
        </div>
      </template>

      <!-- 推荐类型切换 -->
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="个性化推荐" name="personalized">
          <span class="tab-icon"><el-icon><MagicStick /></el-icon></span>
        </el-tab-pane>
        <el-tab-pane label="热门推荐" name="hot">
          <span class="tab-icon"><el-icon><TrendCharts /></el-icon></span>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 推荐列表 -->
    <div v-else-if="animals.length > 0" class="animal-grid">
      <div 
        v-for="animal in animals" 
        :key="animal.id"
        class="animal-card"
        @click="viewDetail(animal.id)"
      >
        <el-card shadow="hover">
          <div class="animal-image">
            <img :src="animal.imageUrl || '/default-animal.jpg'" :alt="animal.name" />
            <div class="status-badge" v-if="animal.status === 0">
              <el-tag type="success" size="small">可领养</el-tag>
            </div>
          </div>
          <div class="animal-info">
            <h3>{{ animal.name }}</h3>
            <div class="tags">
              <el-tag size="small">{{ getCategoryName(animal.categoryId) }}</el-tag>
              <el-tag size="small" type="info">{{ animal.breed }}</el-tag>
            </div>
            <div class="description">
              {{ animal.description || '暂无描述' }}
            </div>
            <div class="stats">
              <span><el-icon><View /></el-icon> {{ animal.viewCount || 0 }}</span>
              <span><el-icon><Star /></el-icon> {{ animal.likeCount || 0 }}</span>
              <span><el-icon><Collection /></el-icon> {{ animal.favoriteCount || 0 }}</span>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" size="small" @click.stop="viewDetail(animal.id)">
              查看详情
            </el-button>
            <el-button 
              size="small" 
              :type="animal.isFavorited ? 'warning' : 'default'"
              @click.stop="toggleFavorite(animal)"
            >
              <el-icon><Star /></el-icon>
              {{ animal.isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-else description="暂无推荐内容">
      <el-button type="primary" @click="goToPreference">去设置偏好</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Setting, MagicStick, TrendCharts, View, Star, Collection } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

const loading = ref(false)
const activeTab = ref('personalized')
const animals = ref([])
const categories = ref([])

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await request.get('/user/animal/categories')
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未知'
}

// 加载推荐列表
const loadRecommendations = async () => {
  loading.value = true
  try {
    let url = ''
    if (activeTab.value === 'personalized') {
      url = '/user/recommendation/personalized'
    } else {
      url = '/user/recommendation/hot'
    }

    const res = await request.get(url, {
      params: { limit: 12 }
    })

    if (res.code === 200) {
      animals.value = res.data || []
      // 加载每个动物的互动状态
      await loadInteractionStatus()
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载推荐失败', error)
    ElMessage.error('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 加载互动状态
const loadInteractionStatus = async () => {
  for (const animal of animals.value) {
    try {
      const res = await request.get(`/user/recommendation/interaction/${animal.id}`)
      if (res.code === 200) {
        animal.isLiked = res.data.isLiked
        animal.isFavorited = res.data.isFavorited
      }
    } catch (error) {
      console.error('加载互动状态失败', error)
    }
  }
}

// 切换标签
const handleTabChange = () => {
  loadRecommendations()
}

// 查看详情
const viewDetail = (animalId) => {
  router.push(`/user/animal/detail/${animalId}`)
}

// 切换收藏
const toggleFavorite = async (animal) => {
  try {
    if (animal.isFavorited) {
      // 取消收藏
      const res = await request.delete(`/user/recommendation/favorite/${animal.id}`)
      if (res.code === 200) {
        animal.isFavorited = false
        animal.favoriteCount = Math.max(0, (animal.favoriteCount || 0) - 1)
        ElMessage.success('已取消收藏')
      }
    } else {
      // 收藏
      const res = await request.post(`/user/recommendation/favorite/${animal.id}`)
      if (res.code === 200) {
        animal.isFavorited = true
        animal.favoriteCount = (animal.favoriteCount || 0) + 1
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    console.error('操作失败', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 去偏好设置
const goToPreference = () => {
  router.push('/user/preference')
}

// 初始化
onMounted(() => {
  loadCategories()
  loadRecommendations()
})
</script>

<style scoped>
.recommendation-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-card {
  margin-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.tab-icon {
  margin-right: 5px;
}

.loading-container {
  padding: 20px;
}

.animal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.animal-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.animal-card:hover {
  transform: translateY(-5px);
}

.animal-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 15px;
}

.animal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
}

.animal-info {
  padding: 0 10px;
}

.animal-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 42px;
}

.stats {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 3px;
}

.actions {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-top: 1px solid #f0f0f0;
}

.actions .el-button {
  flex: 1;
}

@media (max-width: 768px) {
  .animal-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }
}
</style>
