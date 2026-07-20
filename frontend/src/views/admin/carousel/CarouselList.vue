<template>
  <div class="carousel-container">
    <div class="header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="handleAdd">添加轮播图</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入标题关键字"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>
    
    <!-- 轮播图列表 -->
    <el-table :data="carouselList" border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="200">
        <template #default="{ row }">
          <el-image
            :src="row.imageUrl"
            :preview-src-list="[row.imageUrl]"
            fit="cover"
            style="width: 150px; height: 80px; cursor: pointer"
            :preview-teleported="true"
            :initial-index="0"
            preview-class="carousel-preview"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <el-upload
            class="carousel-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="carousel-image" />
            <el-icon v-else class="carousel-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCarouselList, addCarousel, updateCarousel, deleteCarousel, updateCarouselStatus } from '@/api/admin'

// 数据列表
const carouselList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  title: '',
  imageUrl: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 上传相关
const uploadUrl = '/api/admin/upload'
const uploadHeaders = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

// 获取轮播图列表
const getList = async () => {
  try {
    const { data } = await getCarouselList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    carouselList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
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

// 添加轮播图
const handleAdd = () => {
  dialogTitle.value = '添加轮播图'
  Object.assign(form, {
    id: null,
    title: '',
    imageUrl: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑轮播图
const handleEdit = (row) => {
  dialogTitle.value = '编辑轮播图'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除轮播图
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCarousel(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除轮播图失败:', error)
    }
  })
}

// 修改状态
const handleStatusChange = async (row) => {
  try {
    await updateCarouselStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('修改状态失败:', error)
    row.status = row.status === 1 ? 0 : 1 // 恢复状态
  }
}

// 上传图片
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    // 检查返回的URL是否已经是完整URL
    let imageUrl = response.data;
    if (imageUrl && !imageUrl.startsWith('http') && !imageUrl.startsWith('/')) {
      imageUrl = '/' + imageUrl;
    }
    // 确保URL前有/uploads前缀
    if (imageUrl && !imageUrl.includes('/uploads')) {
      imageUrl = '/uploads' + imageUrl;
    }
    form.imageUrl = imageUrl;
    console.log('设置图片URL:', form.imageUrl);
  } else {
    ElMessage.error(response.message || '上传失败');
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt3M = file.size / 1024 / 1024 < 3

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt3M) {
    ElMessage.error('图片大小不能超过 3MB!')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 确保所有必填字段都有值
    if (!form.title || !form.imageUrl || form.sort === undefined || form.status === undefined) {
      ElMessage.error('请填写所有必填字段')
      return
    }
    
    if (form.id) {
      // 编辑
      await updateCarousel(form)
      ElMessage.success('更新成功')
    } else {
      // 添加
      await addCarousel(form)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存轮播图失败:', error)
  }
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.carousel-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.carousel-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 100px;
}

.carousel-uploader:hover {
  border-color: #409EFF;
}

.carousel-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.carousel-image {
  width: 200px;
  height: 100px;
  display: block;
  object-fit: cover;
}

:deep(.carousel-preview) {
  z-index: 9999;
}

:deep(.el-image-viewer__wrapper) {
  z-index: 9999;
}

:deep(.el-image-viewer__mask) {
  z-index: 9998;
}

:deep(.el-image-viewer__canvas) {
  z-index: 9999;
}

:deep(.el-image-viewer__actions) {
  z-index: 9999;
}

:deep(.el-image-viewer__actions__inner) {
  z-index: 9999;
}
</style> 