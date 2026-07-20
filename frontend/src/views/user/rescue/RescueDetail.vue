<template>
  <div class="rescue-detail-container">
    <div class="back-link">
      <el-button type="text" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </el-button>
    </div>

    <div v-loading="loading">
      <el-empty v-if="!rescueInfo.id && !loading" description="救助信息不存在或已被删除" />
      
      <template v-else>
        <div class="rescue-header">
          <h1>{{ rescueInfo.title }}</h1>
          <el-tag :type="getStatusType(rescueInfo.status)" class="status-tag">
            {{ getStatusLabel(rescueInfo.status) }}
          </el-tag>
        </div>
        
        <div class="rescue-meta">
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            发布时间：{{ formatDate(rescueInfo.createTime) }}
          </div>
        </div>
        
        <div class="rescue-content">
          <div class="info-section">
            <h2>基本信息</h2>
            <div class="info-grid">
              <div class="info-item">
                <div class="info-label">联系人</div>
                <div class="info-value">{{ rescueInfo.contactName }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">联系电话</div>
                <div class="info-value call-phone" @click="handleCall">
                  <el-icon><Phone /></el-icon>
                  {{ rescueInfo.contactPhone }}
                </div>
              </div>
              <div class="info-item full-width">
                <div class="info-label">位置</div>
                <div class="info-value location">
                  <el-icon><Location /></el-icon>
                  {{ rescueInfo.location }}
                  <el-button size="small" type="primary" text @click="openMap">
                    <el-icon><Map /></el-icon>
                    查看地图
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h2>详细描述</h2>
            <div class="detail-content">
              {{ rescueInfo.content }}
            </div>
          </div>
          
          <div class="image-section" v-if="rescueInfo.imageUrl">
            <h2>动物照片</h2>
            <div class="image-wrapper">
              <el-image 
                :src="rescueInfo.imageUrl" 
                :preview-src-list="[rescueInfo.imageUrl]"
                alt="动物照片"
                fit="cover"
              ></el-image>
            </div>
          </div>
        </div>
        
        <div class="action-section">
          <el-button type="success" @click="handleEmergencyCall">
            <el-icon><Phone /></el-icon>
            救助站联系电话
          </el-button>
          <el-button type="warning" @click="handleShare">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
          <el-button 
            v-if="isCurrentUserPublisher" 
            type="primary" 
            @click="handleStatusChange"
          >
            <el-icon><Edit /></el-icon>
            修改救助状态
          </el-button>
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

    <!-- 分享对话框 -->
    <el-dialog
      title="分享救助信息"
      v-model="shareDialogVisible"
      width="400px"
      center
    >
      <div class="share-content">
        <p class="share-tip">请复制以下链接分享给好友：</p>
        <el-input 
          v-model="shareUrl" 
          readonly
          class="share-url"
        >
          <template #append>
            <el-button @click="copyShareUrl">复制</el-button>
          </template>
        </el-input>
      </div>
    </el-dialog>

    <!-- 修改状态对话框 -->
    <el-dialog
      title="修改救助状态"
      v-model="statusDialogVisible"
      width="400px"
      center
    >
      <div class="status-content">
        <p class="status-tip">请选择当前救助信息的状态：</p>
        <el-radio-group v-model="selectedStatus" class="status-options">
          <el-radio :label="0">待救助</el-radio>
          <el-radio :label="1">救助中</el-radio>
          <el-radio :label="2">已解决</el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatusChange" :loading="statusUpdateLoading">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getRescueInfoDetail, updateRescueStatus } from '@/api/rescue'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, Calendar, Phone, Location, Map, Share, Edit
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const rescueId = route.params.id
const rescueInfo = ref({})
const emergencyDialogVisible = ref(false)
const shareDialogVisible = ref(false)
const shareUrl = ref('')
const statusDialogVisible = ref(false)
const selectedStatus = ref(null)
const statusUpdateLoading = ref(false)

// 判断当前用户是否为发布者
const isCurrentUserPublisher = computed(() => {
  // 获取当前登录用户信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  // 判断是否为发布者
  return userInfo.id && rescueInfo.value && String(rescueInfo.value.userId) === String(userInfo.id)
})

// 加载救助信息详情
const loadRescueDetail = async () => {
  if (!rescueId) {
    ElMessage.error('救助信息ID不能为空')
    return
  }
  
  loading.value = true
  try {
    const res = await getRescueInfoDetail(rescueId)
    if (res.code === 200) {
      rescueInfo.value = res.data
    } else {
      ElMessage.error(res.msg || '获取救助信息失败')
    }
  } catch (error) {
    console.error('获取救助信息详情失败:', error)
    ElMessage.error('获取救助信息详情失败')
  } finally {
    loading.value = false
  }
}

// 处理拨打电话
const handleCall = () => {
  const phone = rescueInfo.value.contactPhone
  if (phone) {
    window.location.href = `tel:${phone}`
  }
}

// 打开地图
const openMap = () => {
  const location = encodeURIComponent(rescueInfo.value.location)
  window.open(`https://map.baidu.com/search/${location}`, '_blank')
}

// 紧急救助电话
const handleEmergencyCall = () => {
  emergencyDialogVisible.value = true
}

// 分享
const handleShare = () => {
  shareUrl.value = window.location.href
  shareDialogVisible.value = true
}

// 复制分享链接
const copyShareUrl = () => {
  navigator.clipboard.writeText(shareUrl.value).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

// 返回列表
const goBack = () => {
  router.push('/user/rescue')
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

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'primary'
    case 2:
      return 'success'
    default:
      return 'info'
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 修改救助状态
const handleStatusChange = () => {
  statusDialogVisible.value = true
  selectedStatus.value = rescueInfo.value.status
}

// 提交状态修改
const submitStatusChange = async () => {
  if (!selectedStatus.value) {
    ElMessage.error('请选择新的救助状态')
    return
  }

  statusUpdateLoading.value = true
  try {
    const res = await updateRescueStatus(rescueId, selectedStatus.value)
    if (res.code === 200) {
      ElMessage.success('救助状态修改成功')
      rescueInfo.value.status = selectedStatus.value
    } else {
      ElMessage.error(res.msg || '修改救助状态失败')
    }
  } catch (error) {
    console.error('修改救助状态失败:', error)
    ElMessage.error('修改救助状态失败')
  } finally {
    statusDialogVisible.value = false
    statusUpdateLoading.value = false
  }
}

onMounted(() => {
  loadRescueDetail()
})
</script>

<style scoped>
.rescue-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.back-link {
  margin-bottom: 20px;
}

.rescue-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.rescue-header h1 {
  margin: 0;
  margin-right: 15px;
  font-size: 24px;
  color: #303133;
}

.status-tag {
  font-size: 14px;
  height: 28px;
  line-height: 26px;
}

.rescue-meta {
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #909399;
  font-size: 14px;
}

.rescue-content {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.info-section, .detail-section, .image-section {
  margin-bottom: 30px;
}

.info-section h2, .detail-section h2, .image-section h2 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.full-width {
  grid-column: span 2;
}

.info-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.info-value {
  color: #303133;
  font-size: 16px;
}

.call-phone {
  color: #409EFF;
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.location {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-content {
  color: #606266;
  line-height: 1.8;
  white-space: pre-line;
}

.image-wrapper {
  width: 100%;
  text-align: center;
}

.image-wrapper img {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.action-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
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

.share-content {
  padding: 10px 0;
}

.share-tip {
  margin-bottom: 15px;
  color: #606266;
}

.share-url {
  margin-bottom: 20px;
}

.status-content {
  padding: 10px 0;
}

.status-tip {
  margin-bottom: 15px;
  color: #606266;
}

.status-options {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .full-width {
    grid-column: span 1;
  }
  
  .rescue-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .rescue-header h1 {
    margin-bottom: 10px;
  }
  
  .action-section {
    flex-direction: column;
  }
}
</style> 