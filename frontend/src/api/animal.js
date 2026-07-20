import request from '@/utils/request'

/**
 * 获取待领养动物列表（分页）
 * @param {Object} params - 查询参数 {pageNum, pageSize, keyword, categoryId}
 * @returns {Promise}
 */
export function getAdoptableAnimals(params) {
  return request({
    url: '/user/animal/adoptable',
    method: 'get',
    params
  })
}

/**
 * 获取动物详情
 * @param {Number} id - 动物ID
 * @returns {Promise}
 */
export function getAnimalDetail(id) {
  return request({
    url: `/user/animal/${id}`,
    method: 'get'
  })
}

/**
 * 提交领养申请
 * @param {Object} data - 领养申请信息 {userId, animalId, name, phone, reason}
 * @returns {Promise}
 */
export function submitAdoptionApplication(data) {
  return request({
    url: '/user/adoption/apply',
    method: 'post',
    data
  })
}

/**
 * 获取动物分类列表
 * @returns {Promise}
 */
export function getAnimalCategories() {
  return request({
    url: '/user/animal/categories',
    method: 'get'
  })
}

/**
 * 获取我的领养申请列表
 * @returns {Promise}
 */
export function getMyAdoptions() {
  return request({
    url: '/user/adoption/my',
    method: 'get'
  })
}

/**
 * 获取领养申请详情
 * @param {Number} id - 申请ID
 * @returns {Promise}
 */
export function getAdoptionDetail(id) {
  return request({
    url: `/user/adoption/${id}`,
    method: 'get'
  })
} 