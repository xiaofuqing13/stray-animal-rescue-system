import request from '@/utils/request'

/**
 * 获取动物评论列表
 */
export function getAnimalComments(animalId) {
  return request({
    url: `/user/animal/comment/${animalId}`,
    method: 'get'
  })
}

/**
 * 添加评论
 */
export function addAnimalComment(data) {
  return request({
    url: '/user/animal/comment',
    method: 'post',
    data
  })
}

/**
 * 删除评论
 */
export function deleteAnimalComment(commentId) {
  return request({
    url: `/user/animal/comment/${commentId}`,
    method: 'delete'
  })
}
