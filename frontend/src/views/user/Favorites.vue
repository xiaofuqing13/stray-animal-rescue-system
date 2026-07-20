<template>
  <div class="favorites-page">
    <el-card>
      <template #header>
        <div class="header">
          <h2>我的收藏</h2>
          <el-tag type="info">共 {{ total }} 个收藏</el-tag>
        </div>
      </template>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 收藏列表 -->
      <div v-else-if="favorites.length > 0" class="favorites-list">
        <div 
          v-for="item in favorites" 
          :key="item.id"
          class="favorite-item"
          @click="viewDetail(item.animalId)"
        >
          <div class="animal-image">
            <img :src="item.animal.imageUrl || '/default-animal.jpg'" :alt="item.animal.name" />
          </div>
          <div class="animal-info">
            <h3>{{ item.animal.name }}</h3>
            <div class="info-row">
              <el-tag size="small">{{ getCategoryName(item.animal.categoryId) }}</el-tag>
              <el-tag size="small" type="info">{{ item.animal.breed }}</el-tag>
              <el-tag size="small" :type="item.animal.gender === 0 ? 'primary' : 'danger'">
                {{ item.animal.gender === 0 ? '雄性' : '雌性' }}
              </el-tag>
            </div>
            <div class="description">
              {{ item.animal.description || '暂无描述' }}
            </div>
            <div class="meta">
              <span>收藏时间：{{ formatDate(item.createTime) }}</span>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" size="small" @click.stop="viewDetail(item.animalId)">
              查看详情
            </el-button>
            <el-button type="danger" size="small" @click.stop="removeFavorite(item.animalId)">
              取消收藏
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无收藏内容">
        <el-button type="primary" @click="goToAnimalList">去看看动物</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

const loading = ref(false)
const favorites = ref([])
const total = ref(0)
const categories = ref([])

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/favorites')
    if (res.code === 200) {
      // 转换数据结构，将扁平的Map转换为嵌套结构
      const data = res.data || []
      favorites.value = data.map(item => ({
        id: item.id,
        userId: item.user_id,
        animalId: item.animal_id,
        createTime: item.create_time,
        animal: {
          id: item.animal_id,
          name: item.name,
          categoryId: item.category_id,
          breed: item.breed,
          gender: item.gender,
          age: item.age,
          weight: item.weight,
          healthStatus: item.health_status,
          features: item.features,
          description: item.description,
          imageUrl: item.image_url,
          status: item.status,
          viewCount: item.view_count,
          likeCount: item.like_count,
          favoriteCount: item.favorite_count
        }
      }))
      total.value = favorites.value.length
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载收藏失败', error)
    ElMessage.error('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

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

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看详情
const viewDetail = (animalId) => {
  router.push(`/user/animal/detail/${animalId}`)
}

// 取消收藏
const removeFavorite = async (animalId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.delete(`/user/recommendation/favorite/${animalId}`)
    if (res.code === 200) {
      ElMessage.success('已取消收藏')
      loadFavorites() // 重新加载列表
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 去动物列表
const goToAnimalList = () => {
  router.push('/user/animal')
}

// 初始化
onMounted(() => {
  loadCategories()
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
}

.loading {
  padding: 20px;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.favorite-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.favorite-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.animal-image {
  width: 200px;
  height: 150px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 8px;
}

.animal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.animal-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.animal-info h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.info-row {
  display: flex;
  gap: 10px;
}

.description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.meta {
  font-size: 12px;
  color: #999;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
}

@media (max-width: 768px) {
  .favorite-item {
    flex-direction: column;
  }

  .animal-image {
    width: 100%;
    height: 200px;
  }

  .actions {
    flex-direction: row;
  }
}
</style>
