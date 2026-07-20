import request from '@/utils/request'

/**
 * 获取个性化推荐
 */
export function getPersonalizedRecommendations(limit = 8) {
  return request({
    url: '/user/recommendation/personalized',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取热门动物
 */
export function getHotAnimals(limit = 8) {
  return request({
    url: '/user/recommendation/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 点赞动物
 */
export function likeAnimal(animalId) {
  return request({
    url: `/user/recommendation/like/${animalId}`,
    method: 'post'
  })
}

/**
 * 取消点赞
 */
export function unlikeAnimal(animalId) {
  return request({
    url: `/user/recommendation/like/${animalId}`,
    method: 'delete'
  })
}

/**
 * 收藏动物
 */
export function favoriteAnimal(animalId) {
  return request({
    url: `/user/recommendation/favorite/${animalId}`,
    method: 'post'
  })
}

/**
 * 取消收藏
 */
export function unfavoriteAnimal(animalId) {
  return request({
    url: `/user/recommendation/favorite/${animalId}`,
    method: 'delete'
  })
}

/**
 * 获取动物互动状态
 */
export function getInteractionStatus(animalId) {
  return request({
    url: `/user/recommendation/interaction/${animalId}`,
    method: 'get'
  })
}
