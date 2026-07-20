<template>
  <div class="preference-setup">
    <el-card class="setup-card">
      <template #header>
        <div class="card-header">
          <h2>偏好设置</h2>
          <p class="subtitle">完善您的偏好，帮助我们为您推荐更合适的宠物</p>
        </div>
      </template>

      <el-form 
        ref="formRef" 
        :model="form" 
        label-width="120px"
        label-position="left"
      >
        <!-- 偏好类别 -->
        <el-form-item label="喜欢的类型">
          <el-checkbox-group v-model="form.preferredCategories">
            <el-checkbox label="猫">猫咪</el-checkbox>
            <el-checkbox label="狗">狗狗</el-checkbox>
            <el-checkbox label="兔">兔子</el-checkbox>
            <el-checkbox label="鼠">仓鼠</el-checkbox>
          </el-checkbox-group>
          <div class="form-tip">可以多选</div>
        </el-form-item>

        <!-- 偏好体型 -->
        <el-form-item label="偏好体型">
          <el-radio-group v-model="form.preferredSize">
            <el-radio label="小型">小型 (小于5kg)</el-radio>
            <el-radio label="中型">中型 (5-15kg)</el-radio>
            <el-radio label="大型">大型 (大于15kg)</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 偏好年龄段 -->
        <el-form-item label="偏好年龄">
          <el-radio-group v-model="form.preferredAgeRange">
            <el-radio label="幼年">幼年 (小于1岁)</el-radio>
            <el-radio label="成年">成年 (1-7岁)</el-radio>
            <el-radio label="老年">老年 (大于7岁)</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 偏好性别 -->
        <el-form-item label="偏好性别">
          <el-radio-group v-model="form.preferredGender">
            <el-radio label="雄性">雄性</el-radio>
            <el-radio label="雌性">雌性</el-radio>
            <el-radio label="无偏好">无偏好</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 居住环境 -->
        <el-form-item label="居住环境">
          <el-radio-group v-model="form.livingEnvironment">
            <el-radio label="公寓">公寓</el-radio>
            <el-radio label="别墅">别墅</el-radio>
            <el-radio label="农村">农村</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 养宠经验 -->
        <el-form-item label="养宠经验">
          <el-radio-group v-model="form.experience">
            <el-radio label="新手">新手（第一次养宠物）</el-radio>
            <el-radio label="有经验">有经验（养过宠物）</el-radio>
            <el-radio label="专业">专业（多年养宠经验）</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 偏好地点 -->
        <el-form-item label="偏好地区">
          <el-select v-model="form.preferredLocation" placeholder="请选择地区" style="width: 100%">
            <el-option label="北京市朝阳区" value="北京市朝阳区" />
            <el-option label="北京市海淀区" value="北京市海淀区" />
            <el-option label="北京市丰台区" value="北京市丰台区" />
            <el-option label="北京市西城区" value="北京市西城区" />
            <el-option label="北京市东城区" value="北京市东城区" />
          </el-select>
          <div class="form-tip">优先推荐该地区的宠物</div>
        </el-form-item>

        <!-- 按钮 -->
        <el-form-item>
          <el-button type="primary" @click="savePreference" :loading="loading">
            保存设置
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 动态偏好展示 -->
    <el-card class="dynamic-card" v-if="showDynamic">
      <template #header>
        <h3>您的浏览偏好</h3>
      </template>
      <div class="dynamic-content">
        <div class="dynamic-item" v-if="dynamicPreference.categoryViews">
          <span class="label">最近浏览的类型：</span>
          <el-tag 
            v-for="(count, category) in parseCategoryViews()" 
            :key="category"
            style="margin-right: 10px"
          >
            {{ category }} ({{ count }}次)
          </el-tag>
        </div>
        <div class="tip">
          <el-icon><InfoFilled /></el-icon>
          系统会根据您的浏览记录自动学习您的偏好
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

const formRef = ref(null)
const loading = ref(false)
const showDynamic = ref(false)

// 表单数据
const form = reactive({
  preferredCategories: [],
  preferredSize: '中型',
  preferredAgeRange: '成年',
  preferredGender: '无偏好',
  livingEnvironment: '公寓',
  experience: '有经验',
  preferredLocation: '北京市朝阳区'
})

// 动态偏好
const dynamicPreference = reactive({
  categoryViews: null
})

// 初始化
onMounted(() => {
  loadUserProfile()
})

// 加载用户画像
const loadUserProfile = async () => {
  try {
    const res = await request.get('/user/profile')
    if (res.code === 200 && res.data) {
      // 解析偏好类别
      if (res.data.preferredCategories) {
        try {
          form.preferredCategories = JSON.parse(res.data.preferredCategories)
        } catch (e) {
          form.preferredCategories = []
        }
      }
      
      // 其他字段
      form.preferredSize = res.data.preferredSize || '中型'
      form.preferredAgeRange = res.data.preferredAgeRange || '成年'
      form.preferredGender = res.data.preferredGender || '无偏好'
      form.livingEnvironment = res.data.livingEnvironment || '公寓'
      form.experience = res.data.experience || '有经验'
      form.preferredLocation = res.data.preferredLocation || '北京市朝阳区'
    }
    
    // 加载动态偏好
    loadDynamicPreference()
  } catch (error) {
    console.error('加载用户画像失败', error)
  }
}

// 加载动态偏好
const loadDynamicPreference = async () => {
  try {
    const res = await request.get('/user/profile')
    if (res.code === 200 && res.data) {
      // 这里可以扩展获取动态偏好的接口
      // 暂时从用户画像中获取
      showDynamic.value = true
    }
  } catch (error) {
    console.error('加载动态偏好失败', error)
  }
}

// 解析类别浏览统计
const parseCategoryViews = () => {
  if (!dynamicPreference.categoryViews) return {}
  try {
    return JSON.parse(dynamicPreference.categoryViews)
  } catch (e) {
    return {}
  }
}

// 保存偏好
const savePreference = async () => {
  if (form.preferredCategories.length === 0) {
    ElMessage.warning('请至少选择一种喜欢的宠物类型')
    return
  }
  
  loading.value = true
  try {
    const data = {
      preferredCategories: JSON.stringify(form.preferredCategories),
      preferredSize: form.preferredSize,
      preferredAgeRange: form.preferredAgeRange,
      preferredGender: form.preferredGender,
      livingEnvironment: form.livingEnvironment,
      experience: form.experience,
      preferredLocation: form.preferredLocation
    }
    
    const res = await request.post('/user/profile', data)
    
    if (res.code === 200) {
      ElMessage.success('偏好设置保存成功！')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存偏好失败', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.preferredCategories = []
  form.preferredSize = '中型'
  form.preferredAgeRange = '成年'
  form.preferredGender = '无偏好'
  form.livingEnvironment = '公寓'
  form.experience = '有经验'
  form.preferredLocation = '北京市朝阳区'
}
</script>

<style scoped>
.preference-setup {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.setup-card {
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.subtitle {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: #666;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.el-form-item {
  margin-bottom: 28px;
}

.el-checkbox-group,
.el-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.el-checkbox,
.el-radio {
  margin-right: 0;
}

.dynamic-card {
  margin-top: 20px;
}

.dynamic-content {
  padding: 10px 0;
}

.dynamic-item {
  margin-bottom: 15px;
}

.dynamic-item .label {
  font-weight: 500;
  color: #333;
  margin-right: 10px;
}

.tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background-color: #f0f9ff;
  border-radius: 4px;
  color: #0284c7;
  font-size: 14px;
  margin-top: 15px;
}
</style>
