<template>
  <div class="forum-container">
    <div class="forum-header">
      <h1 class="main-title">社区论坛</h1>
      <p class="sub-title">分享您的经验、故事和问题，与爱心人士一起讨论流浪动物相关话题</p>
      <div class="action-buttons">
        <el-button type="primary" @click="goToPublish" :icon="Edit">发布话题</el-button>
      </div>
    </div>

    <div class="search-container">
      <el-input
        v-model="keyword"
        placeholder="搜索话题"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <div class="forum-content">
      <el-empty v-if="topicList.length === 0 && !loading" description="暂无话题，快来发布第一个话题吧！" />
      <el-skeleton :rows="5" animated v-if="loading" />
      <div v-else class="topic-list">
        <div v-for="topic in topicList" :key="topic.id" class="topic-card" @click="goToDetail(topic.id)">
          <div class="topic-content">
            <h3 class="topic-title">{{ topic.title }}</h3>
            <p class="topic-excerpt">{{ truncateText(topic.content, 100) }}</p>
            <div class="topic-info">
              <span class="info-item author-info">
                <el-avatar :size="24" :src="getAvatarUrl(topic.user ? topic.user.avatar : '')" icon="User"></el-avatar>
                {{ topic.user ? topic.user.name : topic.userName || '未知用户' }}
              </span>
              <span class="info-item">
                <el-icon><View /></el-icon>
                {{ topic.viewCount }}
              </span>
              <span class="info-item">
                <el-icon><ChatLineRound /></el-icon>
                {{ topic.commentCount }}
              </span>
              <span class="info-item">
                <el-icon><Timer /></el-icon>
                {{ formatDate(topic.createTime) }}
              </span>
            </div>
          </div>
          <div class="topic-image" v-if="topic.imageUrl">
            <el-image 
              :src="topic.imageUrl" 
              fit="cover"
              :preview-src-list="[topic.imageUrl]"
              @click.stop
            />
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-container" v-if="topicList.length > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        :page-sizes="[10, 20, 30, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, User, View, ChatLineRound, Timer, Edit } from '@element-plus/icons-vue';
import { getTopicPage } from '@/api/forum';
import { ElMessage } from 'element-plus';
import { getAvatarUrl } from '@/utils/index';

const router = useRouter();
const topicList = ref([]);
const loading = ref(false);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(20);
const keyword = ref('');
const userId = ref(null);

onMounted(() => {
  // 获取用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  userId.value = userInfo.id || null;
  
  loadTopicList();
});

const loadTopicList = async () => {
  loading.value = true;
  try {
    const res = await getTopicPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    });
    
    topicList.value = res.data.records;
    total.value = res.data.total;
  } catch (error) {
    console.error('加载话题列表失败', error);
    ElMessage.error('加载话题列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageNum.value = 1;
  loadTopicList();
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadTopicList();
};

const handleCurrentChange = (val) => {
  pageNum.value = val;
  loadTopicList();
};

const goToDetail = (id) => {
  router.push(`/user/forum/topic/${id}`);
};

const goToPublish = () => {
  router.push('/user/forum/publish');
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now - date;
  
  // 不到1分钟
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  // 不到1小时
  if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`;
  }
  // 不到1天
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`;
  }
  // 不到1周
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`;
  }
  
  // 超过1周显示完整日期
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + '...';
};

</script>

<style scoped>
.forum-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.forum-header {
  text-align: center;
  margin-bottom: 30px;
}

.main-title {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.sub-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 20px;
}

.action-buttons {
  margin-top: 20px;
}

.search-container {
  display: flex;
  margin-bottom: 20px;
}

.search-input {
  margin-right: 10px;
  width: 100%;
}

.forum-content {
  margin-top: 20px;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.topic-card {
  display: flex;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.topic-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.topic-content {
  flex: 1;
  margin-right: 20px;
}

.topic-title {
  font-size: 18px;
  color: #303133;
  margin: 0 0 10px;
}

.topic-excerpt {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.topic-info {
  display: flex;
  color: #909399;
  font-size: 13px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  margin-right: 16px;
  font-size: 14px;
  color: #909399;
}

.author-info {
  display: flex;
  align-items: center;
}

.author-info .el-avatar {
  margin-right: 5px;
}

.info-item .el-icon {
  margin-right: 5px;
}

.topic-image {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
}

.topic-image .el-image {
  width: 100%;
  height: 100%;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media screen and (max-width: 768px) {
  .topic-card {
    flex-direction: column;
  }
  
  .topic-content {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .topic-image {
    width: 100%;
    height: 180px;
  }
}
</style> 