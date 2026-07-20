<template>
  <div class="article-list">
    <div class="page-header">
      <h2>知识文章管理</h2>
      <el-button type="primary" @click="handleAdd">添加文章</el-button>
    </div>

    <div class="search-bar">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入文章标题或关键词"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="categoryId"
            placeholder="选择分类"
            clearable
            @change="handleSearch"
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-col>
      </el-row>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      border
      style="width: 100%; overflow-x: auto;"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="文章标题" width="180" show-overflow-tooltip />
      <el-table-column label="分类" width="120">
        <template #default="scope">
          {{ scope.row.categoryName }}
        </template>
      </el-table-column>
      <el-table-column label="封面" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.imageUrl"
            :src="scope.row.imageUrl"
            style="width: 80px; height: 50px"
            fit="cover"
            @click="previewImage(scope.row.imageUrl, scope.row.title)"
          />
          <span v-else>无封面</span>
        </template>
      </el-table-column>
      <el-table-column label="视频" width="80">
        <template #default="scope">
          <el-button v-if="scope.row.videoUrl" 
            size="small" 
            type="primary" 
            link
            @click="previewVideo(scope.row.videoUrl, scope.row.title)">
            预览
          </el-button>
          <span v-else>无视频</span>
        </template>
      </el-table-column>
      <el-table-column label="视频封面" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.videoCoverUrl"
            :src="scope.row.videoCoverUrl"
            style="width: 80px; height: 50px"
            fit="cover"
            @click="previewImage(scope.row.videoCoverUrl, '视频封面: ' + scope.row.title)"
          />
          <span v-else>无视频封面</span>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读量" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            @click="handleEdit(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
          <el-button
            size="small"
            type="success"
            @click="handlePreviewContent(scope.row)"
          >
            预览内容
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="formTitle"
      width="800px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" />
        </el-form-item>
        
        <el-form-item label="所属分类" prop="categoryId">
          <el-select
            v-model="form.categoryId"
            placeholder="选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="封面图片">
          <el-upload
            class="avatar-uploader"
            :action="uploadAction"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸: 1200x800像素，JPG/PNG格式</div>
        </el-form-item>
        
        <el-form-item label="视频">
          <el-upload
            class="video-uploader"
            :action="uploadVideoAction"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleVideoSuccess"
            :before-upload="beforeVideoUpload"
          >
            <video 
              v-if="form.videoUrl" 
              :src="form.videoUrl" 
              class="video-preview" 
              controls
            ></video>
            <el-button v-else>
              <el-icon><Upload /></el-icon>
              <span>上传视频</span>
            </el-button>
          </el-upload>
          <div class="upload-tip">支持MP4格式，大小不超过50MB</div>
        </el-form-item>
        
        <el-form-item label="视频封面" v-if="form.videoUrl">
          <el-upload
            class="avatar-uploader"
            :action="uploadVideoCoverAction"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleVideoCoverSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.videoCoverUrl" :src="form.videoCoverUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸: 1200x800像素，JPG/PNG格式</div>
        </el-form-item>
        
        <el-form-item label="文章内容" prop="content">
          <div style="border: 1px solid #dcdfe6; border-radius: 4px;">
            <el-input
              v-model="form.content"
              type="textarea"
              placeholder="请输入文章内容"
              :rows="10"
            />
          </div>
        </el-form-item>
        
        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 视频预览对话框 -->
    <el-dialog
      v-model="videoDialogVisible"
      :title="videoDialogTitle"
      width="800px"
      destroy-on-close
      center
      custom-class="video-preview-dialog"
    >
      <div class="video-preview-container">
        <video 
          v-if="previewVideoUrl" 
          :src="previewVideoUrl" 
          class="video-preview-full" 
          controls
          autoplay
        ></video>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="imageDialogVisible"
      :title="imageDialogTitle"
      width="800px"
      destroy-on-close
      center
      custom-class="image-preview-dialog"
    >
      <div class="image-preview-container">
        <img 
          v-if="previewImageUrl" 
          :src="previewImageUrl" 
          class="image-preview-full" 
          alt="图片预览"
        />
      </div>
    </el-dialog>

    <!-- 文章内容预览对话框 -->
    <el-dialog
      v-model="contentDialogVisible"
      :title="contentDialogTitle"
      width="800px"
      destroy-on-close
      custom-class="content-preview-dialog"
    >
      <div class="content-preview-container">
        <div v-if="previewContent" class="content-preview-body" v-html="previewContent"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getKnowledgeCategoryPage, 
  getAllKnowledgeCategories,
  getKnowledgeArticlePage, 
  getKnowledgeArticleById,
  addKnowledgeArticle, 
  updateKnowledgeArticle, 
  deleteKnowledgeArticle,
  uploadKnowledgeArticleImage,
  uploadKnowledgeArticleVideo,
  uploadKnowledgeArticleVideoCover
} from '@/api/admin'
import { formatDate } from '@/utils/date'
import { getToken } from '@/utils/auth'

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const categoryId = ref(null)
const categoryOptions = ref([])

