<template>
  <div class="video-category-list">
    <div class="header">
      <h2>视频分类管理</h2>
      <el-button type="primary" @click="handleAdd">新增分类</el-button>
    </div>
    
    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
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
    
    <!-- 新增/编辑对话框 -->
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
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入分类描述"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/video/category/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
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

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增分类'
  Object.assign(form, {
    id: null,
    name: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该分类吗？删除后不可恢复，且会影响关联的视频。', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/video/category/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await request.put('/admin/video/category', form)
        } else {
          await request.post('/admin/video/category', form)
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

onMounted(() => {
  getList()
})
</script>

<style scoped>
.video-category-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 