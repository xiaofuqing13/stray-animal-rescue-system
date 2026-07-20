<template>
  <div class="rescue-list-container">
    <div class="page-header">
      <h1>流浪动物救助</h1>
      <p class="sub-title">在这里可以发布流浪动物救助信息，也可以查看其他人发布的救助信息</p>
    </div>

    <div class="action-bar">
      <el-button type="primary" @click="handleAddRescue">
        <el-icon><Plus /></el-icon>
        发布救助信息
      </el-button>
      <el-button @click="handleEmergencyCall">
        <el-icon><Phone /></el-icon>
        紧急救助电话
      </el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchParams.keyword"
        placeholder="搜索救助标题、内容或位置"
        clearable
        @clear="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="searchParams.status"
        placeholder="状态筛选"
        clearable
        class="filter-select"
        @change="handleSearch"
      >
        <el-option
          v-for="option in statusOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>

      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>

    <div class="rescue-content" v-loading="loading">
      <div v-if="rescueList.length === 0" class="empty-data">
        <el-empty description="暂无救助信息" />
      </div>
      <template v-else>
        <div class="rescue-grid">
          <div
            v-for="item in rescueList"
            :key="item.id"
            class="rescue-card"
            @click="viewDetail(item.id)"
          >
            <div class="rescue-image">
              <img
                :src="item.imageUrl || '/uploads/rescue/default.jpg'"
                :alt="item.title"
              />
              <div class="rescue-status" :class="getStatusClass(item.status)">
                {{ getStatusLabel(item.status) }}
              </div>
            </div>
            <div class="rescue-info">
              <h3 class="rescue-title">{{ item.title }}</h3>
              <p class="rescue-location">
                <el-icon><Location /></el-icon>
                {{ item.location }}
              </p>
              <p class="rescue-contact">
                <el-icon><User /></el-icon>
                {{ item.contactName }}
                <el-icon style="margin-left: 10px"><Phone /></el-icon>
                {{ item.contactPhone }}
              </p>
              <p class="rescue-description">{{ truncateText(item.content, 100) }}</p>
              <div class="rescue-meta">
                <span class="time">{{ formatDate(item.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[8, 12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>
      </template>
    </div>

    <!-- 紧急救助电话对话框 -->
    <el-dialog
      title="紧急救助电话"
      v-model="emergencyDialogVisible"
      width="400px"
      center
    >
      <div class="emergency-content">
        <div class="phone-item">
          <el-icon size="22" color="#409EFF"><Phone /></el-icon>
          <div class="phone-info">
            <h3>动物救助站</h3>
            <p>123-4567-8900</p>
            <p class="time-hint">工作时间: 8:00-20:00</p>
          </div>
        </div>
        
        <div class="phone-item">
          <el-icon size="22" color="#67C23A"><Phone /></el-icon>
          <div class="phone-info">
            <h3>宠物医院急诊</h3>
            <p>123-4567-8901</p>
            <p class="time-hint">24小时服务</p>
          </div>
        </div>
        
        <div class="phone-item">
          <el-icon size="22" color="#E6A23C"><Phone /></el-icon>
          <div class="phone-info">
            <h3>社区志愿者热线</h3>
            <p>123-4567-8902</p>
            <p class="time-hint">工作时间: 9:00-18:00</p>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="emergencyDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRescueInfoPage, getRescueStatusOptions } from '@/api/rescue'
import { Search, Plus, Location, User, Phone } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const rescueList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const emergencyDialogVisible = ref(false)
const statusOptions = getRescueStatusOptions()

const searchParams = reactive({
  keyword: '',
  status: null
})

// 加载救助信息列表
const loadRescueList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchParams.keyword,
      status: searchParams.status
    }

    const res = await getRescueInfoPage(params)
    if (res.code === 200) {
      rescueList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取救助信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadRescueList()
}

// 页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadRescueList()
}

// 每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadRescueList()
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/user/rescue/detail/${id}`)
}

// 添加救助信息
const handleAddRescue = () => {
  router.push('/user/rescue/form')
}

// 紧急救助电话
const handleEmergencyCall = () => {
  emergencyDialogVisible.value = true
}

// 获取状态标签
const getStatusLabel = (status) => {
  switch (status) {
    case 0:
      return '待救助'
    case 1:
      return '救助中'
    case 2:
      return '已解决'
    default:
      return '未知状态'
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 0:
      return 'status-waiting'
    case 1:
      return 'status-processing'
    case 2:
      return 'status-resolved'
    default:
      return 'status-unknown'
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text
}

onMounted(() => {
  loadRescueList()
})
</script>

<style scoped>
.rescue-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #409EFF;
  margin-bottom: 10px;
}

.sub-title {
  color: #606266;
  font-size: 16px;
}

.action-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 15px;
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
}

.filter-select {
  width: 150px;
}

.rescue-content {
  min-height: 400px;
}

.rescue-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.rescue-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.rescue-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.rescue-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.rescue-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.rescue-card:hover .rescue-image img {
  transform: scale(1.05);
}

.rescue-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.status-waiting {
  background-color: #E6A23C;
}

.status-processing {
  background-color: #409EFF;
}

.status-resolved {
  background-color: #67C23A;
}

.status-unknown {
  background-color: #909399;
}

.rescue-info {
  padding: 15px;
}

.rescue-title {
  font-size: 18px;
  margin: 0 0 10px;
  color: #333;
}

.rescue-location, .rescue-contact {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.rescue-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 10px;
  height: 60px;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #eee;
  padding-top: 10px;
  color: #909399;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.emergency-content {
  padding: 10px 0;
}

.phone-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.phone-item:last-child {
  border-bottom: none;
}

.phone-info {
  margin-left: 15px;
}

.phone-info h3 {
  margin: 0 0 5px;
  font-size: 16px;
  color: #333;
}

.phone-info p {
  margin: 0;
  color: #409EFF;
  font-size: 16px;
  font-weight: bold;
}

.time-hint {
  color: #909399 !important;
  font-size: 12px !important;
  font-weight: normal !important;
  margin-top: 5px !important;
}

.empty-data {
  padding: 50px 0;
}

@media (max-width: 1200px) {
  .rescue-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .rescue-grid {
    grid-template-columns: 1fr;
  }
  
  .search-input,
  .filter-select {
    width: 100%;
  }
  
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
}
</style> 