<template>
  <div class="topic-publish-container">
    <div class="back-button">
      <el-button @click="goBack" :icon="Back" plain>返回论坛</el-button>
    </div>

    <div class="form-card">
      <h1 class="form-title">发布话题</h1>
      <p class="form-subtitle">分享您的经验和问题，与爱心人士一起交流</p>
      
      <el-form 
        ref="formRef" 
        :model="topicForm" 
        :rules="rules" 
        label-position="top"
        class="topic-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="topicForm.title" 
            placeholder="请输入话题标题（5-50字）" 
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="topicForm.content" 
            type="textarea" 
            :rows="8" 
            placeholder="请详细描述您的话题内容..." 
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="上传图片（可选）">
          <el-upload
            class="topic-upload"
            :action="uploadAction"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <div v-if="topicForm.imageUrl" class="preview-container">
              <el-image 
                :src="topicForm.imageUrl" 
                fit="cover" 
                class="preview-image"
                :preview-src-list="[topicForm.imageUrl]"
              />
              <div class="preview-mask">
                <el-icon><EditPen /></el-icon>
                <span>重新上传</span>
              </div>
            </div>
            <el-button v-else type="primary" :icon="Plus">选择图片</el-button>
            <template #tip>
              <div class="upload-tip">
                支持 JPG、PNG 格式图片，大小不超过5MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <div class="form-actions">
          <el-button @click="goBack">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">发布话题</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Back, Plus, EditPen } from '@element-plus/icons-vue';
import { addTopic, uploadTopicImage } from '@/api/forum';
import { getAvatarUrl } from '@/utils/index';

const router = useRouter();
const formRef = ref(null);
const submitting = ref(false);

// 检查用户是否登录
onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  if (!userInfo.id) {
    ElMessage.warning('请先登录后再发布话题');
    router.push('/login');
  }
});

const topicForm = reactive({
  title: '',
  content: '',
  imageUrl: ''
});

const uploadAction = ''; // 仅作为占位符，实际上传会被自定义处理函数拦截

const rules = {
  title: [
    { required: true, message: '请输入话题标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度应为5-50个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入话题内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度应为10-2000个字符', trigger: 'blur' }
  ]
};

const goBack = () => {
  router.push('/user/forum');
};

const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error('只能上传JPG或PNG格式的图片！');
    return false;
  }
  
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！');
    return false;
  }
  
  return true;
};

const handleUpload = async (options) => {
  const file = options.file;
  
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const res = await uploadTopicImage(formData);
    topicForm.imageUrl = res.data;
    ElMessage.success('图片上传成功');
  } catch (error) {
    console.error('上传图片失败', error);
    ElMessage.error('上传图片失败');
  }
};

const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    submitting.value = true;
    await addTopic({
      title: topicForm.title,
      content: topicForm.content,
      imageUrl: topicForm.imageUrl
    });
    
    ElMessage.success('话题发布成功');
    router.push('/user/forum');
  } catch (error) {
    if (error === false) {
      // 表单验证失败
      ElMessage.warning('请检查表单填写是否正确');
    } else {
      console.error('发布话题失败', error);
      ElMessage.error('发布话题失败');
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.topic-publish-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.form-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.form-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px;
  text-align: center;
}

.form-subtitle {
  color: #606266;
  font-size: 16px;
  margin-bottom: 30px;
  text-align: center;
}

.topic-form {
  margin-top: 20px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.topic-upload {
  display: flex;
  justify-content: center;
}

.preview-container {
  position: relative;
  width: 200px;
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-container:hover .preview-mask {
  opacity: 1;
}

.preview-mask .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 10px;
}

@media screen and (max-width: 768px) {
  .topic-publish-container {
    padding: 10px;
  }
  
  .form-card {
    padding: 20px;
  }
  
  .form-title {
    font-size: 20px;
  }
  
  .form-subtitle {
    font-size: 14px;
  }
}
</style> 