// 表单相关
const dialogVisible = ref(false)
const formTitle = ref('添加文章')
const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  categoryId: null,
  imageUrl: '',
  videoUrl: '',
  videoCoverUrl: '',
  content: '',
  viewCount: 0,
  status: 1
})
const rules = reactive({
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
})

// 上传相关
const uploadAction = '/api/admin/knowledge/article/upload/image'
const uploadVideoAction = '/api/admin/knowledge/article/upload/video'
const uploadVideoCoverAction = '/api/admin/knowledge/article/upload/video-cover'
const headers = {
  Authorization: getToken()
}

// 视频预览相关
const videoDialogVisible = ref(false)
const videoDialogTitle = ref('')
const previewVideoUrl = ref('')

// 图片预览相关
const imageDialogVisible = ref(false)
const imageDialogTitle = ref('')
const previewImageUrl = ref('')

// 文章内容预览相关
const contentDialogVisible = ref(false)
const contentDialogTitle = ref('')
const previewContent = ref('')

// 初始化
onMounted(() => {
  console.log('正在加载知识分类...');
  fetchCategories();
  fetchData();
})

// 加载分类
const fetchCategories = () => {
  console.log('正在加载知识分类...');
  getAllKnowledgeCategories()
    .then(response => {
      if (response.code === 200) {
        console.log('知识分类加载成功:', response.data);
        categoryOptions.value = response.data;
      }
    })
    .catch(error => {
      console.error('获取知识分类列表失败:', error);
      ElMessage.error('获取知识分类列表失败');
    });
};

// 加载数据
const fetchData = () => {
  loading.value = true;
  console.log('正在加载知识文章列表...');
  console.log('参数:', {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value,
    categoryId: categoryId.value
  });
  
  getKnowledgeArticlePage(currentPage.value, pageSize.value, searchKeyword.value, categoryId.value)
    .then(response => {
      if (response.code === 200) {
        console.log('知识文章列表加载成功:', response.data);
        tableData.value = response.data.records;
        total.value = response.data.total;
        
        // 检查每条记录是否有分类名称，如果没有则从缓存中获取
        tableData.value.forEach(item => {
          if (!item.categoryName && item.categoryId) {
            const category = categoryOptions.value.find(c => c.id === item.categoryId);
            if (category) {
              item.categoryName = category.name;
            }
          }
        });
      }
    })
    .catch(error => {
      console.error('获取知识文章列表失败:', error);
      ElMessage.error('获取知识文章列表失败');
    })
    .finally(() => {
      loading.value = false;
    });
};

// 搜索
const handleSearch = () => {
  // 确保categoryId不是字符串"all"
  if (categoryId.value === 'all') {
    categoryId.value = null;
  }
  currentPage.value = 1;
  fetchData();
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

// 添加文章
const handleAdd = () => {
  formTitle.value = '添加文章'
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    title: '',
    categoryId: null,
    imageUrl: '',
    videoUrl: '',
    videoCoverUrl: '',
    content: '',
    viewCount: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑文章
const handleEdit = (row) => {
  formTitle.value = '编辑文章'
  formRef.value?.resetFields()
  
  // 先获取详细信息
  getKnowledgeArticleById(row.id)
    .then(response => {
      if (response.code === 200) {
        const article = response.data
        Object.assign(form, {
          id: article.id,
          title: article.title,
          categoryId: article.categoryId,
          imageUrl: article.imageUrl,
          videoUrl: article.videoUrl,
          videoCoverUrl: article.videoCoverUrl,
          content: article.content,
          viewCount: article.viewCount,
          status: article.status
        })
        dialogVisible.value = true
      } else {
        ElMessage.error(response.msg || '获取文章详情失败')
      }
    })
    .catch(error => {
      console.error('获取文章详情失败:', error)
      ElMessage.error('获取文章详情失败')
    })
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(valid => {
    if (!valid) return;
    
    // 确保分类ID不是字符串"all"
    if (form.categoryId === 'all') {
      form.categoryId = null;
    }
    
    if (form.id) {
      // 更新
      updateKnowledgeArticle(form)
        .then(response => {
          if (response.code === 200) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchData()
          } else {
            ElMessage.error(response.msg || '更新失败')
          }
        })
        .catch(error => {
          console.error('更新文章失败:', error)
          ElMessage.error('更新文章失败')
        })
    } else {
      // 添加
      addKnowledgeArticle(form)
        .then(response => {
          if (response.code === 200) {
            ElMessage.success('添加成功')
            dialogVisible.value = false
            fetchData()
          } else {
            ElMessage.error(response.msg || '添加失败')
          }
        })
        .catch(error => {
          console.error('添加文章失败:', error)
          ElMessage.error('添加文章失败')
        })
    }
  })
}

// 删除文章
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除这篇文章吗？删除后无法恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      deleteKnowledgeArticle(row.id)
        .then(response => {
          if (response.code === 200) {
            ElMessage.success('删除成功')
            fetchData()
          } else {
            ElMessage.error(response.msg || '删除失败')
          }
        })
        .catch(error => {
          console.error('删除文章失败:', error)
          ElMessage.error('删除文章失败')
        })
    })
    .catch(() => {
      // 取消删除
    })
}

