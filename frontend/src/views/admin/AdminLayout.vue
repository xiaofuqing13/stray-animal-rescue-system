<template>
  <div class="admin-layout">
    <el-container class="container">
      <el-aside width="200px" class="aside">
        <div class="logo">
          <h3>流浪动物保护系统</h3>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router>
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/user">用户列表</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/admin/carousel">轮播图管理</el-menu-item>
            <el-menu-item index="/admin/announcement">通知公告管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="3">
            <template #title>
              <el-icon><VideoPlay /></el-icon>
              <span>视频管理</span>
            </template>
            <el-menu-item index="/admin/video">视频列表</el-menu-item>
            <el-menu-item index="/admin/video/category">视频分类</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="4">
            <template #title>
              <el-icon><View /></el-icon>
              <span>动物管理</span>
            </template>
            <el-menu-item index="/admin/animal">动物列表</el-menu-item>
            <el-menu-item index="/admin/animal-category">动物分类</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="5">
            <template #title>
              <el-icon><Files /></el-icon>
              <span>领养管理</span>
            </template>
            <el-menu-item index="/admin/adoption">领养申请</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="6">
            <template #title>
              <el-icon><Service /></el-icon>
              <span>救助管理</span>
            </template>
            <el-menu-item index="/admin/rescue">救助信息</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="7">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>知识管理</span>
            </template>
            <el-menu-item index="/admin/knowledge/category">知识分类</el-menu-item>
            <el-menu-item index="/admin/knowledge/article">知识文章</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="right">
            <el-dropdown trigger="click">
              <span class="user-info">
                <img :src="avatar" class="user-avatar" alt="avatar">
                {{ username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handlePassword">修改密码</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  DataLine, 
  User, 
  Picture, 
  VideoPlay, 
  ArrowDown, 
  Document,
  View,
  Service,
  Files,
  Reading
} from '@element-plus/icons-vue'
import { getAvatarUrl } from '@/utils/index'
import { getUserInfo as apiGetUserInfo } from '@/api/user'

const router = useRouter()
const route = useRoute()

// 活动菜单
const activeMenu = computed(() => route.path)

// 用户信息
const username = ref('')
const avatar = ref('')

// 先用缓存渲染，再异步用服务端真实数据覆盖，自动清除旧账号残留的脏 localStorage
onMounted(async () => {
  const cached = JSON.parse(localStorage.getItem('userInfo') || '{}')
  username.value = cached.username || '管理员'
  avatar.value = getAvatarUrl(cached.avatar)

  if (!localStorage.getItem('token')) return
  try {
    const res = await apiGetUserInfo()
    if (res?.code === 200 && res.data) {
      username.value = res.data.username || '管理员'
      avatar.value = getAvatarUrl(res.data.avatar)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
  } catch (_) {
    // 401 由 axios 响应拦截器统一处理
  }
})

// 修改密码
const handlePassword = () => {
  router.push('/admin/profile/password')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除登录信息
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userRole')
    
    // 跳转到登录页
    router.push('/login')
    
    ElMessage.success('已退出登录')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.container {
  height: 100%;
}

.aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 16px;
  border-bottom: 1px solid #1f2d3d;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
}

.right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 10px;
}

.el-menu {
  border-right: none;
}
</style> 