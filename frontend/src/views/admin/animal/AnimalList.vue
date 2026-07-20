<template>
  <div class="animal-list">
    <div class="header">
      <h2>动物列表</h2>
      <el-button type="primary" @click="handleAdd">添加动物</el-button>
    </div>
    
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="请输入动物名称/特征/描述"
        clearable
        style="width: 250px"
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
      <el-select 
        v-model="selectedStatus" 
        placeholder="选择状态" 
        clearable
        style="width: 180px; margin-left: 10px"
        @change="handleSearch"
      >
        <el-option :value="0" label="待领养" />
        <el-option :value="1" label="已领养" />
        <el-option :value="2" label="救助中" />
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
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column label="分类" width="120">
        <template #default="scope">
          {{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="breed" label="品种" width="120" />
      <el-table-column label="性别" width="80">
        <template #default="scope">
          {{ scope.row.gender === 0 ? '雄性' : '雌性' }}
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄(月)" width="100" />
      <el-table-column prop="weight" label="体重(kg)" width="100" />
      <el-table-column prop="healthStatus" label="健康状态" width="120" />
      <el-table-column prop="features" label="特征" width="150" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
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
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="650px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入动物名称" />
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
        
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入动物品种" />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">雄性</el-radio>
            <el-radio :label="1">雌性</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="年龄(月)" prop="age">
          <el-input-number v-model="form.age" :min="0" />
        </el-form-item>
        
        <el-form-item label="体重(kg)" prop="weight">
          <el-input-number v-model="form.weight" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        
        <el-form-item label="健康状态" prop="healthStatus">
          <el-select v-model="form.healthStatus" placeholder="请选择健康状态" style="width: 100%">
            <el-option value="健康" label="健康" />
            <el-option value="亚健康" label="亚健康" />
            <el-option value="生病" label="生病" />
            <el-option value="重病" label="重病" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="特征" prop="features">
          <el-input v-model="form.features" placeholder="请输入动物特征" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option :value="0" label="待领养" />
            <el-option :value="1" label="已领养" />
            <el-option :value="2" label="救助中" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="救助时间" prop="rescueTime" v-if="form.status === 2">
          <el-date-picker 
            v-model="form.rescueTime" 
            type="datetime" 
            placeholder="请选择救助时间"
            style="width: 100%" 
          />
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
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入详细描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
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
import { ref, reactive, onMounted, computed } from 'vue'
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
const categories = ref([])
const keyword = ref('')
const selectedCategory = ref(null)
const selectedStatus = ref(null)
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  breed: '',
  gender: 0,
  age: null,
  weight: null,
  healthStatus: '健康',
  features: '',
  description: '',
  imageUrl: '',
  status: 0,
  rescueTime: null
})

const rules = {
  name: [{ required: true, message: '请输入动物名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(item => item.id === categoryId)
  return category ? category.name : '未知分类'
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '待领养'
    case 1: return '已领养'
    case 2: return '救助中'
    default: return '未知'
  }
}

// 获取状态类型（样式）
const getStatusType = (status) => {
  switch (status) {
    case 0: return 'success'
    case 1: return 'info'
    case 2: return 'warning'
    default: return 'info'
  }
}

// 获取动物列表
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/animal/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        keyword: keyword.value || undefined,
        categoryId: selectedCategory.value || undefined,
        status: selectedStatus.value || undefined
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

// 获取所有分类
const getCategories = async () => {
  try {
    const res = await request.get('/admin/animal/category/list')
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
  dialogTitle.value = '添加动物'
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: null,
    breed: '',
    gender: 0,
    age: null,
    weight: null,
    healthStatus: '健康',
    features: '',
    description: '',
    imageUrl: '',
    status: 0,
    rescueTime: null
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑动物'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该动物吗？删除后不可恢复。', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/animal/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  })
}

// 上传图片
const uploadImage = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    const res = await request.post('/admin/animal/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.imageUrl = res.data
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error(error)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await request.put('/admin/animal', form)
        } else {
          await request.post('/admin/animal', form)
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

// 图片预览
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
.animal-list {
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