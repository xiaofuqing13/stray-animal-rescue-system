<template>
  <div class="user-adoption-container">
    <h2 class="page-title">我的领养申请</h2>
    
    <el-card v-loading="loading" shadow="never" class="adoption-card">
      <template v-if="adoptionList.length > 0">
        <div v-for="item in adoptionList" :key="item.id" class="adoption-item">
          <div class="adoption-item-header">
            <div class="animal-info">
              <el-image 
                v-if="item.animalImage" 
                :src="item.animalImage" 
                class="animal-image"
                fit="cover"
                @click="handlePreviewImage(item.animalImage)"
              />
              <div class="animal-text">
                <div class="animal-name">{{ item.animalName }}</div>
                <div class="animal-category">{{ item.animalCategory }}</div>
              </div>
            </div>
            <div class="adoption-status">
              <el-tag :type="getStatusType(item.status)">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="adoption-item-body">
            <div class="adoption-reason">
              <h4>申请理由：</h4>
              <p>{{ item.reason }}</p>
            </div>
            <div class="adoption-meta">
              <p><span class="meta-label">申请时间：</span>{{ item.createTime }}</p>
              <p v-if="item.status !== 0">
                <span class="meta-label">审核备注：</span>
                {{ item.remark || '无' }}
              </p>
            </div>
          </div>
        </div>
      </template>
      
      <el-empty 
        v-else 
        description="您还没有提交过领养申请" 
        :image-size="200"
      >
        <el-button type="primary" @click="goToAnimalList">去看看动物</el-button>
      </el-empty>
    </el-card>
    
    <!-- 图片预览对话框 -->
    <el-dialog 
      title="图片预览" 
      v-model="imagePreviewVisible" 
      width="80%" 
      append-to-body 
      destroy-on-close
      custom-class="image-preview-dialog"
    >
      <div class="image-preview-container">
        <el-image 
          :src="previewImageUrl" 
          fit="contain"
          class="image-preview-full"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyAdoptions } from '@/api/animal'

const router = useRouter()
const loading = ref(false)
const adoptionList = ref([])
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

// 初始化
onMounted(() => {
  fetchAdoptionList()
})

// 获取我的领养申请列表
const fetchAdoptionList = async () => {
  loading.value = true
  try {
    const res = await getMyAdoptions()
    if (res.code === 200) {
      adoptionList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取领养申请列表失败')
    }
  } catch (error) {
    console.error('获取领养申请列表失败:', error)
    ElMessage.error('获取领养申请列表失败')
  } finally {
    loading.value = false
  }
}

// 获取状态对应的Tag类型
const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'primary'
    default: return 'info'
  }
}

// 获取状态对应的文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已拒绝'
    case 3: return '已联系'
    default: return '未知'
  }
}

// 图片预览
const handlePreviewImage = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 跳转到动物列表
const goToAnimalList = () => {
  router.push('/user/animal')
}
</script>

<style scoped>
.user-adoption-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.adoption-card {
  margin-bottom: 20px;
}

.adoption-item {
  border-bottom: 1px solid #eee;
  padding: 20px 0;
}

.adoption-item:last-child {
  border-bottom: none;
}

.adoption-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.animal-info {
  display: flex;
  align-items: center;
}

.animal-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
  cursor: pointer;
}

.animal-text {
  margin-left: 15px;
}

.animal-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.animal-category {
  font-size: 14px;
  color: #888;
}

.adoption-reason {
  margin-bottom: 15px;
}

.adoption-reason h4 {
  margin-bottom: 5px;
  color: #555;
}

.adoption-reason p {
  line-height: 1.6;
  color: #666;
}

.adoption-meta {
  font-size: 14px;
  color: #888;
}

.meta-label {
  color: #555;
  font-weight: 500;
}

.image-preview-container {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 20px;
  border-radius: 4px;
  display: flex;
  justify-content: center;
}

.image-preview-full {
  max-width: 100%;
  max-height: calc(90vh - 100px);
  object-fit: contain;
}
</style>

<style>
/* 全局样式 - 预览对话框样式 */
.image-preview-dialog .el-dialog__body {
  padding: 0;
}

.image-preview-dialog .el-dialog {
  margin-top: 15vh !important;
  background-color: transparent;
  box-shadow: none;
}

.image-preview-dialog .el-dialog__header {
  padding-top: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  margin-right: 0;
  border-radius: 8px 8px 0 0;
}
</style> 