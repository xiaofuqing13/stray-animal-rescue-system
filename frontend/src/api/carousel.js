import request from '@/utils/request'

/**
 * 获取轮播图列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求Promise
 */
export function getCarouselList(params) {
  return request({
    url: '/admin/carousel/list',
    method: 'get',
    params
  })
}

/**
 * 获取轮播图详情
 * @param {number} id - 轮播图ID
 * @returns {Promise} - 请求Promise
 */
export function getCarouselDetail(id) {
  return request({
    url: `/admin/carousel/${id}`,
    method: 'get'
  })
}

/**
 * 添加轮播图
 * @param {FormData} data - 轮播图数据
 * @returns {Promise} - 请求Promise
 */
export function addCarousel(data) {
  return request({
    url: '/admin/carousel',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新轮播图
 * @param {number} id - 轮播图ID
 * @param {FormData} data - 轮播图数据
 * @returns {Promise} - 请求Promise
 */
export function updateCarousel(id, data) {
  return request({
    url: `/admin/carousel/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除轮播图
 * @param {number} id - 轮播图ID
 * @returns {Promise} - 请求Promise
 */
export function deleteCarousel(id) {
  return request({
    url: `/admin/carousel/${id}`,
    method: 'delete'
  })
}

/**
 * 更新轮播图状态
 * @param {number} id - 轮播图ID
 * @param {number} status - 状态 0-禁用 1-正常
 * @returns {Promise} - 请求Promise
 */
export function updateCarouselStatus(id, status) {
  return request({
    url: `/admin/carousel/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 获取前台展示的轮播图
 * @returns {Promise} - 请求Promise
 */
export function getActiveCarousels() {
  return request({
    url: '/carousel/active',
    method: 'get'
  })
} 