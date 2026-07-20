<template>
  <div class="video-list">
    <div class="header">
      <h2>视频管理</h2>
      <el-button type="primary" @click="handleAdd">上传视频</el-button>
    </div>
    
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="请输入视频标题关键字"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <el-select 
        v-model="selectedCategory" 
        placeholder="选择分类" 
        clearable
        style="width: 180px; margin-left: 10px"
        @change="handleSearch"
      >
        <el-option
          v-for="item in categories"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button type="primary" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
    </div>
    
    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column label="封面" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.coverUrl"
            :src="scope.row.coverUrl"
            fit="cover"
            style="width: 80px; height: 45px; cursor: pointer;"
            @click="handleImagePreview(scope.row.coverUrl)"
          />
          <span v-else>无封面</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="分类" width="100">
        <template #default="scope">
          {{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="播放数" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="handlePreview(scope.row)">预览</el-button>
          <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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
    
    <!-- 新增/编辑对话框 -->
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
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入视频标题" />
        </el-form-item>
        
        <el-form-item label="视频文件" prop="videoUrl" v-if="!form.id">
          <el-upload
            class="video-uploader"
            action="#"
            :http-request="uploadVideo"
            :show-file-list="false"
            accept="video/*"
          >
            <div v-if="form.videoUrl" class="video-preview">
              <video :src="form.videoUrl" controls style="max-width: 100%; max-height: 150px"></video>
            </div>
            <el-icon v-else class="upload-icon"><Upload /></el-icon>
            <div class="upload-tip">点击上传视频 (MP4格式)</div>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="视频地址" prop="videoUrl" v-if="form.id">
          <el-input v-model="form.videoUrl" placeholder="视频文件URL" disabled />
          <div class="video-preview" v-if="form.videoUrl">
            <video :src="form.videoUrl" controls style="max-width: 100%; max-height: 150px; margin-top: 10px"></video>
          </div>
        </el-form-item>
        
        <el-form-item label="封面" prop="coverUrl">
          <el-upload
            class="cover-uploader"
            action="#"
            :http-request="uploadCover"
            :show-file-list="false"
            accept="image/*"
          >
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传封面图片</div>
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入视频描述"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="() => handleDialogClose(() => dialogVisible = false)">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 视频预览对话框 -->
    <el-dialog
      title="视频预览"
      v-model="previewVisible"
      width="800px"
      :close-on-click-modal="true"
      :before-close="handlePreviewClose"
    >
      <div class="video-preview-container">
        <video 
          v-if="previewVideo" 
          :src="previewVideo.videoUrl" 
          controls 
          style="width: 100%"
          ref="previewVideoElement"
        ></video>
        <div class="video-info" v-if="previewVideo">
          <h3>{{ previewVideo.title }}</h3>
          <p>{{ previewVideo.description }}</p>
          <p>分类：{{ getCategoryName(previewVideo.categoryId) }}</p>
          <p>播放量：{{ previewVideo.viewCount }}</p>
          <p>上传时间：{{ previewVideo.createTime }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      title="封面预览"
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
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const categories = ref([])
const previewVisible = ref(false)
const previewVideo = ref(null)
const keyword = ref('')
const selectedCategory = ref(null)
const previewVideoElement = ref(null)
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

const form = reactive({
  id: null,
  title: '',
  description: '',
  videoUrl: '',
  coverUrl: '',
  categoryId: null,
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  videoUrl: [{ required: true, message: '请上传视频文件', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(item => item.id === categoryId)
  return category ? category.name : '未知分类'
}

// 获取视频列表
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/video/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        keyword: keyword.value || undefined,
        categoryId: selectedCategory.value || undefined
      }
    })
    
    tableData.value = res.data.records;
    
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取所有分类
const getCategories = async () => {
  try {
    const res = await request.get('/admin/video/category/list')
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '上传视频'
  Object.assign(form, {
    id: null,
    title: '',
    description: '',
    videoUrl: '',
    coverUrl: '',
    categoryId: null,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑视频'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 预览
const handlePreview = (row) => {
  previewVideo.value = { ...row }
  previewVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该视频吗？删除后不可恢复。', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/video/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  })
}

// 上传视频
const uploadVideo = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    loading.value = true
    const res = await request.post('/admin/video/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.videoUrl = res.data
    ElMessage.success('视频上传成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('视频上传失败')
  } finally {
    loading.value = false
  }
}

// 上传封面
const uploadCover = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    loading.value = true
    const res = await request.post('/admin/video/cover/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.coverUrl = res.data
    ElMessage.success('封面上传成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('封面上传失败')
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
        if (form.id) {
          await request.put('/admin/video', form)
        } else {
          await request.post('/admin/video', form)
        }
        ElMessage.success(form.id ? '更新成功' : '添加成功')
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error(error)
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

// 处理对话框关闭
const handleDialogClose = (done) => {
  if (loading.value) {
    ElMessage.warning('文件正在上传中，请等待上传完成')
    return
  }
  ElMessageBox.confirm('确认关闭？未保存的数据将丢失', '提示', {
    type: 'warning'
  }).then(() => {
    done()
  }).catch(() => {})
}

// 处理预览对话框关闭
const handlePreviewClose = (done) => {
  // 获取视频元素并暂停播放
  if (previewVideoElement.value) {
    previewVideoElement.value.pause();
    previewVideoElement.value.currentTime = 0;
  }
  done();
}

// 监听预览对话框可见性变化
watch(previewVisible, (newValue) => {
  if (!newValue && previewVideoElement.value) {
    // 对话框关闭时，确保视频停止播放
    previewVideoElement.value.pause();
    previewVideoElement.value.currentTime = 0;
  }
});

// 封面图片预览
const handleImagePreview = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

onMounted(() => {
  getCategories()
  getList()
})
</script>

<style scoped>
.video-list {
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
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.video-uploader,
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.video-uploader:hover,
.cover-uploader:hover {
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

.cover-preview {
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

.video-preview-container {
  display: flex;
  flex-direction: column;
}

.video-info {
  margin-top: 15px;
}

.video-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
}

.video-info p {
  margin: 5px 0;
  color: #606266;
}
</style> 