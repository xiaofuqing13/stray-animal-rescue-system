<template>
  <div class="rescue-form-container">
    <div class="page-header">
      <h1>发布救助信息</h1>
      <p class="sub-title">请填写流浪动物的救助信息，帮助它们尽快得到救助</p>
    </div>

    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="left"
        v-loading="loading"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入救助标题，例如：发现一只受伤的流浪猫"></el-input>
        </el-form-item>

        <el-form-item label="内容描述" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述动物的状况、特征等信息，以便救助人员更好地了解情况"
          ></el-input>
        </el-form-item>

        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入动物的具体位置，例如：XX市XX区XX街道附近"></el-input>
        </el-form-item>

        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入您的姓名"></el-input>
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入您的手机号码"></el-input>
        </el-form-item>

        <el-form-item label="动物照片">
          <el-upload
            class="rescue-image-uploader"
            :action="null"
            :http-request="handleUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="rescue-image" alt="动物照片" />
            <el-icon v-else class="rescue-image-uploader-icon"><Plus /></el-icon>
            <div class="el-upload__tip">
              点击上传动物照片（可选）
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="submitForm">提交</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addRescueInfo, uploadRescueImage } from '@/api/rescue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  title: '',
  content: '',
  location: '',
  contactName: '',
  contactPhone: '',
  imageUrl: '',
  status: 0 // 默认为待救助状态
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2到100个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '内容长度在10到1000个字符之间', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入位置', trigger: 'blur' },
    { min: 5, max: 255, message: '位置长度在5到255个字符之间', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { min: 2, max: 50, message: '联系人长度在2到50个字符之间', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    loading.value = true
    const res = await addRescueInfo(form)
    if (res.code === 200) {
      ElMessage.success('救助信息发布成功')
      router.push('/user/rescue')
    } else {
      ElMessage.error(res.msg || '发布失败，请稍后重试')
    }
  } catch (error) {
    console.error('表单验证失败', error)
  } finally {
    loading.value = false
  }
}

// 上传前校验
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG或PNG格式的图片')
    return false
  }
  
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  
  return true
}

// 自定义上传
const handleUpload = async (options) => {
  const { file } = options
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    loading.value = true
    const res = await uploadRescueImage(formData)
    if (res.code === 200) {
      form.imageUrl = res.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(res.msg || '上传失败，请稍后重试')
    }
  } catch (error) {
    console.error('上传图片失败', error)
    ElMessage.error('上传失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 返回列表
const goBack = () => {
  router.push('/user/rescue')
}
</script>

<style scoped>
.rescue-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #409EFF;
  margin-bottom: 10px;
}

.sub-title {
  color: #606266;
  font-size: 16px;
}

.form-card {
  margin-bottom: 40px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.rescue-image-uploader {
  width: 100%;
}

.rescue-image-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 200px;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.rescue-image-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.rescue-image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 30px;
  height: 30px;
}

.rescue-image {
  width: 200px;
  height: 200px;
  display: block;
  object-fit: cover;
}

.rescue-image-uploader :deep(.el-upload__tip) {
  margin-top: 10px;
  text-align: center;
  color: #909399;
}
</style> 