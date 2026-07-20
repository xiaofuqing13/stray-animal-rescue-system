<template>
  <div class="category-list">
    <div class="page-header">
      <h2>知识分类管理</h2>
      <el-button type="primary" @click="handleAdd">添加分类</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入分类名称或描述"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      border
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="180" />
      <el-table-column prop="description" label="分类描述" />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
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
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 400px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入分类描述"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getKnowledgeCategoryPage, addKnowledgeCategory, updateKnowledgeCategory, deleteKnowledgeCategory } from '@/api/admin'
import { formatDate } from '@/utils/date'

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 表单相关
const dialogVisible = ref(false)
const formTitle = ref('添加分类')
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  description: ''
})
const rules = reactive({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '长度不超过 200 个字符', trigger: 'blur' }
  ]
})

// 初始化
onMounted(() => {
  fetchData()
})

// 加载数据
const fetchData = () => {
  loading.value = true
  getKnowledgeCategoryPage(currentPage.value, pageSize.value, searchKeyword.value)
    .then(response => {
      tableData.value = response.data.records
      total.value = response.data.total
    })
    .catch(error => {
      console.error('获取知识分类列表失败:', error)
      ElMessage.error('获取知识分类列表失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
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

// 添加分类
const handleAdd = () => {
  formTitle.value = '添加分类'
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  formTitle.value = '编辑分类'
  formRef.value?.resetFields()
  Object.assign(form, {
    id: row.id,
    name: row.name,
    description: row.description
  })
  dialogVisible.value = true
}

// 提交表单
const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (form.id) {
        // 更新
        updateKnowledgeCategory(form)
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
            console.error('更新分类失败:', error)
            ElMessage.error('更新分类失败')
          })
      } else {
        // 添加
        addKnowledgeCategory(form)
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
            console.error('添加分类失败:', error)
            ElMessage.error('添加分类失败')
          })
      }
    }
  })
}

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除这个分类吗？删除后无法恢复，且会影响相关联的知识文章。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      deleteKnowledgeCategory(row.id)
        .then(response => {
          if (response.code === 200) {
            ElMessage.success('删除成功')
            fetchData()
          } else {
            ElMessage.error(response.msg || '删除失败')
          }
        })
        .catch(error => {
          console.error('删除分类失败:', error)
          ElMessage.error('删除分类失败')
        })
    })
    .catch(() => {
      // 取消删除
    })
}
</script>

<style scoped>
.category-list {
  padding: 20px;
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

.search-input {
  width: 350px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 