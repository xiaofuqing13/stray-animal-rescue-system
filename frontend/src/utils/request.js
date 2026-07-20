import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // API的基础URL
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 请求头中添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，则判断为错误
    if (res.code !== 200) {
      // 401: 未登录或token过期
      if (res.code === 401) {
        // 清除本地token
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userRole')
        
        // 重定向到登录页
        router.replace('/login')
      }
      
      return Promise.reject(new Error(res.message || '系统错误'))
    } else {
      return res
    }
  },
  error => {
    console.error('请求错误:', error)
    
    // 获取响应中的错误信息
    const errorMessage = error.response && error.response.data && error.response.data.message
      ? error.response.data.message
      : error.message || '请求失败'
    
    // 只在error处理中显示错误消息
    ElMessage({
      message: errorMessage,
      type: 'error',
      duration: 3 * 1000
    })
    
    // 如果是401错误，清除token并跳转到登录页
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('userRole')
      router.replace('/login')
    }
    
    return Promise.reject(new Error(errorMessage))
  }
)

export default service 