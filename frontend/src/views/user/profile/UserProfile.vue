<template>
  <div class="profile-container">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-subtitle">查看和管理您的个人信息</p>
    </div>

    <el-card class="profile-card">
      <div class="profile-header">
        <div class="avatar-container">
          <el-avatar 
            :size="100" 
            :src="getAvatarUrl(form.avatar)" 
            icon="UserFilled"
            class="avatar"
          ></el-avatar>
          <div class="avatar-upload">
            <el-upload
              class="upload-btn"
              :action="uploadAction"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <el-button size="small" type="primary">更换头像</el-button>
            </el-upload>
          </div>
        </div>
        <div class="user-info">
          <h2>{{ form.username }}</h2>
          <p>用户ID: {{ form.id }}</p>
          <p>注册时间: {{ formatDate(form.createTime) }}</p>
        </div>
      </div>

      <el-divider />

      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
        class="profile-form"
        v-loading="loading"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled placeholder="用户名不可修改"></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入您的真实姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="电话号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入您的电话号码"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入您的邮箱地址"></el-input>
        </el-form-item>
        
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入您的地址"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goToChangePassword" type="info">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getUserInfo, updateUserInfo } from '@/api/user';
import { getAvatarUrl } from '@/utils/index';

const router = useRouter();
const formRef = ref(null);
const loading = ref(true);
const submitting = ref(false);
const uploadAction = ''; // 仅作为占位符，实际上传会被自定义处理函数拦截

// 表单数据
const form = reactive({
  id: '',
  username: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  avatar: '',
  createTime: ''
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符之间', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { max: 255, message: '地址长度不能超过255个字符', trigger: 'blur' }
  ]
};

// 页面加载时获取用户信息
onMounted(async () => {
  await loadUserInfo();
});

// 加载用户信息
const loadUserInfo = async () => {
  loading.value = true;
  try {
    const res = await getUserInfo();
    Object.assign(form, res.data);
  } catch (error) {
    console.error('获取用户信息失败', error);
    ElMessage.error('获取用户信息失败');
  } finally {
    loading.value = false;
  }
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    submitting.value = true;
    const formData = new FormData();
    // 只提交需要更新的字段
    formData.append('name', form.name);
    formData.append('phone', form.phone || '');
    formData.append('email', form.email || '');
    formData.append('address', form.address || '');
    
    // 如果有新上传的头像，则不需要在这里处理，因为上传头像时已经更新了
    
    await updateUserInfo(formData);
    
    ElMessage.success('用户信息更新成功');
    
    // 更新本地存储的用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    userInfo.name = form.name;
    userInfo.avatar = form.avatar;
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
    
  } catch (error) {
    if (error === false) {
      // 表单验证失败
      ElMessage.warning('请检查表单填写是否正确');
    } else {
      console.error('更新用户信息失败', error);
      ElMessage.error('更新用户信息失败');
    }
  } finally {
    submitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  loadUserInfo();
};

// 头像上传前的校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('头像只能是JPG或PNG格式的图片！');
    return false;
  }
  
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过2MB！');
    return false;
  }
  
  return true;
};

// 处理头像上传
const handleAvatarUpload = async (options) => {
  const file = options.file;
  
  const formData = new FormData();
  formData.append('avatar', file);
  
  try {
    const res = await updateUserInfo(formData);
    form.avatar = res.data.avatar;
    ElMessage.success('头像上传成功');
    
    // 更新本地存储的用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    userInfo.avatar = form.avatar;
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  } catch (error) {
    console.error('上传头像失败', error);
    ElMessage.error('上传头像失败');
  }
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 跳转到修改密码页面
const goToChangePassword = () => {
  router.push('/user/profile/password');
};
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
}

.profile-card {
  margin-bottom: 30px;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 30px;
}

.avatar {
  margin-bottom: 10px;
}

.user-info {
  flex: 1;
}

.user-info h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #303133;
}

.user-info p {
  margin: 5px 0;
  color: #606266;
}

.profile-form {
  padding: 20px 0;
}

@media screen and (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .avatar-container {
    margin-right: 0;
    margin-bottom: 20px;
  }
}
</style> 