import request from '@/utils/request'

/**
 * 获取话题分页列表
 * @param {Object} params 查询参数
 * @returns {Promise<any>}
 */
export function getTopicPage(params) {
  return request({
    url: '/user/forum/topic/page',
    method: 'get',
    params
  })
}

/**
 * 获取话题详情
 * @param {number} id 话题ID
 * @returns {Promise<any>}
 */
export function getTopicDetail(id) {
  return request({
    url: `/user/forum/topic/${id}`,
    method: 'get'
  })
}

/**
 * 添加话题
 * @param {Object} data 话题数据
 * @returns {Promise<any>}
 */
export function addTopic(data) {
  return request({
    url: '/user/forum/topic',
    method: 'post',
    data
  })
}

/**
 * 删除话题
 * @param {number} id 话题ID
 * @returns {Promise<any>}
 */
export function deleteTopic(id) {
  return request({
    url: `/user/forum/topic/${id}`,
    method: 'delete'
  })
}

/**
 * 上传话题图片
 * @param {FormData} formData 包含图片的表单数据
 * @returns {Promise<any>}
 */
export function uploadTopicImage(formData) {
  return request({
    url: '/user/forum/topic/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 获取评论分页列表
 * @param {Object} params 查询参数
 * @returns {Promise<any>}
 */
export function getCommentPage(params) {
  return request({
    url: '/user/forum/comment/page',
    method: 'get',
    params
  })
}

/**
 * 获取评论回复列表
 * @param {number} commentId 评论ID
 * @returns {Promise<any>}
 */
export function getCommentReplies(commentId) {
  return request({
    url: `/user/forum/comment/replies/${commentId}`,
    method: 'get'
  })
}

/**
 * 添加评论
 * @param {Object} data 评论数据
 * @returns {Promise<any>}
 */
export function addComment(data) {
  return request({
    url: '/user/forum/comment',
    method: 'post',
    data
  })
}

/**
 * 删除评论
 * @param {number} id 评论ID
 * @returns {Promise<any>}
 */
export function deleteComment(id) {
  return request({
    url: `/user/forum/comment/${id}`,
    method: 'delete'
  })
} 