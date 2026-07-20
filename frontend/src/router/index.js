import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('../views/admin/user/UserList.vue')
      },
      {
        path: 'carousel',
        name: 'CarouselManagement',
        component: () => import('../views/admin/carousel/CarouselList.vue')
      },
      {
        path: 'video',
        name: 'AdminVideo',
        component: () => import('@/views/admin/video/VideoList.vue'),
        meta: {
          title: '视频管理',
          icon: 'VideoPlay'
        }
      },
      {
        path: 'video/category',
        name: 'AdminVideoCategory',
        component: () => import('@/views/admin/video/VideoCategoryList.vue'),
        meta: {
          title: '视频分类管理',
          icon: 'Files'
        }
      },
      {
        path: 'announcement',
        name: 'AdminAnnouncement',
        component: () => import('@/views/admin/AnnouncementList.vue'),
        meta: {
          title: '通知公告管理',
          icon: 'Bell'
        }
      },
      {
        path: 'animal',
        name: 'AdminAnimal',
        component: () => import('@/views/admin/animal/AnimalList.vue'),
        meta: {
          title: '动物列表',
          icon: 'View'
        }
      },
      {
        path: 'animal-category',
        name: 'AdminAnimalCategory',
        component: () => import('@/views/admin/animal/AnimalCategoryList.vue'),
        meta: {
          title: '动物分类',
          icon: 'Menu'
        }
      },
      {
        path: 'rescue',
        name: 'AdminRescue',
        component: () => import('@/views/admin/rescue/RescueList.vue'),
        meta: {
          title: '救助管理',
          icon: 'FirstAid'
        }
      },
      {
        path: '/admin/adoption',
        name: 'AdminAdoption',
        component: () => import('@/views/admin/adoption/AdoptionList.vue'),
        meta: { title: '领养申请管理', icon: 'Document' }
      },
      {
        path: 'knowledge/category',
        name: 'AdminKnowledgeCategory',
        component: () => import('@/views/admin/knowledge/CategoryList.vue'),
        meta: { title: '知识分类管理', icon: 'Files' }
      },
      {
        path: 'knowledge/article',
        name: 'AdminKnowledgeArticle',
        component: () => import('@/views/admin/knowledge/ArticleList.vue'),
        meta: { title: '知识文章管理', icon: 'Document' }
      },
      {
        path: 'profile/password',
        name: 'AdminChangePassword',
        component: () => import('@/views/admin/profile/ChangePassword.vue'),
        meta: { title: '修改密码', icon: 'Lock' }
      }
    ],
    meta: { requireAuth: true, role: 1 }
  },
  {
    path: '/user',
    name: 'UserLayout',
    component: () => import('../views/user/UserLayout.vue'),
    redirect: '/user/home',
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('../views/user/Home.vue')
      },
      {
        path: 'announcement',
        name: 'UserAnnouncement',
        component: () => import('../views/user/Announcement.vue')
      },
      {
        path: 'knowledge',
        name: 'KnowledgeList',
        component: () => import('../views/user/Knowledge.vue')
      },
      {
        path: 'knowledge/detail/:id',
        name: 'KnowledgeDetail',
        component: () => import('../views/user/KnowledgeDetail.vue'),
        props: true
      },
      {
        path: 'animal',
        name: 'AnimalList',
        component: () => import('@/views/user/Animal.vue')
      },
      {
        path: 'animal/detail/:id',
        name: 'AnimalDetail',
        component: () => import('@/views/user/AnimalDetail.vue'),
        props: true
      },
      {
        path: 'adoption',
        name: 'UserAdoption',
        component: () => import('@/views/user/Adoption.vue')
      },
      {
        path: 'rescue',
        name: 'RescueList',
        component: () => import('@/views/user/rescue/RescueList.vue'),
        meta: { title: '流浪动物救助', icon: 'FirstAid' }
      },
      {
        path: 'rescue/form',
        name: 'RescueForm',
        component: () => import('@/views/user/rescue/RescueForm.vue'),
        meta: { title: '发布救助信息', icon: 'Edit' }
      },
      {
        path: 'rescue/detail/:id',
        name: 'RescueDetail',
        component: () => import('@/views/user/rescue/RescueDetail.vue'),
        props: true,
        meta: { title: '救助详情', icon: 'InfoFilled' }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/user/forum/ForumList.vue'),
        meta: { title: '社区论坛', icon: 'ChatLineRound' }
      },
      {
        path: 'forum/topic/:id',
        name: 'TopicDetail',
        component: () => import('@/views/user/forum/TopicDetail.vue'),
        props: true,
        meta: { title: '话题详情', icon: 'Document' }
      },
      {
        path: 'forum/publish',
        name: 'TopicPublish',
        component: () => import('@/views/user/forum/TopicPublish.vue'),
        meta: { title: '发布话题', icon: 'Edit' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile/UserProfile.vue'),
        meta: { title: '个人中心', icon: 'User' }
      },
      {
        path: 'profile/password',
        name: 'ChangePassword',
        component: () => import('@/views/user/profile/ChangePassword.vue'),
        meta: { title: '修改密码', icon: 'Lock' }
      },
      {
        path: 'preference',
        name: 'PreferenceSetup',
        component: () => import('@/views/user/profile/PreferenceSetup.vue'),
        meta: { title: '偏好设置', icon: 'Setting' }
      },
      {
        path: 'recommendation',
        name: 'Recommendation',
        component: () => import('@/views/user/EnhancedRecommendation.vue'),
        meta: { title: '智能推荐', icon: 'MagicStick' }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/user/Favorites.vue'),
        meta: { title: '我的收藏', icon: 'Star' }
      }
    ],
    meta: { requireAuth: true, role: 0 }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  if (to.matched.some(record => record.meta.requireAuth)) {
    // 获取 token 和用户角色
    const token = localStorage.getItem('token')
    const userRole = parseInt(localStorage.getItem('userRole'))
    
    if (!token) {
      // 如果没有 token，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.meta.role !== undefined && to.meta.role !== userRole) {
      // 如果角色不匹配，重定向到对应的首页
      if (userRole === 1) {
        next({ path: '/admin/dashboard' })
      } else if (userRole === 0) {
        next({ path: '/user/home' })
      } else {
        // 无效角色，退回登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userRole')
        next({ path: '/login' })
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router 