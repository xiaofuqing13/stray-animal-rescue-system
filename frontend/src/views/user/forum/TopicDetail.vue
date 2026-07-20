<template>
  <div class="topic-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button @click="goBack" :icon="Back" plain>返回论坛</el-button>
    </div>

    <!-- 话题内容 -->
    <div class="topic-content" v-loading="loading">
      <el-skeleton :rows="10" animated v-if="loading" />
      <div v-else>
        <div class="topic-header">
          <h1 class="topic-title">{{ topic.title }}</h1>
          <div class="topic-info">
            <span class="info-item author-info">
              <el-avatar :size="32" :src="getAvatarUrl(topic.user ? topic.user.avatar : '')" icon="User"></el-avatar>
              {{ topic.user ? topic.user.name : topic.userName || '未知用户' }}
            </span>
            <span class="info-item">
              <el-icon><View /></el-icon>
              {{ topic.viewCount }} 浏览
            </span>
            <span class="info-item">
              <el-icon><ChatLineRound /></el-icon>
              {{ topic.commentCount }} 评论
            </span>
            <span class="info-item">
              <el-icon><Timer /></el-icon>
              {{ formatDate(topic.createTime) }}
            </span>
          </div>
        </div>

        <div class="topic-body">
          <p class="topic-text">{{ topic.content }}</p>
          <div class="topic-image" v-if="topic.imageUrl">
            <el-image 
              :src="topic.imageUrl" 
              fit="cover"
              :preview-src-list="[topic.imageUrl]"
            />
          </div>
        </div>

        <div class="topic-actions" v-if="canDelete">
          <el-button type="danger" :icon="Delete" @click="handleDeleteTopic">删除话题</el-button>
        </div>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-container">
      <h2 class="comments-title">
        <el-icon><ChatLineRound /></el-icon>
        评论区 ({{ topic.commentCount || 0 }})
      </h2>

      <!-- 发表评论 -->
      <div class="comment-form">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="请输入你的评论..."
          :maxlength="500"
          show-word-limit
        />
        <div class="form-actions">
          <el-button type="primary" @click="submitComment" :disabled="!commentContent.trim()">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list" v-loading="commentsLoading">
        <el-empty v-if="commentList.length === 0 && !commentsLoading" description="暂无评论，来发表第一条评论吧！" />
        <div v-else>
          <div v-for="comment in commentList" :key="comment.id" class="comment-item">
            <div class="comment-user">
              <el-avatar :size="40" :src="getAvatarUrl(comment.user ? comment.user.avatar : '')" icon="UserFilled"></el-avatar>
              <div class="user-info">
                <div class="username">{{ comment.user ? comment.user.name : '未知用户' }}</div>
                <div class="comment-time">{{ formatDate(comment.createTime) }}</div>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="text" @click="showReplyForm(comment)">回复</el-button>
              <el-button type="text" v-if="comment.userId === userId" @click="handleDeleteComment(comment.id)">删除</el-button>
            </div>

            <!-- 回复表单 -->
            <div class="reply-form" v-if="replyToId === comment.id">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                placeholder="请输入回复内容..."
                :maxlength="200"
                show-word-limit
              />
              <div class="form-actions">
                <el-button @click="cancelReply">取消</el-button>
                <el-button type="primary" @click="submitReply(comment.id)" :disabled="!replyContent.trim()">
                  回复
                </el-button>
              </div>
            </div>

            <!-- 回复列表 -->
            <div class="replies-list" v-if="comment.replies && comment.replies.length > 0">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="comment-user">
                  <el-avatar :size="32" :src="getAvatarUrl(reply.user ? reply.user.avatar : '')" icon="UserFilled"></el-avatar>
                  <div class="user-info">
                    <div class="username">{{ reply.user ? reply.user.name : '未知用户' }}</div>
                    <div class="comment-time">{{ formatDate(reply.createTime) }}</div>
                  </div>
                </div>
                <div class="comment-content">{{ reply.content }}</div>
                <div class="comment-actions">
                  <el-button type="text" @click="showReplyForm(comment)">回复</el-button>
                  <el-button type="text" v-if="reply.userId === userId" @click="handleDeleteComment(reply.id)">删除</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="commentList.length > 0">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="commentsTotal"
          :page-size="commentsPageSize"
          :current-page="commentsPageNum"
          @current-change="handleCommentsPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Back, Delete, View, User, Timer, ChatLineRound } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  getTopicDetail, 
  deleteTopic, 
  getCommentPage, 
  getCommentReplies, 
  addComment, 
  deleteComment 
} from '@/api/forum';
import { getAvatarUrl } from '@/utils/index';

const router = useRouter();
const route = useRoute();
const topicId = ref(route.params.id);
const userId = ref(null);

const topic = ref({});
const loading = ref(true);
const commentList = ref([]);
const commentsLoading = ref(false);
const commentsTotal = ref(0);
const commentsPageNum = ref(1);
const commentsPageSize = ref(10);
const commentContent = ref('');
const replyToId = ref(null);
const replyContent = ref('');

const canDelete = computed(() => {
  return userId.value && topic.value.userId === Number(userId.value);
});

