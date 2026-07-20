<template>
  <div class="password-container">
    <div class="page-header">
      <h1 class="page-title">修改密码</h1>
      <p class="page-subtitle">您可以在此修改您的登录密码</p>
    </div>

    <el-card class="password-card">
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="120px"
        class="password-form"
        v-loading="loading"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="form.oldPassword" 
            type="password" 
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="form.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">确认修改</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
      
      <div class="password-tips">
        <h3>密码安全提示：</h3>
        <ul>
          <li>密码长度至少为6位</li>
          <li>建议使用字母、数字和特殊字符的组合</li>
          <li>请勿使用与其他网站相同的密码</li>
          <li>请定期更换密码以保证账号安全</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { changePassword } from '@/api/user';

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);
const submitting = ref(false);

// 表单数据
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 检查两次密码输入是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 表单校验规则
const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    // 检查新旧密码是否相同
    if (form.oldPassword === form.newPassword) {
      ElMessage.warning('新密码不能与原密码相同');
      return;
    }
    
    submitting.value = true;
    // 修改密码API调用
    const res = await changePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    });
    
    // 如果请求成功
    if (res && res.code === 200) {
      // 使用后端返回的消息
      ElMessage.success(res.message || '密码修改成功，请使用新密码重新登录');
      
      // 清除登录信息
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('userRole');
      
      // 延迟跳转到登录页，给用户时间看到成功消息
      setTimeout(() => {
        router.push('/login');
      }, 1500);
    } else {
      // 如果后端返回错误信息
      ElMessage.error(res && res.message ? res.message : '密码修改失败，请稍后重试');
    }
  } catch (error) {
    console.error('修改密码出错:', error);
    
    if (error === false) {
      // 表单验证失败
      ElMessage.warning('请检查表单填写是否正确');
    } else if (error.response) {
      // 服务器返回错误
      const { status, data } = error.response;
      
      if (status === 400) {
        ElMessage.error('原密码错误，请重新输入');
      } else if (data && data.msg) {
        ElMessage.error(data.msg);
      } else {
        ElMessage.error(`服务器错误 (${status})，请稍后重试`);
      }
    } else if (error.request) {
      // 请求发送成功但没有收到响应
      ElMessage.error('服务器无响应，请检查网络连接或联系管理员');
    } else {
      // 请求设置错误
      ElMessage.error('请求失败: ' + (error.message || '未知错误'));
    }
  } finally {
    submitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  formRef.value.resetFields();
};

// 返回个人中心
const goBack = () => {
  router.push('/user/profile');
};
</script>

<style scoped>
.password-container {
  max-width: 600px;
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

.password-card {
  margin-bottom: 30px;
}

.password-form {
  padding: 20px 0;
}

.password-tips {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.password-tips h3 {
  font-size: 16px;
  color: #606266;
  margin: 0 0 10px;
}

.password-tips ul {
  margin: 0;
  padding-left: 20px;
}

.password-tips li {
  color: #909399;
  line-height: 1.8;
  font-size: 14px;
}
</style> 