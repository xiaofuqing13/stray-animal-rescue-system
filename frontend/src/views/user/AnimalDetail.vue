<template>
  <div class="animal-detail">
    <div class="container" v-loading="loading">
      <div v-if="animal" class="animal-container">
        <div class="detail-header">
          <el-button @click="goBack" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
        
        <div class="detail-content">
          <div class="animal-image-area">
            <div class="image-container">
              <img :src="animal.imageUrl || DEFAULT_IMAGE_PLACEHOLDER" :alt="animal.name" class="animal-image">
              <div class="animal-status">待领养</div>
            </div>
            
            <!-- 互动按钮 -->
            <div class="interaction-buttons">
              <el-button 
                :type="interactionStatus.isLiked ? 'danger' : 'default'"
                :icon="interactionStatus.isLiked ? 'StarFilled' : 'Star'"
                @click="toggleLike"
                :loading="likeLoading"
              >
                {{ interactionStatus.isLiked ? '已点赞' : '点赞' }} ({{ animal.likeCount || 0 }})
              </el-button>
              <el-button 
                :type="interactionStatus.isFavorited ? 'warning' : 'default'"
                :icon="interactionStatus.isFavorited ? 'CollectionTag' : 'Collection'"
                @click="toggleFavorite"
                :loading="favoriteLoading"
              >
                {{ interactionStatus.isFavorited ? '已收藏' : '收藏' }} ({{ animal.favoriteCount || 0 }})
              </el-button>
            </div>
            
            <div class="animal-stats-detail">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ animal.viewCount || 0 }} 次浏览</span>
              </div>
            </div>
            
            <div class="adoption-action">
              <el-button type="success" size="large" @click="showAdoptionForm">申请领养</el-button>
            </div>
          </div>
          
          <div class="animal-info-area">
            <h1 class="animal-name">{{ animal.name }}</h1>
            
            <div class="info-tags">
              <el-tag size="large">{{ animal.categoryName }}</el-tag>
              <el-tag 
                size="large"
                :type="animal.gender === 0 ? 'info' : 'danger'"
                effect="plain"
              >
                {{ animal.gender === 0 ? '公' : '母' }}
              </el-tag>
            </div>
            
            <div class="info-item">
              <span class="label">品种：</span>
              <span class="value">{{ animal.breed || '未知' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{ animal.age ? formatAge(animal.age) : '未知' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">体重：</span>
              <span class="value">{{ animal.weight ? `${animal.weight}kg` : '未知' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">健康状况：</span>
              <span class="value">{{ animal.healthStatus || '良好' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">特征：</span>
              <span class="value">{{ animal.features || '无特殊特征' }}</span>
            </div>
            
            <div class="info-item">
              <span class="label">救助时间：</span>
              <span class="value">{{ formatDate(animal.rescueTime) }}</span>
            </div>
            
            <div class="description-section">
              <h3>详细描述</h3>
              <p>{{ animal.description || '暂无详细描述' }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 评论区 -->
      <div class="comments-container" v-if="animal">
        <h2 class="comments-title">
          <el-icon><ChatLineRound /></el-icon>
          评论区 ({{ animal.commentCount || 0 }})
        </h2>
        
        <!-- 发表评论 -->
        <div class="comment-form" v-if="isLoggedIn">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="请输入你的评论..."
            maxlength="500"
            show-word-limit
          />
          <div class="form-actions">
            <el-button 
              type="primary" 
              @click="submitComment"
              :loading="commentSubmitting"
              :disabled="!newComment.trim()"
            >
              发表评论
            </el-button>
          </div>
        </div>
        
        <div class="login-tip" v-else>
          <el-alert
            title="请登录后发表评论"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
        
        <!-- 评论列表 -->
        <div class="comments-list" v-loading="commentLoading">
          <el-empty v-if="comments.length === 0 && !commentLoading" description="暂无评论，快来抢沙发吧！" />
          
          <div v-else>
            <div 
              v-for="comment in comments" 
              :key="comment.id" 
              class="comment-item"
            >
              <div class="comment-user">
                <el-avatar :size="40" :src="getAvatarUrl(comment.avatar)" icon="UserFilled">
                  {{ !comment.avatar ? (comment.username?.charAt(0) || 'U') : '' }}
                </el-avatar>
                <div class="user-info">
                  <div class="username">{{ comment.username || '匿名用户' }}</div>
                  <div class="comment-time">{{ formatRelativeTime(comment.createTime) }}</div>
                </div>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-actions">
                <el-button type="text" v-if="isLoggedIn && comment.userId === currentUserId" @click="handleDeleteComment(comment.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else-if="!loading" description="动物信息不存在或已被领养">
        <el-button type="primary" @click="goBack">返回列表</el-button>
      </el-empty>
    </div>
    
    <!-- 领养申请表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="提交领养申请"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="adoptionFormRef"
        :model="adoptionForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="您的姓名" prop="name">
          <el-input v-model="adoptionForm.name" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="adoptionForm.phone" />
        </el-form-item>
        
        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="adoptionForm.reason"
            type="textarea"
            :rows="6"
            placeholder="请详细说明您的领养意向、饲养条件、个人情况等信息，以便工作人员评估您是否适合领养该动物。"
          />
        </el-form-item>
      </el-form>
      
      <div class="adoption-notice">
        <p><strong>领养须知：</strong></p>
        <p>1. 提交申请后，工作人员将在1-3个工作日内对您的申请进行审核。</p>
        <p>2. 审核通过后，您需要前往救助站与动物进行实地接触，确保双方适应。</p>
        <p>3. 领养成功后，您需要承诺善待动物，定期反馈动物生活状况。</p>
        <p>4. 本系统支持一键查询您的领养申请状态，请关注"我的领养"页面。</p>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAdoption" :loading="submitting">提交申请</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, View, ChatLineRound } from '@element-plus/icons-vue'
import { getAnimalDetail, submitAdoptionApplication } from '@/api/animal'
import { getUserInfo } from '@/api/user'
import { formatDate } from '@/utils/date'
import { getAvatarUrl as resolveAvatar, DEFAULT_IMAGE_PLACEHOLDER } from '@/utils/index'
import { getAnimalCategories } from '@/api/animal'
import { 
  likeAnimal, 
  unlikeAnimal, 
  favoriteAnimal, 
  unfavoriteAnimal,
  getInteractionStatus 
} from '@/api/recommendation'
import { 
  getAnimalComments, 
  addAnimalComment, 
  deleteAnimalComment 
} from '@/api/comment'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const likeLoading = ref(false)
const favoriteLoading = ref(false)
const animal = ref(null)
const dialogVisible = ref(false)
const interactionStatus = reactive({
  isLiked: false,
  isFavorited: false
})
const adoptionForm = reactive({
  name: '',
  phone: '',
  reason: '',
  userId: ''
})

// 评论相关
const comments = ref([])
const commentLoading = ref(false)
const newComment = ref('')
const commentSubmitting = ref(false)
const isLoggedIn = ref(false)
const currentUserId = ref(null)

const rules = {
  name: [
    { required: true, message: '请填写您的姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请填写联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请填写申请理由', trigger: 'blur' },
    { min: 10, max: 500, message: '申请理由应在10-500个字符之间', trigger: 'blur' }
  ]
}

const adoptionFormRef = ref(null)

// 初始化
onMounted(() => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  
  if (token) {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        currentUserId.value = user.id
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }
  
  const animalId = route.params.id
  if (animalId) {
    fetchAnimalDetail(animalId)
  } else {
    loading.value = false
    ElMessage.error('动物ID不能为空')
  }
})

// 获取动物详情
const fetchAnimalDetail = async (id) => {
  loading.value = true
  try {
    const res = await getAnimalDetail(id)
    if (res.code === 200) {
      animal.value = res.data
      
      // 检查动物状态是否为待领养
      if (animal.value.status !== 0) {
        ElMessage.warning('该动物不是待领养状态，无法提交领养申请')
      }
      
      // 确保categoryName存在，如果后端没有返回，则尝试使用breed替代
      if (!animal.value.categoryName && animal.value.categoryId) {
        // 尝试加载分类信息
        try {
          const categoryRes = await getAnimalCategories()
          if (categoryRes.code === 200) {
            const category = categoryRes.data.find(c => c.id === animal.value.categoryId)
            if (category) {
              animal.value.categoryName = category.name
            }
          }
        } catch (error) {
          console.error('获取动物分类信息失败:', error)
        }
      }
      
      // 如果仍然没有categoryName，则使用breed或默认值
      if (!animal.value.categoryName) {
        animal.value.categoryName = animal.value.breed || '未知分类'
      }
      
      // 获取互动状态
      await fetchInteractionStatus(id)
      
      // 获取评论列表
      await fetchComments()
    } else {
      ElMessage.error(res.msg || '获取动物详情失败')
    }
  } catch (error) {
    console.error('获取动物详情失败:', error)
    ElMessage.error('获取动物详情失败')
  } finally {
    loading.value = false
  }
}

// 获取互动状态
const fetchInteractionStatus = async (animalId) => {
  try {
    const res = await getInteractionStatus(animalId)
    if (res.code === 200) {
      interactionStatus.isLiked = res.data.isLiked
      interactionStatus.isFavorited = res.data.isFavorited
    }
  } catch (error) {
    console.error('获取互动状态失败:', error)
  }
}

// 切换点赞
const toggleLike = async () => {
  likeLoading.value = true
  try {
    if (interactionStatus.isLiked) {
      await unlikeAnimal(animal.value.id)
      interactionStatus.isLiked = false
      animal.value.likeCount = Math.max(0, (animal.value.likeCount || 0) - 1)
      ElMessage.success('已取消点赞')
    } else {
      await likeAnimal(animal.value.id)
      interactionStatus.isLiked = true
      animal.value.likeCount = (animal.value.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    likeLoading.value = false
  }
}

// 切换收藏
const toggleFavorite = async () => {
  favoriteLoading.value = true
  try {
    if (interactionStatus.isFavorited) {
      await unfavoriteAnimal(animal.value.id)
      interactionStatus.isFavorited = false
      animal.value.favoriteCount = Math.max(0, (animal.value.favoriteCount || 0) - 1)
      ElMessage.success('已取消收藏')
    } else {
      await favoriteAnimal(animal.value.id)
      interactionStatus.isFavorited = true
      animal.value.favoriteCount = (animal.value.favoriteCount || 0) + 1
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    favoriteLoading.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  commentLoading.value = true
  try {
    const res = await getAnimalComments(route.params.id)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    commentLoading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  commentSubmitting.value = true
  try {
    await addAnimalComment({
      animalId: route.params.id,
      content: newComment.value.trim(),
      parentId: null
    })
    
    ElMessage.success('评论成功')
    newComment.value = ''
    
    // 刷新评论列表
    await fetchComments()
    
    // 更新动物评论数
    if (animal.value) {
      animal.value.commentCount = (animal.value.commentCount || 0) + 1
    }
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败，请重试')
  } finally {
    commentSubmitting.value = false
  }
}

// 删除评论
const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAnimalComment(commentId)
    ElMessage.success('删除成功')
    
    // 刷新评论列表
    await fetchComments()
    
    // 更新动物评论数
    if (animal.value && animal.value.commentCount > 0) {
      animal.value.commentCount = animal.value.commentCount - 1
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 格式化相对时间
const formatRelativeTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  // 不到1分钟
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  // 不到1小时
  if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`
  }
  // 不到1天
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  }
  // 不到1周
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }
  
  // 超过1周显示完整日期
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 获取头像URL（avatar 为空时返回 null，让 el-avatar 显示默认图标）
const getAvatarUrl = (avatar) => resolveAvatar(avatar, false)

// 返回列表
const goBack = () => {
  router.push('/user/animal')
}

// 显示领养表单
const showAdoptionForm = async () => {
  // 检查动物状态
  if (animal.value.status !== 0) {
    ElMessage.warning('该动物不是待领养状态，无法提交领养申请')
    return
  }
  
  // 获取用户信息填充表单
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      const userInfo = res.data
      adoptionForm.name = userInfo.name
      adoptionForm.phone = userInfo.phone
      adoptionForm.userId = userInfo.id
      dialogVisible.value = true
    } else {
      ElMessage.error('获取用户信息失败，请重新登录')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败，请重新登录')
  }
}

// 提交领养申请
const submitAdoption = async () => {
  // 表单验证
  if (!adoptionFormRef.value) return
  
  await adoptionFormRef.value.validate(async (valid) => {
    if (!valid) {
      return ElMessage.warning('请完善表单信息')
    }
    
    submitting.value = true
    try {
      const data = {
        animalId: animal.value.id,
        userId: adoptionForm.userId,
        name: adoptionForm.name,
        phone: adoptionForm.phone,
        reason: adoptionForm.reason
      }
      
      const res = await submitAdoptionApplication(data)
      if (res.code === 200) {
        ElMessageBox.alert(
          '您的领养申请已提交成功！工作人员将在1-3个工作日内进行审核，请关注"我的领养"页面查看申请状态。',
          '申请已提交',
          {
            confirmButtonText: '查看我的领养',
            showCancelButton: true,
            cancelButtonText: '返回列表',
            callback: (action) => {
              if (action === 'confirm') {
                router.push('/user/adoption')
              } else if (action === 'cancel') {
                router.push('/user/animal')
              }
            },
            beforeClose: (action, instance, done) => {
              if (action === 'close') {
                // 用户点击了叉号
                router.push('/user/animal')
              }
              done() // 关闭对话框
            }
          }
        )
        dialogVisible.value = false
      } else {
        ElMessage.error(res.msg || '提交领养申请失败')
      }
    } catch (error) {
      console.error('提交领养申请失败:', error)
      ElMessage.error('提交领养申请失败')
    } finally {
      submitting.value = false
    }
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
</script>

<style scoped>
.animal-detail {
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

.detail-header {
  margin-bottom: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
}

.detail-content {
  display: flex;
  gap: 30px;
}

.animal-image-area {
  flex: 0 0 450px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.image-container {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.animal-image {
  width: 100%;
  border-radius: 8px;
  display: block;
}

.animal-status {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: #67c23a;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

.interaction-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.animal-stats-detail {
  display: flex;
  justify-content: center;
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 14px;
}

.adoption-action {
  display: flex;
  justify-content: center;
}

.animal-info-area {
  flex: 1;
}

.animal-name {
  font-size: 28px;
  margin-bottom: 15px;
  color: #333;
}

.info-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 12px;
  display: flex;
}

.info-item .label {
  width: 100px;
  color: #606266;
  font-weight: 500;
}

.info-item .value {
  flex: 1;
  color: #333;
}

.description-section {
  margin-top: 25px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.description-section h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.description-section p {
  line-height: 1.8;
  color: #333;
  white-space: pre-line;
}

.adoption-notice {
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
  margin-top: 20px;
  font-size: 14px;
}

.adoption-notice p {
  margin: 5px 0;
  color: #666;
}

.adoption-notice strong {
  color: #409EFF;
}

@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
  }
  
  .animal-image-area {
    flex: none;
    width: 100%;
  }
}

/* 评论区样式 */
.comments-container {
  margin-top: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.comments-title {
  font-size: 20px;
  color: #303133;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
}

.comments-title .el-icon {
  margin-right: 8px;
}

.comment-form {
  margin-bottom: 30px;
}

.form-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.login-tip {
  margin-bottom: 20px;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  margin-left: 10px;
}

.username {
  font-weight: bold;
  color: #303133;
  font-size: 14px;
}

.comment-time {
  color: #909399;
  font-size: 12px;
  margin-top: 3px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin: 5px 0;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  margin-top: 5px;
}

@media screen and (max-width: 768px) {
  .comments-container {
    padding: 15px;
  }
  
  .comments-title {
    font-size: 18px;
  }
}
</style>

@media screen and (max-width: 768px) {
  .comments-container {
    padding: 15px;
  }
  
  .comments-title {
    font-size: 18px;
  }
}
