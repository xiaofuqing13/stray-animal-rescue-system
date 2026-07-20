<template>
  <div class="login-container" :style="`background-image: url(${backgroundImage})`">
    <div class="login-card">
      <div class="logo">
        <h2>流浪动物保护系统</h2>
      </div>
      
      <el-tabs v-model="activeTab" class="demo-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password>
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loginLoading" class="submit-btn" @click="handleLogin">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="用户名">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="密码" show-password>
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" show-password>
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="name">
              <el-input v-model="registerForm.name" placeholder="姓名">
                <template #prefix>
                  <el-icon><UserFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="registerForm.phone" placeholder="电话">
                <template #prefix>
                  <el-icon><PhoneFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="email">
              <el-input v-model="registerForm.email" placeholder="邮箱">
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="address">
              <el-input v-model="registerForm.address" placeholder="地址">
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="avatar" label="头像">
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleAvatarChange">
                <img v-if="avatarUrl" :src="avatarUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="registerLoading" class="submit-btn" @click="handleRegister">注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, PhoneFilled, Message, Location, Plus } from '@element-plus/icons-vue'
import { login, register } from '../api/user.js'

const router = useRouter()
const route = useRoute()

// 背景图
const backgroundImage = ref(require('@/assets/image/LoginBackground.jpg'))

// 当前激活的标签页
const activeTab = ref('login')

// 登录表单
const loginFormRef = ref(null)
const loginForm = reactive({
  username: '',
  password: ''
})
const loginLoading = ref(false)

// 登录表单验证规则
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册表单
const registerFormRef = ref(null)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  email: '',
  address: ''
})
const registerLoading = ref(false)
const avatarFile = ref(null)
const avatarUrl = ref('')

// 注册表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

// 处理头像上传
const handleAvatarChange = (file) => {
  avatarFile.value = file.raw
  avatarUrl.value = URL.createObjectURL(file.raw)
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loginLoading.value = true
        const { data } = await login(loginForm)
        
        // 保存用户信息和token
        localStorage.setItem('token', data.token)
        localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
        
        // 获取用户角色
        const userRole = data.userInfo.role
        localStorage.setItem('userRole', userRole.toString())
        
        ElMessage.success('登录成功')
        
        // 根据角色进入不同页面（1为管理员，0为普通用户）
        if (userRole === 1) {
          router.push('/admin/dashboard')
        } else {
          router.push('/user/home')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loginLoading.value = false
      }
    }
  })
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        registerLoading.value = true
        
        // 创建表单数据
        const formData = new FormData()
        if (avatarFile.value) {
          formData.append('avatar', avatarFile.value)
        }
        
        // 添加用户信息
        formData.append('username', registerForm.username)
        formData.append('password', registerForm.password)
        formData.append('name', registerForm.name)
        formData.append('phone', registerForm.phone)
        formData.append('email', registerForm.email)
        formData.append('address', registerForm.address)
        
        await register(formData)
        
        ElMessage.success('注册成功，请登录')
        activeTab.value = 'login'
        
        // 清空注册表单
        registerFormRef.value.resetFields()
        avatarUrl.value = ''
        avatarFile.value = null
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        registerLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-position: center;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
}

.login-card {
  width: 480px;
  padding: 30px;
  background-color: rgba(255, 255, 255, 0.55);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(5px);
}

.logo {
  text-align: center;
  margin-bottom: 20px;
}

.logo h2 {
  color: #409EFF;
}

.submit-btn {
  width: 100%;
}

.avatar-uploader {
  text-align: center;
  width: 100%;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}
</style> 