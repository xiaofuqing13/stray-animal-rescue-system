import request from '@/utils/request'

/**
 * 分页查询救助信息
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页大小
 * @param {string} params.keyword 关键词
 * @param {number} params.status 状态 0-待救助 1-救助中 2-已解决
 * @returns {Promise} 返回结果
 */
export function getRescueInfoPage(params) {
  return request({
    url: '/user/rescue/page',
    method: 'get',
    params
  })
}

/**
 * 获取救助信息详情
 * @param {number} id 救助信息ID
 * @returns {Promise} 返回结果
 */
export function getRescueInfoDetail(id) {
  return request({
    url: `/user/rescue/${id}`,
    method: 'get'
  })
}

/**
 * 添加救助信息
 * @param {Object} data 救助信息数据
 * @returns {Promise} 返回结果
 */
export function addRescueInfo(data) {
  return request({
    url: '/user/rescue',
    method: 'post',
    data
  })
}

/**
 * 上传救助图片
 * @param {FormData} formData 表单数据
 * @returns {Promise} 返回结果
 */
export function uploadRescueImage(formData) {
  return request({
    url: '/user/rescue/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取救助状态标签
export function getRescueStatusTag(status) {
  switch (status) {
    case 0:
      return { type: 'warning', label: '待救助' }
    case 1:
      return { type: 'primary', label: '救助中' }
    case 2:
      return { type: 'success', label: '已解决' }
    default:
      return { type: 'info', label: '未知状态' }
  }
}

// 获取救助状态选项
export function getRescueStatusOptions() {
  return [
    { value: 0, label: '待救助' },
    { value: 1, label: '救助中' },
    { value: 2, label: '已解决' }
  ]
}

/**
 * 更新救助信息状态
 * @param {number} id 救助信息ID
 * @param {number} status 新状态
 * @returns {Promise<any>}
 */
export function updateRescueStatus(id, status) {
  return request({
    url: `/user/rescue/status/${id}`,
    method: 'put',
    params: { status }
  })
} 