onMounted(async () => {
  // 从userInfo中获取用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  userId.value = userInfo.id || null;
  
  await loadTopicDetail();
  loadComments();
});

const loadTopicDetail = async () => {
  loading.value = true;
  try {
    const res = await getTopicDetail(topicId.value);
    topic.value = res.data;
  } catch (error) {
    console.error('加载话题详情失败', error);
    ElMessage.error('加载话题详情失败');
  } finally {
    loading.value = false;
  }
};

const loadComments = async () => {
  commentsLoading.value = true;
  try {
    const res = await getCommentPage({
      topicId: topicId.value,
      pageNum: commentsPageNum.value,
      pageSize: commentsPageSize.value
    });
    
    // 获取评论列表
    commentList.value = res.data.records;
    commentsTotal.value = res.data.total;
    
    // 加载每个评论的回复
    await Promise.all(commentList.value.map(async (comment) => {
      try {
        const repliesRes = await getCommentReplies(comment.id);
        comment.replies = repliesRes.data;
      } catch (error) {
        console.error(`加载评论${comment.id}的回复失败`, error);
        comment.replies = [];
      }
    }));
  } catch (error) {
    console.error('加载评论列表失败', error);
    ElMessage.error('加载评论列表失败');
  } finally {
    commentsLoading.value = false;
  }
};

const handleCommentsPageChange = (val) => {
  commentsPageNum.value = val;
  loadComments();
};

const submitComment = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录后再评论');
    return;
  }
  
  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空');
    return;
  }
  
  try {
    await addComment({
      topicId: topicId.value,
      content: commentContent.value,
      parentId: null // 顶级评论
    });
    
    ElMessage.success('评论发表成功');
    commentContent.value = '';
    
    // 重新加载评论和话题详情
    loadComments();
    loadTopicDetail();
  } catch (error) {
    console.error('发表评论失败', error);
    ElMessage.error('发表评论失败');
  }
};

const showReplyForm = (comment) => {
  if (!userId.value) {
    ElMessage.warning('请先登录后再回复');
    return;
  }
  
  replyToId.value = comment.id;
  replyContent.value = '';
};

const cancelReply = () => {
  replyToId.value = null;
  replyContent.value = '';
};

const submitReply = async (commentId) => {
  if (!userId.value) {
    ElMessage.warning('请先登录后再回复');
    return;
  }
  
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空');
    return;
  }
  
  try {
    await addComment({
      topicId: topicId.value,
      content: replyContent.value,
      parentId: commentId
    });
    
    ElMessage.success('回复发表成功');
    replyToId.value = null;
    replyContent.value = '';
    
    // 重新加载评论和话题详情
    loadComments();
    loadTopicDetail();
  } catch (error) {
    console.error('发表回复失败', error);
    ElMessage.error('发表回复失败');
  }
};

const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await deleteComment(commentId);
    ElMessage.success('评论删除成功');
    
    // 重新加载评论和话题详情
    loadComments();
    loadTopicDetail();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败', error);
      ElMessage.error('删除评论失败');
    }
  }
};

const handleDeleteTopic = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个话题吗？删除后将无法恢复！', '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await deleteTopic(topicId.value);
    ElMessage.success('话题删除成功');
    goBack();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除话题失败', error);
      ElMessage.error('删除话题失败');
    }
  }
};

const goBack = () => {
  router.push('/user/forum');
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
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

</script>

<style scoped>
.topic-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.topic-content {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 30px;
  margin-bottom: 30px;
}

.topic-header {
  margin-bottom: 20px;
}

.topic-title {
  font-size: 24px;
  color: #303133;
  margin: 0 0 15px;
  word-break: break-word;
}

.topic-info {
  display: flex;
  flex-wrap: wrap;
  color: #909399;
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
  margin-bottom: 10px;
}

.author-info {
  display: flex;
  align-items: center;
}

.author-info .el-avatar {
  margin-right: 8px;
}

.info-item .el-icon {
  margin-right: 5px;
}

.topic-body {
  margin-bottom: 30px;
}

.topic-text {
  font-size: 16px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 20px;
}

.topic-image {
  margin-top: 20px;
  max-width: 100%;
}

.topic-image .el-image {
  max-width: 100%;
  border-radius: 8px;
}

.topic-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.comments-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.comments-title {
  font-size: 20px;
  color: #303133;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
}

.comments-title .el-icon {
  margin-right: 8px;
}

.comment-form {
  margin-bottom: 30px;
}

.form-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  margin-left: 10px;
}

.username {
  font-weight: bold;
  color: #303133;
  font-size: 14px;
}

.comment-time {
  color: #909399;
  font-size: 12px;
  margin-top: 3px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin: 5px 0;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  margin-top: 5px;
}

.reply-form {
  margin: 10px 0 10px 50px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.replies-list {
  margin-left: 50px;
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.reply-item {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.reply-item:last-child {
  border-bottom: none;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media screen and (max-width: 768px) {
  .topic-detail-container {
    padding: 10px;
  }
  
  .topic-content, 
  .comments-container {
    padding: 15px;
  }
  
  .topic-title {
    font-size: 20px;
  }
  
  .comments-title {
    font-size: 18px;
  }
}
</style> 