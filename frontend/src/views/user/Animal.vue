<template>
  <div class="animal-list">
    <div class="search-bar">
      <div class="filters">
        <el-input
          v-model="searchParams.keyword"
          placeholder="搜索动物名称、特征或描述"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="searchParams.categoryId"
          placeholder="动物分类"
          clearable
          @change="handleSearch"
        >
          <el-option
            v-for="item in categories"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </div>
    
    <div class="content" v-loading="loading">
      <el-empty v-if="animalList.length === 0 && !loading" description="暂无符合条件的流浪动物" />
      
      <div v-else class="animal-grid">
        <div v-for="animal in animalList" :key="animal.id" class="animal-card" @click="viewDetail(animal.id)">
          <div class="animal-image">
            <img :src="animal.imageUrl || DEFAULT_IMAGE_PLACEHOLDER" :alt="animal.name">
            <div class="animal-status">待领养</div>
          </div>
          <div class="animal-info">
            <h3 class="animal-name">{{ animal.name }}</h3>
            <div class="animal-meta">
              <el-tag size="small">{{ animal.categoryName }}</el-tag>
              <span class="animal-gender">
                <el-icon :color="animal.gender === 0 ? '#409EFF' : '#FF66AA'">
                  <Male v-if="animal.gender === 0" />
                  <Female v-else />
                </el-icon>
                {{ animal.gender === 0 ? '公' : '母' }}
              </span>
            </div>
            <div class="animal-features">
              <p class="animal-age" v-if="animal.age">{{ formatAge(animal.age) }}</p>
              <p class="animal-weight" v-if="animal.weight">{{ animal.weight }}kg</p>
            </div>
            <p class="animal-description">{{ truncateText(animal.description, 60) }}</p>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Male, Female } from '@element-plus/icons-vue'
import { getAdoptableAnimals, getAnimalCategories } from '@/api/animal'
import { DEFAULT_IMAGE_PLACEHOLDER } from '@/utils/index'

const router = useRouter()
const loading = ref(false)
const animalList = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

const searchParams = reactive({
  keyword: '',
  categoryId: null
})

// 初始化
onMounted(() => {
  fetchCategories()
  fetchAnimalList()
})

// 获取动物分类
const fetchCategories = async () => {
  try {
    const res = await getAnimalCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取动物分类失败:', error)
  }
}

// 获取动物列表
const fetchAnimalList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchParams.keyword,
      categoryId: searchParams.categoryId
    }
    
    const res = await getAdoptableAnimals(params)
    if (res.code === 200) {
      animalList.value = res.data.records
      
      // 处理分类名称
      if (animalList.value.length > 0) {
        // 如果有动物数据但没有分类名称，需要补充分类信息
        const needCategoryNames = animalList.value.some(animal => 
          !animal.categoryName && animal.categoryId
        )
        
        if (needCategoryNames && categories.value.length > 0) {
          // 如果已经有分类数据，直接使用
          animalList.value.forEach(animal => {
            if (!animal.categoryName && animal.categoryId) {
              const category = categories.value.find(c => c.id === animal.categoryId)
              if (category) {
                animal.categoryName = category.name
              } else {
                animal.categoryName = animal.breed || '未知分类'
              }
            }
          })
        } else if (needCategoryNames) {
          // 重新获取分类数据
          try {
            const categoryRes = await getAnimalCategories()
            if (categoryRes.code === 200) {
              categories.value = categoryRes.data
              
              // 更新动物分类名称
              animalList.value.forEach(animal => {
                if (!animal.categoryName && animal.categoryId) {
                  const category = categories.value.find(c => c.id === animal.categoryId)
                  if (category) {
                    animal.categoryName = category.name
                  } else {
                    animal.categoryName = animal.breed || '未知分类'
                  }
                }
              })
            }
          } catch (error) {
            console.error('获取分类数据失败:', error)
          }
        }
      }
      
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '获取流浪动物列表失败')
    }
  } catch (error) {
    console.error('获取流浪动物列表失败:', error)
    ElMessage.error('获取流浪动物列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchAnimalList()
}

// 页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAnimalList()
}

// 每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchAnimalList()
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/user/animal/detail/${id}`)
}

// 格式化年龄
const formatAge = (ageInMonths) => {
  if (ageInMonths < 12) {
    return `${ageInMonths}个月`
  } else {
    const years = Math.floor(ageInMonths / 12)
    const months = ageInMonths % 12
    return months > 0 ? `${years}岁${months}个月` : `${years}岁`
  }
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text
}
</script>

<style scoped>
.animal-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-bar {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.filters {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.filters .el-input {
  width: 300px;
}

.content {
  min-height: 500px;
}

.animal-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.animal-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.animal-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.animal-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.animal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.animal-card:hover .animal-image img {
  transform: scale(1.05);
}

.animal-status {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #67c23a;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.animal-info {
  padding: 15px;
}

.animal-name {
  font-size: 18px;
  margin: 0 0 10px;
  color: #333;
}

.animal-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.animal-gender {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 14px;
}

.animal-features {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.animal-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  height: 60px;
  overflow: hidden;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .animal-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .animal-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filters .el-input,
  .filters .el-select {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .animal-grid {
    grid-template-columns: 1fr;
  }
}
</style> 