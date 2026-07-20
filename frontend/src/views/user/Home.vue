<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <div class="carousel-section">
      <el-carousel height="500px" v-loading="carouselLoading" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in carousels" :key="index">
          <div class="carousel-item" :style="`background-image: url(${item.imageUrl})`">
            <!-- 轮播图内容区域已移除 -->
          </div>
        </el-carousel-item>
        <!-- 无数据时显示默认轮播图 -->
        <el-carousel-item v-if="carousels.length === 0">
          <div class="carousel-item" style="background-image: url(/uploads/carousel/default.jpg)">
            <!-- 轮播图内容区域已移除 -->
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 平台简介和通知公告 -->
    <div class="info-section">
      <el-row :gutter="20">
        <!-- 左侧平台简介 -->
        <el-col :xs="24" :md="12">
          <el-card class="platform-intro">
            <div class="intro-header">
              <h2>平台简介</h2>
            </div>
            <div class="intro-content">
              <p>
                欢迎来到流浪动物保护系统，这是一个致力于帮助流浪动物找到温暖家园的平台。
              </p>
              <p>
                我们的使命是通过搭建流浪动物与爱心人士之间的桥梁，为这些无家可归的小生命提供关爱和保护。
              </p>
              <p>
                在这里，您可以：
              </p>
              <ul>
                <li>浏览待领养的流浪动物信息</li>
                <li>提交领养申请</li>
                <li>了解流浪动物救助知识</li>
                <li>参与流浪动物保护社区讨论</li>
                <li>查看救助站动态和公告</li>
              </ul>
              <p>
                每一个生命都值得被尊重和善待，感谢您加入我们，共同为流浪动物创造更美好的未来！
              </p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 右侧通知公告 -->
        <el-col :xs="24" :md="12">
          <el-card class="announcement-board">
            <div class="board-header">
              <h2>通知公告</h2>
              <el-button type="text" @click="goToAnnouncementList">更多 >></el-button>
            </div>
            <div v-loading="announcementLoading">
              <div v-if="announcements.length === 0" class="empty-tip">
                暂无公告
              </div>
              <el-timeline v-else>
                <el-timeline-item
                  v-for="(item, index) in announcements"
                  :key="index"
                  :timestamp="formatDate(item.createTime)"
                  placement="top"
                >
                  <div class="announcement-item" @click="openAnnouncementDetail(item)">
                    <h3>{{ item.title }}</h3>
                    <p class="announcement-summary">{{ truncateContent(item.content) }}</p>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 欢迎内容 -->
    <div class="welcome-section">
      <h1>欢迎来到流浪动物保护系统</h1>
      <p>这里有许多可爱的流浪动物正在等待一个温暖的家</p>
      <p>您可以浏览领养信息、了解救助知识、参与社区讨论</p>
    </div>

    <!-- 热门动物 -->
    <div class="animal-section">
      <div class="section-title">
        <h2>🔥 热门动物</h2>
        <el-button type="text" @click="goToAnimalList">查看更多 >></el-button>
      </div>
      <div class="animal-grid" v-loading="hotAnimalsLoading">
        <div v-if="hotAnimals.length === 0" class="empty-tip">暂无动物信息</div>
        
        <div v-for="(animal, index) in hotAnimals" :key="'hot-' + index" class="animal-card" @click="viewAnimalDetail(animal)">
          <div class="animal-image">
            <img :src="animal.imageUrl || '/uploads/animal/default.jpg'" :alt="animal.name">
            <div class="animal-status hot-badge">热门</div>
          </div>
          <div class="animal-info">
            <h3 class="animal-name">{{ animal.name }}</h3>
            <div class="animal-meta">
              <el-tag size="small">{{ animal.params?.categoryName || '其他' }}</el-tag>
              <span class="animal-gender">
                <el-icon :color="animal.gender === 0 ? '#409EFF' : '#FF66AA'">
                  <Male v-if="animal.gender === 0" />
                  <Female v-else />
                </el-icon>
                {{ animal.gender === 0 ? '公' : '母' }}
              </span>
            </div>
            <div class="animal-stats">
              <span><el-icon><View /></el-icon> {{ animal.viewCount || 0 }}</span>
              <span><el-icon><Star /></el-icon> {{ animal.likeCount || 0 }}</span>
              <span><el-icon><Collection /></el-icon> {{ animal.favoriteCount || 0 }}</span>
            </div>
            <p class="animal-description">{{ truncateContent(animal.features || '可爱、温顺') }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 为你推荐 -->
    <div class="animal-section" v-if="isLoggedIn">
      <div class="section-title">
        <h2>💝 为你推荐</h2>
        <el-button type="text" @click="goToAnimalList">查看更多 >></el-button>
      </div>
      <div class="animal-grid" v-loading="recommendedAnimalsLoading">
        <div v-if="recommendedAnimals.length === 0" class="empty-tip">
          暂无推荐，多浏览、点赞、收藏动物后会有更精准的推荐哦~
        </div>
        
        <div v-for="(animal, index) in recommendedAnimals" :key="'rec-' + index" class="animal-card" @click="viewAnimalDetail(animal)">
          <div class="animal-image">
            <img :src="animal.imageUrl || '/uploads/animal/default.jpg'" :alt="animal.name">
            <div class="animal-status recommend-badge">推荐</div>
          </div>
          <div class="animal-info">
            <h3 class="animal-name">{{ animal.name }}</h3>
            <div class="animal-meta">
              <el-tag size="small">{{ animal.params?.categoryName || '其他' }}</el-tag>
              <span class="animal-gender">
                <el-icon :color="animal.gender === 0 ? '#409EFF' : '#FF66AA'">
                  <Male v-if="animal.gender === 0" />
                  <Female v-else />
                </el-icon>
                {{ animal.gender === 0 ? '公' : '母' }}
              </span>
            </div>
            <div class="animal-features">
              <p class="animal-age" v-if="animal.age">{{ formatAge(animal.age) }}</p>
              <p class="animal-weight" v-if="animal.weight">{{ animal.weight }}kg</p>
            </div>
            <p class="animal-description">{{ truncateContent(animal.features || '可爱、温顺') }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="features">
      <div class="feature-card" @click="$router.push('/user/animal')">
        <el-icon><Notebook /></el-icon>
        <h3>流浪动物</h3>
        <p>浏览流浪动物信息，选择您喜爱的小伙伴</p>
      </div>
      <div class="feature-card" @click="$router.push('/user/rescue')">
        <el-icon><DiscoveryFilled /></el-icon>
        <h3>动物救助</h3>
        <p>为需要帮助的流浪动物发布救助信息</p>
      </div>
      <div class="feature-card" @click="$router.push('/user/knowledge')">
        <el-icon><Reading /></el-icon>
        <h3>养护知识</h3>
        <p>了解宠物饲养、健康护理等相关知识</p>
      </div>
      <div class="feature-card" @click="$router.push('/user/forum')">
        <el-icon><ChatLineRound /></el-icon>
        <h3>社区论坛</h3>
        <p>分享您的养宠经验，与爱心人士交流互动</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessageBox } from 'element-plus'
import { getActiveCarousels } from '@/api/carousel'
import { getHotAnimals, getPersonalizedRecommendations } from '@/api/recommendation'
import { Male, Female, Notebook, DiscoveryFilled, Reading, ChatLineRound, View, Star, Collection } from '@element-plus/icons-vue'

const router = useRouter()
const announcements = ref([])
const announcementLoading = ref(false)
const carousels = ref([])
const carouselLoading = ref(false)
const hotAnimals = ref([])
const hotAnimalsLoading = ref(false)
const recommendedAnimals = ref([])
const recommendedAnimalsLoading = ref(false)

// 检查是否登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

// 获取轮播图列表
const getCarousels = async () => {
  carouselLoading.value = true
  try {
    const res = await getActiveCarousels()
    if (res.code === 200 && res.data) {
      carousels.value = res.data
    }
  } catch (error) {
    console.error('获取轮播图失败', error)
  } finally {
    carouselLoading.value = false
  }
}

// 获取公告列表
const getAnnouncements = async () => {
  announcementLoading.value = true
  try {
    const res = await request.get('/announcement/list', {
      params: {
        limit: 5 // 只获取最新的5条公告
      }
    })
    announcements.value = res.data || []
  } catch (error) {
    console.error('获取公告列表失败', error)
  } finally {
    announcementLoading.value = false
  }
}

// 获取热门动物
const getHotAnimalsList = async () => {
  hotAnimalsLoading.value = true
  try {
    const res = await getHotAnimals(8)
    if (res.code === 200 && res.data) {
      hotAnimals.value = res.data || []
    }
  } catch (error) {
    console.error('获取热门动物失败', error)
  } finally {
    hotAnimalsLoading.value = false
  }
}

// 获取个性化推荐
const getRecommendedAnimals = async () => {
  if (!isLoggedIn.value) {
    return
  }
  
  recommendedAnimalsLoading.value = true
  try {
    const res = await getPersonalizedRecommendations(8)
    if (res.code === 200 && res.data) {
      recommendedAnimals.value = res.data || []
    }
  } catch (error) {
    console.error('获取推荐动物失败', error)
  } finally {
    recommendedAnimalsLoading.value = false
  }
}

// 查看动物详情
const viewAnimalDetail = (animal) => {
  router.push(`/user/animal/detail/${animal.id}`)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 截断内容
const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 60 ? content.substring(0, 60) + '...' : content
}

// 跳转到领养页面
const goToAdoption = () => {
  router.push('/user/animal')
}

// 跳转到动物列表
const goToAnimalList = () => {
  router.push('/user/animal')
}

// 跳转到公告列表
const goToAnnouncementList = () => {
  router.push('/user/announcement')
}

// 打开公告详情
const openAnnouncementDetail = (announcement) => {
  // 将文本中的换行符转换为HTML的<br>标签
  const formattedContent = announcement.content.replace(/\n/g, '<br>');
  
  ElMessageBox.alert(formattedContent, announcement.title, {
    dangerouslyUseHTMLString: true,
    closeOnClickModal: false,
    showClose: false,
    confirmButtonText: '关闭'
  })
}

// 格式化年龄
const formatAge = (ageInMonths) => {
  if (ageInMonths < 12) {
    return `${ageInMonths}个月`
  } else {
    const years = Math.floor(ageInMonths / 12)
    const months = ageInMonths % 12
    return months > 0 ? `${years}岁${months}个月` : `${years}岁`
  }
}

onMounted(() => {
  getCarousels()
  getAnnouncements()
  getHotAnimalsList()
  getRecommendedAnimals()
})
</script>

<style scoped>
.home-container {
  width: 100%;
}

.carousel-section {
  margin-bottom: 40px;
  width: 100%;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.carousel-section :deep(.el-carousel__indicators) {
  bottom: 20px;
}

.carousel-section :deep(.el-carousel__indicator) {
  padding: 0 6px;
}

.carousel-section :deep(.el-carousel__button) {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.7);
}

.carousel-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
}

