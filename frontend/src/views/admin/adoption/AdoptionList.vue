<template>
  <div class="adoption-list-container">
    <div class="filter-container">
      <el-select v-model="queryParams.status" placeholder="申请状态" clearable style="width: 200px">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" @click="getList">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="adoptionList" border style="width: 100%">
      <el-table-column label="申请ID" prop="id" width="80" />
      <el-table-column label="动物" width="180">
        <template #default="scope">
          <div class="animal-info">
            <el-image 
              v-if="scope.row.animalImage" 
              :src="scope.row.animalImage" 
              style="width: 50px; height: 50px; border-radius: 4px"
              fit="cover"
              @click="handlePreviewImage(scope.row.animalImage)"
            />
            <div class="animal-text">
              <div>{{ scope.row.animalName }}</div>
              <div class="animal-category">{{ scope.row.animalCategory }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请用户" width="180">
        <template #default="scope">
          <div>{{ scope.row.userName }}</div>
          <div v-if="scope.row.userPhone" class="user-info">电话: {{ scope.row.userPhone }}</div>
          <div v-if="scope.row.userEmail" class="user-info">邮箱: {{ scope.row.userEmail }}</div>
        </template>
      </el-table-column>
      <el-table-column label="申请理由" prop="reason" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" width="180" prop="createTime" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <div class="operation-buttons">
            <el-button 
              v-if="scope.row.status === 0" 
              type="success" 
              size="small" 
              @click="handleAudit(scope.row, 1)"
            >
              通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              type="danger" 
              size="small" 
              @click="handleAudit(scope.row, 2)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              type="primary" 
              size="small" 
              @click="handleAudit(scope.row, 3)"
            >
              已联系
            </el-button>
            <el-button 
              size="small" 
              @click="handleAddRemark(scope.row)"
            >
              备注
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      :current-page="queryParams.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 审核弹窗 -->
    <el-dialog
      :title="getAuditTitle(auditForm.status)"
      v-model="auditDialogVisible"
      width="500px"
    >
      <el-form ref="auditFormRef" :model="auditForm" label-width="100px">
        <el-form-item label="申请ID" prop="id">
          <el-input v-model="auditForm.id" disabled />
        </el-form-item>
        <el-form-item label="动物" prop="animalName">
          <el-input v-model="auditForm.animalName" disabled />
        </el-form-item>
        <el-form-item label="申请人" prop="userName">
          <el-input v-model="auditForm.userName" disabled />
        </el-form-item>
        <el-form-item label="备注说明" prop="remark">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="申请详情" v-model="detailDialogVisible" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申请ID">{{ detailInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="动物名称">{{ detailInfo.animalName }}</el-descriptions-item>
        <el-descriptions-item label="动物类别">{{ detailInfo.animalCategory }}</el-descriptions-item>
        <el-descriptions-item label="用户姓名">{{ detailInfo.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailInfo.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱">{{ detailInfo.userEmail }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ detailInfo.userAddress }}</el-descriptions-item>
        <el-descriptions-item label="申请理由">{{ detailInfo.reason }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusType(detailInfo.status)">
            {{ getStatusText(detailInfo.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注说明">{{ detailInfo.remark }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailInfo.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加备注弹窗 -->
    <el-dialog
      title="添加/修改备注"
      v-model="remarkDialogVisible"
      width="500px"
    >
      <el-form ref="remarkFormRef" :model="remarkForm" label-width="100px">
        <el-form-item label="申请ID" prop="id">
          <el-input v-model="remarkForm.id" disabled />
        </el-form-item>
        <el-form-item label="动物" prop="animalName">
          <el-input v-model="remarkForm.animalName" disabled />
        </el-form-item>
        <el-form-item label="申请人" prop="userName">
          <el-input v-model="remarkForm.userName" disabled />
        </el-form-item>
        <el-form-item label="当前备注">
          <el-input
            v-model="remarkForm.currentRemark"
            type="textarea"
            rows="2"
            disabled
            placeholder="暂无备注"
          />
        </el-form-item>
        <el-form-item label="新增备注" prop="remark">
          <el-input
            v-model="remarkForm.remark"
            type="textarea"
            rows="4"
            placeholder="请输入备注信息"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="remarkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRemark">确定</el-button>
        </div>
      </template>
    </el-dialog>

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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const adoptionList = ref([])
const total = ref(0)
const auditDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const remarkDialogVisible = ref(false)
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null
})

// 审核表单
const auditForm = reactive({
  id: null,
  animalName: '',
  userName: '',
  status: null,
  remark: ''
})

// 备注表单
const remarkForm = reactive({
  id: null,
  animalName: '',
  userName: '',
  currentRemark: '',
  remark: ''
})

// 详情信息
const detailInfo = reactive({
  id: null,
  userId: null,
  userName: '',
  userPhone: '',
  userEmail: '',
  userAddress: '',
  animalId: null,
  animalName: '',
  animalCategory: '',
  animalImage: '',
  reason: '',
  status: null,
  remark: '',
  createTime: ''
})

// 状态选项
const statusOptions = [
  { value: 0, label: '待审核' },
  { value: 1, label: '已通过' },
  { value: 2, label: '已拒绝' },
  { value: 3, label: '已联系' },
]

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

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/adoption/page', {
      params: {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        status: queryParams.status
      }
    })
    
    if (res.code === 200) {
      adoptionList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '获取领养申请列表失败')
    }
  } catch (error) {
    console.error('获取领养申请列表失败', error)
    ElMessage.error('获取领养申请列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询条件
const resetQuery = () => {
  queryParams.status = null
  getList()
}

// 获取审核标题
const getAuditTitle = (status) => {
  switch (status) {
    case 1: return '通过申请'
    case 2: return '拒绝申请'
    case 3: return '标记为已联系'
    default: return '审核申请'
  }
}

// 处理审核操作
const handleAudit = (row, status) => {
  auditForm.id = row.id
  auditForm.animalName = row.animalName
  auditForm.userName = row.userName
  auditForm.status = status
  auditForm.remark = ''
  auditDialogVisible.value = true
}

// 处理添加备注
const handleAddRemark = (row) => {
  remarkForm.id = row.id
  remarkForm.animalName = row.animalName
  remarkForm.userName = row.userName
  remarkForm.currentRemark = row.remark || ''
  remarkForm.remark = ''
  remarkDialogVisible.value = true
}

// 提交备注
const submitRemark = async () => {
  if (!remarkForm.remark.trim()) {
    ElMessage.warning('请输入备注信息')
    return
  }
  
  try {
    const res = await request.post('/admin/adoption/remark', {
      id: remarkForm.id,
      remark: remarkForm.remark
    })
    
    if (res.code === 200) {
      ElMessage.success('备注添加成功')
      remarkDialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.msg || '备注添加失败')
    }
  } catch (error) {
    console.error('备注添加失败', error)
    ElMessage.error('备注添加失败')
  }
}

// 提交审核
const submitAudit = async () => {
  try {
    const res = await request.post('/admin/adoption/audit', {
      id: auditForm.id,
      status: auditForm.status,
      remark: auditForm.remark
    })
    
    if (res.code === 200) {
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (error) {
    console.error('审核失败', error)
    ElMessage.error('审核失败')
  }
}

// 图片预览
const handlePreviewImage = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 查看详情
const handleDetail = (row) => {
  // 从当前行数据中获取详情信息，确保状态一致
  Object.assign(detailInfo, {
    id: row.id,
    userId: row.userId,
    userName: row.userName,
    userPhone: row.userPhone || '未设置',
    userEmail: row.userEmail || '未设置',
    userAddress: row.userAddress || '未设置',
    animalId: row.animalId,
    animalName: row.animalName,
    animalCategory: row.animalCategory,
    animalImage: row.animalImage,
    reason: row.reason,
    status: row.status,
    remark: row.remark,
    createTime: row.createTime
  });

  // 如果联系电话、邮箱或地址为空，尝试获取用户详情信息
  if (!detailInfo.userPhone || !detailInfo.userEmail || !detailInfo.userAddress) {
    request.get(`/admin/user/${detailInfo.userId}`).then(userRes => {
      if (userRes.code === 200 && userRes.data) {
        detailInfo.userPhone = userRes.data.phone || '未设置';
        detailInfo.userEmail = userRes.data.email || '未设置';
        detailInfo.userAddress = userRes.data.address || '未设置';
      }
    }).catch(error => {
      console.error('获取用户信息失败', error);
    });
  }
  
  detailDialogVisible.value = true;
}

// 分页处理
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

const handleCurrentChange = (current) => {
  queryParams.pageNum = current
  getList()
}

// 在组件卸载前移除ResizeObserver监听器
const ResizeObserverMap = new Map()
const originalResizeObserver = window.ResizeObserver

onMounted(() => {
  getList()
  
  // 避免ResizeObserver错误
  window.ResizeObserver = class ResizeObserver extends originalResizeObserver {
    constructor(callback) {
      super((entries, observer) => {
        requestAnimationFrame(() => {
          if (!Array.isArray(entries)) {
            return
          }
          
          // 在下一帧执行回调
          callback(entries, observer)
        })
      })
      
      ResizeObserverMap.set(this, true)
    }
    
    disconnect() {
      super.disconnect()
      ResizeObserverMap.delete(this)
    }
  }
})

// 卸载时恢复原始ResizeObserver
onUnmounted(() => {
  window.ResizeObserver = originalResizeObserver
  ResizeObserverMap.clear()
})
</script>

<style scoped>
.adoption-list-container {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.animal-info {
  display: flex;
  align-items: center;
}

.animal-text {
  margin-left: 10px;
}

.animal-category {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.user-info {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}

.operation-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.operation-buttons .el-button {
  margin-left: 0;
  margin-right: 0;
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
.el-dialog__wrapper {
  backdrop-filter: blur(5px);
}

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