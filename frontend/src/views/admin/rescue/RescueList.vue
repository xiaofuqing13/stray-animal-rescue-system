<template>
  <div class="rescue-list">
    <div class="header">
      <h2>救助信息管理</h2>
      <el-button type="primary" @click="handleAdd">发布救助信息</el-button>
    </div>
    
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="请输入标题/内容/位置/联系人"
        clearable
        style="width: 250px"
        @keyup.enter="handleSearch"
      />
      <el-select 
        v-model="selectedStatus" 
        placeholder="选择状态" 
        clearable
        style="width: 180px; margin-left: 10px"
        @change="handleSearch"
      >
        <el-option :value="0" label="待救助" />
        <el-option :value="1" label="救助中" />
        <el-option :value="2" label="已解决" />
      </el-select>
      <el-button type="primary" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
    </div>
    
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.imageUrl"
            :src="scope.row.imageUrl"
            style="width: 100px; height: 100px; object-fit: cover; cursor: pointer;"
            @click="handleImagePreview(scope.row.imageUrl)"
          />
          <div v-else class="no-image">暂无图片</div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="location" label="位置" width="150" />
      <el-table-column prop="contactName" label="联系人" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" width="130" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="success" link @click="handleStatus(scope.row, 1)" v-if="scope.row.status === 0">标记为救助中</el-button>
          <el-button type="success" link @click="handleStatus(scope.row, 2)" v-if="scope.row.status === 1">标记为已解决</el-button>
          <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="650px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入救助标题" />
        </el-form-item>
        
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入救助位置" />
        </el-form-item>
        
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option :value="0" label="待救助" />
            <el-option :value="1" label="救助中" />
            <el-option :value="2" label="已解决" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="图片" prop="imageUrl">
          <el-upload
            class="image-uploader"
            action="#"
            :http-request="uploadImage"
            :show-file-list="false"
            accept="image/*"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="image-preview" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传图片</div>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入详细描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="() => handleDialogClose(() => dialogVisible = false)">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 图片预览对话框 -->
    <el-dialog
      title="图片预览"
      v-model="imagePreviewVisible"
      width="600px"
      :close-on-click-modal="true"
    >
      <div class="image-preview-container" v-if="previewImageUrl">
        <img :src="previewImageUrl" style="max-width: 100%; max-height: 500px; display: block; margin: 0 auto;" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const keyword = ref('')
const selectedStatus = ref(null)
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

const form = reactive({
  id: null,
  title: '',
  content: '',
  location: '',
  contactName: '',
  contactPhone: '',
  imageUrl: '',
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '待救助'
    case 1: return '救助中'
    case 2: return '已解决'
    default: return '未知'
  }
}

// 获取状态类型（样式）
const getStatusType = (status) => {
  switch (status) {
    case 0: return 'danger'
    case 1: return 'warning'
    case 2: return 'success'
    default: return 'info'
  }
}

// 获取救助信息列表
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/rescue/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        keyword: keyword.value || undefined,
        status: selectedStatus.value !== null ? selectedStatus.value : undefined
      }
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '发布救助信息'
  Object.assign(form, {
    id: null,
    title: '',
    content: '',
    location: '',
    contactName: '',
    contactPhone: '',
    imageUrl: '',
    status: 0
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑救助信息'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 处理状态变更
const handleStatus = (row, status) => {
  const statusText = status === 1 ? '救助中' : '已解决'
  ElMessageBox.confirm(`确认将此救助信息标记为${statusText}吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.put('/admin/rescue/status', null, {
        params: {
          id: row.id,
          status: status
        }
      })
      ElMessage.success('状态更新成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {
    // 用户取消操作，不做任何处理
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该救助信息吗？删除后不可恢复。', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/rescue/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {
    // 用户取消操作，不做任何处理
  })
}

// 上传图片
const uploadImage = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    loading.value = true
    const res = await request.post('/admin/rescue/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.imageUrl = res.data
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('图片上传失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        if (form.id) {
          await request.put('/admin/rescue', form)
          ElMessage.success('更新成功')
        } else {
          await request.post('/admin/rescue', form)
          ElMessage.success('发布成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 图片预览
const handleImagePreview = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 处理对话框关闭
const handleDialogClose = (done) => {
  if (loading.value) {
    ElMessage.warning('正在提交数据，请等待操作完成')
    return
  }
  ElMessageBox.confirm('确认关闭？未保存的数据将丢失', '提示', {
    type: 'warning'
  }).then(() => {
    done()
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.rescue-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.no-image {
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.image-uploader:hover {
  border-color: #409EFF;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.image-preview {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
}
</style> 