// 图片上传
const handleCoverSuccess = (response) => {
  if (response.code === 200) {
    form.imageUrl = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt4M = file.size / 1024 / 1024 < 4

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt4M) {
    ElMessage.error('上传图片大小不能超过 4MB!')
    return false
  }
  return true
}

// 视频上传
const handleVideoSuccess = (response) => {
  if (response.code === 200) {
    form.videoUrl = response.data
    ElMessage.success('视频上传成功')
  } else {
    ElMessage.error(response.msg || '视频上传失败')
  }
}

const beforeVideoUpload = (file) => {
  const isMP4 = file.type === 'video/mp4'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isMP4) {
    ElMessage.error('上传视频只能是 MP4 格式!')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('上传视频大小不能超过 50MB!')
    return false
  }
  return true
}

// 视频封面上传
const handleVideoCoverSuccess = (response) => {
  if (response.code === 200) {
    form.videoCoverUrl = response.data
    ElMessage.success('视频封面上传成功')
  } else {
    ElMessage.error(response.msg || '视频封面上传失败')
  }
}

// 预览视频
const previewVideo = (videoUrl, title) => {
  previewVideoUrl.value = videoUrl
  videoDialogTitle.value = `预览视频: ${title}`
  videoDialogVisible.value = true
}

// 预览图片
const previewImage = (imageUrl, title) => {
  previewImageUrl.value = imageUrl
  imageDialogTitle.value = `预览图片: ${title}`
  imageDialogVisible.value = true
}

// 预览文章内容
const handlePreviewContent = (row) => {
  // 获取文章详情
  getKnowledgeArticleById(row.id)
    .then(response => {
      if (response.code === 200) {
        const article = response.data;
        previewContent.value = article.content;
        contentDialogTitle.value = `文章预览: ${article.title}`;
        contentDialogVisible.value = true;
      } else {
        ElMessage.error(response.msg || '获取文章详情失败');
      }
    })
    .catch(error => {
      console.error('获取文章详情失败:', error);
      ElMessage.error('获取文章详情失败');
    });
}
</script>

<style scoped>
.article-list {
  padding: 20px;
  overflow-x: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.video-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 360px;
  margin-bottom: 10px;
}

.video-preview {
  width: 360px;
  max-height: 240px;
  display: block;
  margin-bottom: 10px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.video-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.03);
  padding: 20px;
  border-radius: 8px;
}

.video-preview-full {
  width: 100%;
  max-height: 600px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.03);
  padding: 20px;
  border-radius: 8px;
}

.image-preview-full {
  max-width: 100%;
  max-height: 600px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.content-preview-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  max-height: 70vh;
  overflow-y: auto;
}

.content-preview-body {
  line-height: 1.8;
  font-size: 16px;
  color: #333;
  text-align: justify;
  white-space: pre-wrap;
}

.content-preview-body img {
  max-width: 100%;
  border-radius: 4px;
  margin: 10px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-preview-body h1,
.content-preview-body h2,
.content-preview-body h3 {
  margin-top: 20px;
  margin-bottom: 10px;
  font-weight: 600;
  color: #222;
}
</style>

<style>
/* 全局样式 - 预览对话框样式 */
.el-dialog__wrapper {
  backdrop-filter: blur(5px);
}

.image-preview-dialog .el-dialog__body,
.video-preview-dialog .el-dialog__body,
.content-preview-dialog .el-dialog__body {
  padding: 0;
}

.image-preview-dialog .el-dialog,
.video-preview-dialog .el-dialog {
  margin-top: 15vh !important;
  background-color: transparent;
  box-shadow: none;
}

.content-preview-dialog .el-dialog {
  margin-top: 10vh !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.image-preview-dialog .el-dialog__header,
.video-preview-dialog .el-dialog__header {
  padding-top: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  margin-right: 0;
  border-radius: 8px 8px 0 0;
}

.content-preview-dialog .el-dialog__header {
  background-color: #f7f7f7;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  border-radius: 8px 8px 0 0;
}

.el-image-viewer__mask {
  background-color: rgba(0, 0, 0, 0.85) !important;
  backdrop-filter: blur(5px);
}
</style> 