<template>
  <div class="user-management">
    <div class="header">
      <h2>用户管理</h2>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="输入用户名、姓名或手机号搜索"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #suffix>
            <el-icon class="el-input__icon" @click="handleSearch">
              <Search />
            </el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <!-- 用户列表 -->
    <el-table
      v-loading="loading"
      :data="userList"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column type="index" width="50" label="#" />
      
      <el-table-column prop="username" label="用户名" min-width="120" />
      
      <el-table-column label="头像" width="80">
        <template #default="scope">
          <el-avatar :size="40" :src="scope.row.avatar || '/uploads/avatar/default.png'" />
        </template>
      </el-table-column>
      
      <el-table-column prop="name" label="姓名" min-width="120" />
      
      <el-table-column prop="phone" label="手机号" min-width="120" />
      
      <el-table-column prop="email" label="邮箱" min-width="150" />
      
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
      
      <el-table-column prop="role" label="角色" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.role === 1 ? 'danger' : 'primary'">
            {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
            :disabled="scope.row.role === 1"
          />
        </template>
      </el-table-column>
      
      <el-table-column prop="createTime" label="注册时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button 
            size="small" 
            type="primary" 
            @click="handleEdit(scope.row)"
            :disabled="scope.row.role === 1 && currentUserId !== scope.row.id"
          >
            编辑
          </el-button>
          <el-button 
            size="small" 
            type="danger" 
            @click="handleDelete(scope.row)"
            :disabled="scope.row.role === 1"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
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

    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户信息"
      width="500px"
    >
      <el-form :model="editForm" label-width="80px" ref="editFormRef" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        
        <el-form-item label="地址" prop="address">
          <el-input v-model="editForm.address" type="textarea" rows="3" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitLoading">
            保存
          </el-button>
          <el-button v-if="editForm.id" type="warning" @click="handleResetPassword" :loading="resetLoading">
            重置密码
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, updateUser, deleteUser, updateUserStatus, resetUserPassword } from '@/api/admin'

const router = useRouter()

// 获取当前登录用户ID
const currentUserId = computed(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id
})

// 数据加载状态
const loading = ref(false)
const submitLoading = ref(false)
const resetLoading = ref(false)

// 用户列表数据
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 编辑对话框
const dialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  username: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  status: 1
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 初始化加载数据
onMounted(() => {
  fetchUserList()
})

// 加载用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value
    }
    
    const { data } = await getUserList(params)
    userList.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 编辑用户
const handleEdit = (row) => {
  Object.assign(editForm, row)
  dialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await updateUser(editForm.id, editForm)
        ElMessage.success('更新用户信息成功')
        dialogVisible.value = false
        fetchUserList()
      } catch (error) {
        ElMessage.error(error.message || '更新用户信息失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 修改用户状态
const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success(`已${row.status === 1 ? '启用' : '禁用'}用户`)
  } catch (error) {
    // 恢复原始状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('更新用户状态失败')
  }
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除此用户吗？此操作不可逆。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除用户成功')
      fetchUserList()
    } catch (error) {
      ElMessage.error(error.message || '删除用户失败')
    }
  }).catch(() => {
    // 取消删除，不做操作
  })
}

// 重置密码
const handleResetPassword = async () => {
  ElMessageBox.confirm(
    '确定要重置此用户的密码吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    resetLoading.value = true
    try {
      await resetUserPassword(editForm.id)
      ElMessage.success('密码重置成功')
    } catch (error) {
      ElMessage.error(error.message || '密码重置失败')
    } finally {
      resetLoading.value = false
    }
  }).catch(() => {
    // 取消重置，不做操作
  })
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  width: 350px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 