.welcome-section {
  text-align: center;
  margin: 40px 0;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.welcome-section h1 {
  color: #409EFF;
  margin-bottom: 15px;
}

.welcome-section p {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

.info-section {
  margin: 40px 0;
}

.platform-intro, .announcement-board {
  height: 100%;
}

.intro-header, .board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.intro-content {
  color: #666;
  line-height: 1.6;
}

.intro-content p {
  margin-bottom: 15px;
}

.intro-content ul {
  padding-left: 20px;
  margin-bottom: 15px;
}

.intro-content li {
  margin-bottom: 5px;
}

.announcement-item {
  cursor: pointer;
  padding: 5px 0;
}

.announcement-item h3 {
  font-size: 16px;
  margin: 0 0 5px 0;
  color: #333;
}

.announcement-summary {
  color: #666;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

.empty-tip {
  text-align: center;
  padding: 20px 0;
  color: #999;
}

.animal-section {
  margin: 40px 0;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.animal-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.animal-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.animal-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.animal-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.animal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.animal-card:hover .animal-image img {
  transform: scale(1.05);
}

.animal-status {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #67c23a;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.hot-badge {
  background: linear-gradient(135deg, #ff6b6b, #ff8e53);
}

.recommend-badge {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.animal-info {
  padding: 15px;
}

.animal-name {
  font-size: 18px;
  margin: 0 0 10px;
  color: #333;
}

.animal-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.animal-gender {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 14px;
}

.animal-features {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.animal-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
  color: #909399;
  font-size: 13px;
}

.animal-stats span {
  display: flex;
  align-items: center;
  gap: 3px;
}

.animal-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  height: 60px;
  overflow: hidden;
}

@media (max-width: 1200px) {
  .animal-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .animal-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .animal-grid {
    grid-template-columns: 1fr;
  }
}

.features {
  margin: 40px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feature-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  padding: 20px;
  text-align: center;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.feature-card h3 {
  font-size: 18px;
  margin: 10px 0;
  color: #333;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
</style> 