<template>
  <div class="user-layout">
    <el-container class="container">
      <el-header class="header">
        <div class="logo">
          <h3>流浪动物保护系统</h3>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="header-menu"
          mode="horizontal"
          background-color="#fff"
          text-color="#333"
          active-text-color="#409EFF"
          router>
          <el-menu-item index="/user/home">首页</el-menu-item>
          <el-menu-item index="/user/animal">流浪动物</el-menu-item>
          <el-menu-item index="/user/adoption">我的领养</el-menu-item>
          <el-menu-item index="/user/rescue">动物救助</el-menu-item>
          <el-menu-item index="/user/knowledge">养护知识</el-menu-item>
          <el-menu-item index="/user/forum">社区论坛</el-menu-item>
        </el-menu>
        <div class="right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <img :src="avatar" class="user-avatar" alt="avatar">
              {{ username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleRecommendation">智能推荐</el-dropdown-item>
                <el-dropdown-item @click="handlePreference">偏好设置</el-dropdown-item>
                <el-dropdown-item @click="handleFavorites">我的收藏</el-dropdown-item>
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
      <el-footer class="footer">
        <p>© 2025 流浪动物保护系统 - 关爱流浪动物，温暖每一个生命</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { ArrowDown, DiscoveryFilled, ChatLineRound } from '@element-plus/icons-vue'
import { getAvatarUrl } from '@/utils/index'
import { getUserInfo as apiGetUserInfo } from '@/api/user'

const router = useRouter()
const route = useRoute()

// 活动菜单
const activeMenu = computed(() => route.path)

// 用户信息
const username = ref('')
const avatar = ref('')

// 加载时先用本地缓存渲染，再异步用真实数据覆盖。
// 这样旧账号（如 chen789）残留的 localStorage 会被服务端真实信息替换；
// 若 token 失效，axios 响应拦截器会自动清缓存并跳登录。
onMounted(async () => {
  const cached = JSON.parse(localStorage.getItem('userInfo') || '{}')
  username.value = cached.username || '用户'
  avatar.value = getAvatarUrl(cached.avatar)

  if (!localStorage.getItem('token')) return
  try {
    const res = await apiGetUserInfo()
    if (res?.code === 200 && res.data) {
      username.value = res.data.username || '用户'
      avatar.value = getAvatarUrl(res.data.avatar)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
  } catch (_) {
    // 401 由响应拦截器处理；其他错误保留缓存渲染，不打扰用户
  }
})

// 个人中心
const handleProfile = () => {
  router.push('/user/profile')
}

// 智能推荐
const handleRecommendation = () => {
  router.push('/user/recommendation')
}

// 偏好设置
const handlePreference = () => {
  router.push('/user/preference')
}

// 我的收藏
const handleFavorites = () => {
  router.push('/user/favorites')
}

// 修改密码
const handlePassword = () => {
  router.push('/user/profile/password')
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
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  color: #409EFF;
  font-size: 20px;
  width: 200px;
}

.header-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none;
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

.footer {
  background-color: #f5f5f5;
  color: #666;
  text-align: center;
  padding: 20px 0;
  margin-top: auto;
}
</style> 