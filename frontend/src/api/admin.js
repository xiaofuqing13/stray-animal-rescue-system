import request from '@/utils/request'

/**
 * 获取用户列表
 * @param {Object} params - 查询参数 {page, pageSize, keyword}
 * @returns {Promise}
 */
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {Number} id - 用户ID
 * @returns {Promise}
 */
export function getUserDetail(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {Number} id - 用户ID
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function updateUser(id, data) {
  return request({
    url: `/admin/user/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {Number} id - 用户ID
 * @returns {Promise}
 */
export function deleteUser(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

/**
 * 更新用户状态
 * @param {Number} id - 用户ID
 * @param {Number} status - 状态（0-禁用 1-正常）
 * @returns {Promise}
 */
export function updateUserStatus(id, status) {
  return request({
    url: `/admin/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 重置用户密码
 * @param {Number} id - 用户ID
 * @returns {Promise}
 */
export function resetUserPassword(id) {
  return request({
    url: `/admin/user/${id}/password/reset`,
    method: 'put'
  })
}

// 轮播图相关接口
export function getCarouselList(params) {
  return request({
    url: '/admin/carousel/list',
    method: 'get',
    params
  })
}

export function addCarousel(data) {
  const formData = new FormData()
  formData.append('title', data.title)
  formData.append('sort', data.sort)
  formData.append('status', data.status)
  formData.append('imageUrl', data.imageUrl)
  
  return request({
    url: '/admin/carousel/add',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updateCarousel(data) {
  const formData = new FormData()
  formData.append('id', data.id)
  formData.append('title', data.title)
  formData.append('sort', data.sort)
  formData.append('status', data.status)
  formData.append('imageUrl', data.imageUrl)
  
  return request({
    url: '/admin/carousel/update',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteCarousel(id) {
  return request({
    url: `/admin/carousel/delete/${id}`,
    method: 'post'
  })
}

export function updateCarouselStatus(id, status) {
  return request({
    url: `/admin/carousel/status/${id}/${status}`,
    method: 'post'
  })
}

// 文件上传接口
export function uploadFile(data) {
  return request({
    url: '/admin/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 知识分类管理
 */
// 分页查询知识分类
export function getKnowledgeCategoryPage(pageNum, pageSize, keyword) {
  return request({
    url: '/admin/knowledge/category/page',
    method: 'get',
    params: {
      pageNum,
      pageSize,
      keyword
    }
  })
}

// 获取所有知识分类
export function getAllKnowledgeCategories() {
  return request({
    url: '/admin/knowledge/category/list',
    method: 'get'
  })
}

// 根据ID获取知识分类
export function getKnowledgeCategoryById(id) {
  return request({
    url: `/admin/knowledge/category/${id}`,
    method: 'get'
  })
}

// 添加知识分类
export function addKnowledgeCategory(data) {
  return request({
    url: '/admin/knowledge/category',
    method: 'post',
    data
  })
}

// 更新知识分类
export function updateKnowledgeCategory(data) {
  return request({
    url: '/admin/knowledge/category',
    method: 'put',
    data
  })
}

// 删除知识分类
export function deleteKnowledgeCategory(id) {
  return request({
    url: `/admin/knowledge/category/${id}`,
    method: 'delete'
  })
}

/**
 * 知识文章管理
 */
// 分页查询知识文章
export function getKnowledgeArticlePage(pageNum, pageSize, keyword, categoryId) {
  return request({
    url: '/admin/knowledge/article/page',
    method: 'get',
    params: {
      pageNum,
      pageSize,
      keyword,
      categoryId
    }
  })
}

// 根据ID获取知识文章
export function getKnowledgeArticleById(id) {
  return request({
    url: `/admin/knowledge/article/${id}`,
    method: 'get'
  })
}

// 添加知识文章
export function addKnowledgeArticle(data) {
  return request({
    url: '/admin/knowledge/article',
    method: 'post',
    data
  })
}

// 更新知识文章
export function updateKnowledgeArticle(data) {
  return request({
    url: '/admin/knowledge/article',
    method: 'put',
    data
  })
}

// 删除知识文章
export function deleteKnowledgeArticle(id) {
  return request({
    url: `/admin/knowledge/article/${id}`,
    method: 'delete'
  })
}

/**
 * 上传知识文章图片
 * @param {File} file 图片文件
 * @returns {Promise} 返回Promise对象
 */
export function uploadKnowledgeArticleImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/admin/knowledge/article/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 上传知识文章视频
 * @param {File} file 视频文件
 * @returns {Promise} 返回Promise对象
 */
export function uploadKnowledgeArticleVideo(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/admin/knowledge/article/upload/video',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 上传知识文章视频封面
 * @param {File} file 图片文件
 * @returns {Promise} 返回Promise对象
 */
export function uploadKnowledgeArticleVideoCover(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/admin/knowledge/article/upload/video-cover',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 获取仪表盘统计数据
 * @returns {Promise} 包含动物种类、领养状态、健康状况等统计数据
 */
export function getDashboardStats() {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
} 